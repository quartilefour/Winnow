/*
 * Copyright 2017 the original author or authors.
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

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;
import org.gradle.internal.Cast;
import org.gradle.internal.DisplayName;

import javax.annotation.Nullable;

public class Providers {
    private static final NoValueProvider<Object> NULL_PROVIDER = new NoValueProvider<>(ValueSupplier.Value.MISSING);

    public static final Provider<Boolean> TRUE = of(true);
    public static final Provider<Boolean> FALSE = of(false);

    public static <T> ProviderInternal<T> fixedValue(DisplayName owner, T value, Class<T> targetType, ValueSanitizer<T> sanitizer) {
        value = sanitizer.sanitize(value);
        if (!targetType.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Cannot set the value of %s of type %s using an instance of type %s.", owner.getDisplayName(), targetType.getName(), value.getClass().getName()));
        }
        return new FixedValueProvider<>(value);
    }

    public static <T> ProviderInternal<T> nullableValue(ValueSupplier.Value<? extends T> value) {
        if (value.isMissing()) {
            if (value.getPathToOrigin().isEmpty()) {
                return notDefined();
            } else {
                return new NoValueProvider<>(value);
            }
        } else {
            return of(value.get());
        }
    }

    public static <T> ProviderInternal<T> notDefined() {
        return Cast.uncheckedCast(NULL_PROVIDER);
    }

    public static <T> ProviderInternal<T> of(T value) {
        return new FixedValueProvider<>(value);
    }

    public static <T> ProviderInternal<T> internal(final Provider<T> value) {
        return Cast.uncheckedCast(value);
    }

    public static <T> ProviderInternal<T> ofNullable(@Nullable T value) {
        if (value == null) {
            return notDefined();
        } else {
            return of(value);
        }
    }

    public static class FixedValueProvider<T> extends AbstractProviderWithValue<T> {
        private final T value;

        FixedValueProvider(T value) {
            this.value = value;
        }

        @Nullable
        @Override
        public Class<T> getType() {
            return Cast.uncheckedCast(value.getClass());
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public ProviderInternal<T> withFinalValue() {
            return this;
        }

        @Override
        public String toString() {
            return String.format("fixed(%s, %s)", getType(), value);
        }
    }

    private static class NoValueProvider<T> extends AbstractMinimalProvider<T> {
        private final Value<? extends T> value;

        public NoValueProvider(Value<? extends T> value) {
            assert value.isMissing();
            this.value = value;
        }

        @Override
        public Value<? extends T> calculateValue() {
            return value;
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Nullable
        @Override
        public Class<T> getType() {
            return null;
        }

        @Override
        protected Value<T> calculateOwnValue() {
            return Value.missing();
        }

        @Override
        public <S> ProviderInternal<S> map(Transformer<? extends S, ? super T> transformer) {
            return Cast.uncheckedCast(this);
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public ProviderInternal<T> asSupplier(DisplayName owner, Class<? super T> targetType, ValueSanitizer<? super T> sanitizer) {
            return this;
        }

        @Override
        public ProviderInternal<T> withFinalValue() {
            return this;
        }

        @Override
        public Provider<T> orElse(T value) {
            return Providers.of(value);
        }

        @Override
        public Provider<T> orElse(Provider<? extends T> provider) {
            return Cast.uncheckedCast(provider);
        }

        @Override
        public String toString() {
            return "undefined";
        }
    }
}
