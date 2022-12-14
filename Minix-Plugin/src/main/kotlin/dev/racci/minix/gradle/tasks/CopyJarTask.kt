package dev.racci.minix.gradle.tasks

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.orElse
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByName
import java.io.File

public open class CopyJarTask : Copy() {

    @InputFile
    public var inputFile: Option<File> = None

    @InputFile
    public var inputTask: Option<AbstractArchiveTask> = Option.catch { project.tasks.getByName<Jar>("reobfJar") }
        .orElse { Option.catch { project.tasks.getByName<Jar>("shadowJar") } }
        .orElse { Option.catch { project.tasks.getByName<Jar>("jar") } }

    @OutputDirectory
    public var outputDirectory: File = project.properties["Minix.CopyJar.Destination"]?.toString()?.let(::File) ?: error("Minix.CopyJar.Destination not set")

    /** This action is penitently dangerous. */
    @Input
    public var removeOldCopies: Boolean = false

    @TaskAction
    public fun run() {
        from(source)
        into(destinationDir)

        doLast {
            if (!removeOldCopies) return@doLast

            destinationDir.walk()
                .filter(File::isFile)
                .filter(File::canWrite)
                .filter { file -> file.name.startsWith(source.first().name.substringBefore("-")) }
                .onEach { file -> logger.info("Deleting old copy: ${file.name}") }
                .forEach(File::delete)
        }
    }

    override fun getSource(): FileCollection {
        val file = inputFile.getOrElse { inputTask.getOrElse { error("No input file or task found") }.archiveFile }
        return project.files(file)
    }

    override fun getDestinationDir(): File = this.outputDirectory

    override fun setDestinationDir(destinationDir: File) {
        this.outputDirectory = destinationDir
    }
}
