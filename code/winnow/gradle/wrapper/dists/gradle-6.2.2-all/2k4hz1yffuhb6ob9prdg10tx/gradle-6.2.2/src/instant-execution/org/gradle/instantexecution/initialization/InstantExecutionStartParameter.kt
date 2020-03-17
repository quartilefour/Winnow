/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.instantexecution.initialization

import org.gradle.StartParameter
import org.gradle.initialization.layout.BuildLayout
import java.io.File


class InstantExecutionStartParameter(
    private val buildLayout: BuildLayout,
    private val startParameter: StartParameter
) {

    val rootDirectory: File
        get() = buildLayout.rootDirectory

    val invocationDir: File
        get() = startParameter.currentDir

    val isRefreshDependencies
        get() = startParameter.isRefreshDependencies

    val requestedTaskNames: List<String> by lazy(LazyThreadSafetyMode.NONE) {
        startParameter.taskNames
    }

    val excludedTaskNames: Set<String>
        get() = startParameter.excludedTaskNames

    fun systemPropertyArg(propertyName: String): String? =
        startParameter.systemPropertiesArgs[propertyName]
}
