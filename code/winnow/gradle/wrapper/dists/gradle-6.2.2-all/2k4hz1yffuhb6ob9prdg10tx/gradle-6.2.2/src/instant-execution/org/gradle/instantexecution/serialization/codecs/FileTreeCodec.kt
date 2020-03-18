/*
 * Copyright 2019 the original author or authors.
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

package org.gradle.instantexecution.serialization.codecs

import org.gradle.api.file.FileVisitor
import org.gradle.api.internal.file.DefaultCompositeFileTree
import org.gradle.api.internal.file.FileCollectionInternal
import org.gradle.api.internal.file.FileCollectionStructureVisitor
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.internal.file.archive.TarFileTree
import org.gradle.api.internal.file.archive.ZipFileTree
import org.gradle.api.internal.file.collections.DirectoryFileTreeFactory
import org.gradle.api.internal.file.collections.FileBackedDirectoryFileTree
import org.gradle.api.internal.file.collections.FileTreeAdapter
import org.gradle.api.internal.file.collections.GeneratedSingletonFileTree
import org.gradle.api.internal.file.collections.MinimalFileTree
import org.gradle.api.tasks.util.PatternSet
import org.gradle.instantexecution.serialization.Codec
import org.gradle.instantexecution.serialization.ReadContext
import org.gradle.instantexecution.serialization.WriteContext
import org.gradle.instantexecution.serialization.ownerService
import org.gradle.instantexecution.serialization.readNonNull
import java.io.File


private
sealed class FileTreeSpec


private
class DirectoryTreeSpec(val file: File, val patterns: PatternSet) : FileTreeSpec()


private
class ZipTreeSpec(val file: File) : FileTreeSpec()


private
class TarTreeSpec(val file: File) : FileTreeSpec()


private
class GeneratedTreeSpec(val file: File) : FileTreeSpec()


private
class DummyFileTree(val file: File) : MinimalFileTree {
    override fun getDisplayName(): String {
        return "generated ${file.name}"
    }

    override fun visit(visitor: FileVisitor) {
        if (!file.exists()) {
            // Generate some dummy content if the file does not exist
            file.parentFile.mkdirs()
            file.writeText("")
        }
        FileBackedDirectoryFileTree(file).visit(visitor)
    }
}


internal
class FileTreeCodec(
    private val directoryFileTreeFactory: DirectoryFileTreeFactory
) : Codec<FileTreeInternal> {

    override suspend fun WriteContext.encode(value: FileTreeInternal) {
        write(fileTreeRootsOf(value))
    }

    override suspend fun ReadContext.decode(): FileTreeInternal? =
        DefaultCompositeFileTree(
            readNonNull<List<FileTreeSpec>>().map {
                when (it) {
                    is DirectoryTreeSpec -> FileTreeAdapter(directoryFileTreeFactory.create(it.file, it.patterns))
                    is GeneratedTreeSpec -> FileTreeAdapter(DummyFileTree(it.file))
                    is ZipTreeSpec -> ownerService<FileOperations>().zipTree(it.file) as FileTreeInternal
                    is TarTreeSpec -> ownerService<FileOperations>().tarTree(it.file) as FileTreeInternal
                }
            }
        )

    private
    fun fileTreeRootsOf(value: FileTreeInternal): List<FileTreeSpec> {
        val visitor = FileTreeVisitor()
        value.visitStructure(visitor)
        return visitor.roots
    }

    private
    class FileTreeVisitor : FileCollectionStructureVisitor {

        internal
        var roots = mutableListOf<FileTreeSpec>()

        override fun visitCollection(source: FileCollectionInternal.Source, contents: Iterable<File>) = throw UnsupportedOperationException()

        override fun visitGenericFileTree(fileTree: FileTreeInternal) = throw UnsupportedOperationException()

        override fun visitFileTree(root: File, patterns: PatternSet, fileTree: FileTreeInternal) {
            if (fileTree is FileTreeAdapter) {
                val tree = fileTree.tree
                if (tree is GeneratedSingletonFileTree) {
                    // TODO - should generate the file into some persistent cache dir (eg the instant execution cache dir) and/or persist enough of the generator to recreate the file
                    // For example, for the Jar task persist the effective manifest (not all the stuff that produces it) and an action bean to generate the file from this
                    roots.add(GeneratedTreeSpec(tree.file))
                    return
                }
            }
            roots.add(DirectoryTreeSpec(root, patterns))
        }

        override fun visitFileTreeBackedByFile(file: File, fileTree: FileTreeInternal) {
            if (fileTree is FileTreeAdapter) {
                val tree = fileTree.tree
                if (tree is ZipFileTree) {
                    roots.add(ZipTreeSpec(tree.backingFile!!))
                    return
                } else if (tree is TarFileTree) {
                    roots.add(TarTreeSpec(tree.backingFile!!))
                    return
                }
            }
            throw UnsupportedOperationException()
        }
    }
}
