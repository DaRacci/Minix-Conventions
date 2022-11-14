package dev.racci.minix.gradle.tasks

import org.gradle.api.DefaultTask

class QuickBuildTask : DefaultTask() {
    init {
        group = "build"
        description = "Builds the project with minimal tasks (No testing or documentation, etc.)"

        dependsOn(project.tasks.findByPath("compileKotlin"))
        dependsOn(project.tasks.findByName("shadowJar"))
        dependsOn(project.tasks.findByName("reobfJar"))
        dependsOn(project.tasks.findByName("copyJar"))
    }
}
