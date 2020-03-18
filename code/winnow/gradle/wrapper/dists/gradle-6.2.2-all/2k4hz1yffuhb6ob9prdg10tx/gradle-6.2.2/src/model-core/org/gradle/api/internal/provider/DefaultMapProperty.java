/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.provider;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Provider;
import org.gradle.internal.Cast;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultMapProperty<K, V> extends AbstractProperty<Map<K, V>> implements MapProperty<K, V>, MapProviderInternal<K, V> {
    private static final String NULL_KEY_FORBIDDEN_MESSAGE = String.format("Cannot add an entry with a null key to a property of type %s.", Map.class.getSimpleName());
    private static final String NULL_VALUE_FORBIDDEN_MESSAGE = String.format("Cannot add an entry with a null value to a property of type %s.", Map.class.getSimpleName());

    private static final MapSupplier<Object, Object> NO_VALUE = new NoValueSupplier<>(Value.missing());

    private final Class<K> keyType;
    private final Class<V> valueType;
    private final ValueCollector<K> keyCollector;
    private final MapEntryCollector<K, V> entryCollector;
    private MapSupplier<K, V> convention = noValueSupplier();
    private MapSupplier<K, V> defaultValue = emptySupplier();
    private MapSupplier<K, V> value;

    public DefaultMapProperty(Class<K> keyType, Class<V> valueType) {
        applyDefaultValue();
        this.keyType = keyType;
        this.valueType = valueType;
        keyCollector = new ValidatingValueCollector<>(Set.class, keyType, ValueSanitizers.forType(keyType));
        entryCollector = new ValidatingMapEntryCollector<>(keyType, valueType, ValueSanitizers.forType(keyType), ValueSanitizers.forType(valueType));
    }

    private MapSupplier<K, V> emptySupplier() {
        return new EmptySupplier();
    }

    private MapSupplier<K, V> noValueSupplier() {
        return Cast.uncheckedCast(NO_VALUE);
    }

    @Override
    protected ValueSupplier getSupplier() {
        return value;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Class<Map<K, V>> getType() {
        return (Class) Map.class;
    }

    @Override
    public Class<K> getKeyType() {
        return keyType;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

    @Override
    public Class<?> publicType() {
        return MapProperty.class;
    }

    @Override
    public int getFactoryId() {
        return ManagedFactories.MapPropertyManagedFactory.FACTORY_ID;
    }

    @Override
    public boolean isPresent() {
        beforeRead();
        return value.isPresent();
    }

    @Override
    protected Value<? extends Map<K, V>> calculateOwnValue() {
        beforeRead();
        return doCalculateOwnValue();
    }

    @NotNull
    private Value<? extends Map<K, V>> doCalculateOwnValue() {
        return value.calculateValue();
    }

    @Override
    public Provider<V> getting(final K key) {
        return new EntryProvider(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapProperty<K, V> empty() {
        if (beforeMutate()) {
            set(emptySupplier());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromAnyValue(@Nullable Object object) {
        if (object == null || object instanceof Map<?, ?>) {
            set((Map) object);
        } else if (object instanceof Provider<?>) {
            set((Provider) object);
        } else {
            throw new IllegalArgumentException(String.format(
                "Cannot set the value of a property of type %s using an instance of type %s.", Map.class.getName(), object.getClass().getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(@Nullable Map<? extends K, ? extends V> entries) {
        if (entries == null) {
            if (beforeReset()) {
                set(convention);
                defaultValue = noValueSupplier();
            }
            return;
        }
        if (beforeMutate()) {
            set(new CollectingSupplier(new MapCollectors.EntriesFromMap<>(entries)));
        }
    }

    @Override
    public void set(Provider<? extends Map<? extends K, ? extends V>> provider) {
        if (!beforeMutate()) {
            return;
        }
        ProviderInternal<? extends Map<? extends K, ? extends V>> p = checkMapProvider(provider);
        set(new CollectingSupplier(new MapCollectors.EntriesFromMapProvider<>(p)));
    }

    @Override
    public MapProperty<K, V> value(@Nullable Map<? extends K, ? extends V> entries) {
        set(entries);
        return this;
    }

    @Override
    public MapProperty<K, V> value(Provider<? extends Map<? extends K, ? extends V>> provider) {
        set(provider);
        return this;
    }

    private void set(MapSupplier<K, V> supplier) {
        value = supplier;
    }

    @Override
    public void put(K key, V value) {
        Preconditions.checkNotNull(key, NULL_KEY_FORBIDDEN_MESSAGE);
        Preconditions.checkNotNull(value, NULL_VALUE_FORBIDDEN_MESSAGE);
        if (!beforeMutate()) {
            return;
        }
        addCollector(new MapCollectors.SingleEntry<>(key, value));
    }

    @Override
    public void put(K key, Provider<? extends V> providerOfValue) {
        Preconditions.checkNotNull(key, NULL_KEY_FORBIDDEN_MESSAGE);
        Preconditions.checkNotNull(providerOfValue, NULL_VALUE_FORBIDDEN_MESSAGE);
        if (!beforeMutate()) {
            return;
        }
        ProviderInternal<? extends V> p = Providers.internal(providerOfValue);
        if (p.getType() != null && !valueType.isAssignableFrom(p.getType())) {
            throw new IllegalArgumentException(String.format("Cannot add an entry to a property of type %s with values of type %s using a provider of type %s.",
                Map.class.getName(), valueType.getName(), p.getType().getName()));
        }
        addCollector(new MapCollectors.EntryWithValueFromProvider<>(key, p));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        if (!beforeMutate()) {
            return;
        }
        addCollector(new MapCollectors.EntriesFromMap<>(entries));
    }

    @Override
    public void putAll(Provider<? extends Map<? extends K, ? extends V>> provider) {
        if (!beforeMutate()) {
            return;
        }
        ProviderInternal<? extends Map<? extends K, ? extends V>> p = checkMapProvider(provider);
        addCollector(new MapCollectors.EntriesFromMapProvider<>(p));
    }

    private void addCollector(MapCollector<K, V> collector) {
        value = value.plus(collector);
    }

    @SuppressWarnings("unchecked")
    private ProviderInternal<? extends Map<? extends K, ? extends V>> checkMapProvider(@Nullable Provider<? extends Map<? extends K, ? extends V>> provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Cannot set the value of a property using a null provider.");
        }
        ProviderInternal<? extends Map<? extends K, ? extends V>> p = Providers.internal(provider);
        if (p.getType() != null && !Map.class.isAssignableFrom(p.getType())) {
            throw new IllegalArgumentException(String.format("Cannot set the value of a property of type %s using a provider of type %s.",
                Map.class.getName(), p.getType().getName()));
        }
        if (p instanceof MapProviderInternal) {
            Class<? extends K> providerKeyType = ((MapProviderInternal<? extends K, ? extends V>) p).getKeyType();
            Class<? extends V> providerValueType = ((MapProviderInternal<? extends K, ? extends V>) p).getValueType();
            if (!keyType.isAssignableFrom(providerKeyType) || !valueType.isAssignableFrom(providerValueType)) {
                throw new IllegalArgumentException(String.format("Cannot set the value of a property of type %s with key type %s and value type %s " +
                        "using a provider with key type %s and value type %s.", Map.class.getName(), keyType.getName(), valueType.getName(),
                    providerKeyType.getName(), providerValueType.getName()));
            }
        }
        return p;
    }

    @Override
    public MapProperty<K, V> convention(@Nullable Map<? extends K, ? extends V> value) {
        if (value == null) {
            convention(noValueSupplier());
        } else {
            convention(new CollectingSupplier(new MapCollectors.EntriesFromMap<>(value)));
        }
        return this;
    }

    @Override
    public MapProperty<K, V> convention(Provider<? extends Map<? extends K, ? extends V>> valueProvider) {
        convention(new CollectingSupplier(new MapCollectors.EntriesFromMapProvider<>(Providers.internal(valueProvider))));
        return this;
    }

    private void convention(MapSupplier<K, V> supplier) {
        if (shouldApplyConvention()) {
            this.value = supplier;
        }
        this.convention = supplier;
    }

    public List<? extends ProviderInternal<? extends Map<? extends K, ? extends V>>> getProviders() {
        List<ProviderInternal<? extends Map<? extends K, ? extends V>>> providers = new ArrayList<>();
        value.visit(providers);
        return providers;
    }

    public void providers(List<? extends ProviderInternal<? extends Map<? extends K, ? extends V>>> providers) {
        if (!beforeMutate()) {
            return;
        }
        value = defaultValue;
        for (ProviderInternal<? extends Map<? extends K, ? extends V>> provider : providers) {
            value = value.plus(new MapCollectors.EntriesFromMapProvider<>(provider));
        }
    }

    @Override
    public Provider<Set<K>> keySet() {
        return new KeySetProvider();
    }

    @Override
    protected String describeContents() {
        return String.format("Map(%s->%s, %s)", keyType.getSimpleName().toLowerCase(), valueType.getSimpleName(), value.toString());
    }

    @Override
    protected void applyDefaultValue() {
        value = defaultValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void makeFinal() {
        Value<? extends Map<K, V>> value = doCalculateOwnValue();
        if (!value.isMissing()) {
            Map<K, V> entries = value.get();
            set(new FixedSuppler<>(entries));
        } else if (value.getPathToOrigin().isEmpty()) {
            set(noValueSupplier());
        } else {
            set(new NoValueSupplier<>(value));
        }
        convention = noValueSupplier();
    }

    private class EntryProvider extends AbstractMinimalProvider<V> {
        private final K key;

        public EntryProvider(K key) {
            this.key = key;
        }

        @Nullable
        @Override
        public Class<V> getType() {
            return valueType;
        }

        @Override
        protected Value<? extends V> calculateOwnValue() {
            beforeRead();
            Value<? extends Map<K, V>> result = doCalculateOwnValue();
            if (result.isMissing()) {
                return result.asType();
            }
            return Value.ofNullable(result.get().get(key));
        }
    }

    private class KeySetProvider extends AbstractMinimalProvider<Set<K>> {
        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public Class<Set<K>> getType() {
            return (Class) Set.class;
        }

        @Override
        protected Value<? extends Set<K>> calculateOwnValue() {
            beforeRead();
            return value.calculateKeys();
        }
    }

    private interface MapSupplier<K, V> extends ValueSupplier {
        Value<? extends Map<K, V>> calculateValue();

        Value<? extends Set<K>> calculateKeys();

        MapSupplier<K, V> plus(MapCollector<K, V> collector);

        void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources);
    }

    public static class NoValueSupplier<K, V> implements MapSupplier<K, V> {
        private final Value<? extends Map<K, V>> value;

        public NoValueSupplier(Value<? extends Map<K, V>> value) {
            this.value = value.asType();
            assert value.isMissing();
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Value<? extends Map<K, V>> calculateValue() {
            return value;
        }

        @Override
        public Value<? extends Set<K>> calculateKeys() {
            return value.asType();
        }

        @Override
        public MapSupplier<K, V> plus(MapCollector<K, V> collector) {
            // nothing + something = nothing
            return this;
        }

        @Override
        public void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources) {
        }

        @Override
        public boolean maybeVisitBuildDependencies(TaskDependencyResolveContext context) {
            return true;
        }

        @Override
        public void visitProducerTasks(Action<? super Task> visitor) {
        }

        @Override
        public boolean isValueProducedByTask() {
            return false;
        }
    }

    public class EmptySupplier implements MapSupplier<K, V> {
        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public Value<? extends Map<K, V>> calculateValue() {
            return Value.of(ImmutableMap.of());
        }

        @Override
        public Value<? extends Set<K>> calculateKeys() {
            return Value.of(ImmutableSet.of());
        }

        @Override
        public MapSupplier<K, V> plus(MapCollector<K, V> collector) {
            // empty + something = something
            return new CollectingSupplier(collector);
        }

        @Override
        public void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources) {
        }

        @Override
        public boolean maybeVisitBuildDependencies(TaskDependencyResolveContext context) {
            return true;
        }

        @Override
        public void visitProducerTasks(Action<? super Task> visitor) {
        }

        @Override
        public boolean isValueProducedByTask() {
            return false;
        }
    }

    private static class FixedSuppler<K, V> implements MapSupplier<K, V> {
        private final Map<K, V> entries;

        public FixedSuppler(Map<K, V> entries) {
            this.entries = entries;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public Value<? extends Map<K, V>> calculateValue() {
            return Value.of(entries);
        }

        @Override
        public Value<? extends Set<K>> calculateKeys() {
            return Value.of(entries.keySet());
        }

        @Override
        public MapSupplier<K, V> plus(MapCollector<K, V> collector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources) {
            sources.add(Providers.of(entries));
        }

        @Override
        public boolean isValueProducedByTask() {
            return false;
        }

        @Override
        public void visitProducerTasks(Action<? super Task> visitor) {
        }

        @Override
        public boolean maybeVisitBuildDependencies(TaskDependencyResolveContext context) {
            return true;
        }
    }

    private class CollectingSupplier implements MapSupplier<K, V> {
        private final MapCollector<K, V> collector;

        public CollectingSupplier(MapCollector<K, V> collector) {
            this.collector = collector;
        }

        @Override
        public boolean isPresent() {
            return collector.isPresent();
        }

        @Override
        public Value<? extends Set<K>> calculateKeys() {
            // TODO - don't make a copy when the collector already produces an immutable collection
            ImmutableSet.Builder<K> builder = ImmutableSet.builder();
            Value<Void> result = collector.collectKeys(keyCollector, builder);
            if (result.isMissing()) {
                return result.asType();
            }
            return Value.of(ImmutableSet.copyOf(builder.build()));
        }

        @Override
        public Value<? extends Map<K, V>> calculateValue() {
            // TODO - don't make a copy when the collector already produces an immutable collection
            // Cannot use ImmutableMap.Builder here, as it does not allow multiple entries with the same key, however the contract
            // for MapProperty allows a provider to override the entries of earlier providers and so there can be multiple entries
            // with the same key
            Map<K, V> entries = new LinkedHashMap<>();
            Value<Void> result = collector.collectEntries(entryCollector, entries);
            if (result.isMissing()) {
                return result.asType();
            }
            return Value.of(ImmutableMap.copyOf(entries));
        }

        @Override
        public MapSupplier<K, V> plus(MapCollector<K, V> collector) {
            return new CollectingSupplier(new PlusCollector<>(this.collector, collector));
        }

        @Override
        public void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources) {
            collector.visit(sources);
        }

        @Override
        public boolean isValueProducedByTask() {
            return collector.isValueProducedByTask();
        }

        @Override
        public boolean maybeVisitBuildDependencies(TaskDependencyResolveContext context) {
            return collector.maybeVisitBuildDependencies(context);
        }

        @Override
        public void visitProducerTasks(Action<? super Task> visitor) {
            collector.visitProducerTasks(visitor);
        }
    }

    private static class PlusCollector<K, V> implements MapCollector<K, V> {
        private final MapCollector<K, V> left;
        private final MapCollector<K, V> right;

        public PlusCollector(MapCollector<K, V> left, MapCollector<K, V> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isPresent() {
            return left.isPresent() && right.isPresent();
        }

        @Override
        public Value<Void> collectEntries(MapEntryCollector<K, V> collector, Map<K, V> dest) {
            Value<Void> result = left.collectEntries(collector, dest);
            if (result.isMissing()) {
                return result;
            }
            return right.collectEntries(collector, dest);
        }

        @Override
        public Value<Void> collectKeys(ValueCollector<K> collector, ImmutableCollection.Builder<K> dest) {
            Value<Void> result = left.collectKeys(collector, dest);
            if (result.isMissing()) {
                return result;
            }
            return right.collectKeys(collector, dest);
        }

        @Override
        public void visit(List<ProviderInternal<? extends Map<? extends K, ? extends V>>> sources) {
            left.visit(sources);
            right.visit(sources);
        }

        @Override
        public boolean maybeVisitBuildDependencies(TaskDependencyResolveContext context) {
            if (left.maybeVisitBuildDependencies(context)) {
                return right.maybeVisitBuildDependencies(context);
            }
            return false;
        }

        @Override
        public void visitProducerTasks(Action<? super Task> visitor) {
            left.visitProducerTasks(visitor);
            right.visitProducerTasks(visitor);
        }

        @Override
        public boolean isValueProducedByTask() {
            return left.isValueProducedByTask() || right.isValueProducedByTask();
        }
    }
}
