plugins {
    `version-catalog`
    id("dev.racci.minix.publication")
}

minixPublishing.publishComponentName = "versionCatalog"

catalog {
    versionCatalog {
        version("version", version.toString())

        fun minixPlugin(file: File) {
            val removedExtension = file.name.removeSuffix(".gradle.kts")
            val scriptName = removedExtension.substringAfter("dev.racci.minix.")

            plugin("minix-$scriptName", removedExtension).version(version.toString())
        }

        gradle.includedBuilds.find { it.name == "Minix-Gradle" }!!.projectDir.resolve("src/main/kotlin").listFiles { file ->
            file.isFile
        }?.forEach { file -> minixPlugin(file) }

        from(files("../gradle/libs.versions.toml"))
    }
}
