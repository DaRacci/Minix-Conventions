import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `version-catalog`
    id("dev.racci.minix.publication")
}

minixPublishing.publishComponentName = "versionCatalog"

catalog {
    versionCatalog {
        version("minix-scripts", version.toString())

        fun minixPlugin(file: File) {
            val removedExtension = file.name.removeSuffix(".gradle.kts")
            val scriptName = removedExtension.substringAfter("dev.racci.minix.")

            plugin("minix-$scriptName", removedExtension).versionRef("minix-scripts")
        }

        gradle.includedBuilds.find { it.name == "Minix-Gradle" }!!.projectDir.resolve("src/main/kotlin").listFiles { file ->
            file.isFile
        }?.forEach { file -> minixPlugin(file) }

        gradle.includedBuilds.find { it.name == "minix-plugin" }!!.also { gradlePlugin ->
            val properties = loadProperties(gradlePlugin.projectDir.resolve("gradle.properties").absolutePath)
            val version = properties["version"] as String

            version("minix-plugin", version)
            plugin("minix", "dev.racci.minix").versionRef("minix-plugin")
        }

        from(files("../gradle/libs.versions.toml"))
    }
}
