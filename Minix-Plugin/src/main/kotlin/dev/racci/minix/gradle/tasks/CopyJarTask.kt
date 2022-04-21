package dev.racci.minix.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.IOException

open class CopyJarTask : DefaultTask() {

    @Optional
    @get:InputDirectory
    var destinationDir: String? = null

    @Optional
    @get:InputFile
    val inputFile: RegularFileProperty? = null

    @TaskAction
    fun run() {
        if (System.getenv("CI") == "true") {
            return logger.log(LogLevel.INFO, "Skipping copyJar task")
        }

        val file = when {
            inputFile != null && inputFile!!.isPresent && inputFile!!.orNull?.asFile?.exists() == true -> inputFile!!.asFile.get()
            project.tasks.findByName("shadowJar") != null -> project.tasks.getByName("shadowJar").outputs.files.singleFile
            project.tasks.findByName("reobfJar") != null -> project.tasks.getByName("reobfJar").outputs.files.singleFile
            else -> {
                logger.log(LogLevel.ERROR, "No input file found for copyJar task")
                return
            }
        }

        val dir = destinationDir ?: project.properties["destinationDir"] as? String ?: return logger.log(
            LogLevel.ERROR,
            "No destination directory found for copyJar task"
        )
        val destination = File(dir, file.name)

        if (!destination.exists()) {
            try {
                destination.ensureParentDirsCreated()
            } catch (e: IOException) {
                return logger.log(LogLevel.ERROR, "Could not create destination directory $destination", e)
            }
        }

        try {
            file.copyTo(destination)
        } catch (e: IOException) {
            logger.log(LogLevel.ERROR, "Could not copy $file to $destination", e)
        }
    }
}
