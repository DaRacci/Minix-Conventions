package dev.racci.minix.gradle.tasks

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import java.io.File

open class CopyJarTask : Copy() {

    @InputFile
    val inputFile: java.util.Optional<File> = java.util.Optional.empty()

    @OutputDirectory
    val outputDirectory: java.util.Optional<File> = java.util.Optional.ofNullable(
        project.properties["Minix.CopyJar.Destination"]?.toString()?.let(::File)
    )

    // @TaskAction
    // fun run() {
    //
    //     // val file = this.getFile()
    //     // if (file == null) {
    //     //     logger.error("No input file found for copyJar task.")
    //     //     return
    //     // }
    //     //
    //     // val destination = this.getDestination(file)
    //     // if (destination == null) {
    //     //     logger.error("No destination directory found for copyJar task.")
    //     //     return
    //     // }
    //     val destination = this.destinationDir
    //
    //     if (!destination.exists()) {
    //         try {
    //             destination.ensureParentDirsCreated()
    //         } catch (e: IOException) {
    //             return logger.error("Could not create destination directory $destination", e)
    //         }
    //     }
    //
    //     // destination.walk().forEach {
    //     //     if (!it.isFile) return@forEach
    //     //     if (!it.canWrite()) return@forEach
    //     //     if (!it.name.startsWith(file.name.substringBefore('-'))) return@forEach
    //     //
    //     //     it.delete()
    //     // }
    //
    //     // try {
    //     //     file.copyTo(destination)
    //     // } catch (e: IOException) {
    //     //     logger.error("Could not copy $file to $destination", e)
    //     // }
    // }

    override fun getSource(): FileCollection {
        val file = this.inputFile
            .filter { file -> file.exists() }
            .orElseGet {
                project.tasks.findByName("reobfJar")?.outputs?.files?.singleFile?.also { file ->
                    logger.info("Using reobfJar output as input for copyJar: $file")
                    return@orElseGet file
                }

                project.tasks.findByName("shadowJar")?.outputs?.files?.singleFile?.also { file ->
                    logger.info("Using shadowJar output as input for copyJar: $file")
                    return@orElseGet file
                }

                null
            }

        return project.files(file)
    }

    override fun getDestinationDir(): File = this.outputDirectory.get()
}
