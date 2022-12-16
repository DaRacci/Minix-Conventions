plugins {
    `version-catalog`
    `maven-publish`
}

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

version = "$kotlinVersion-$runNumber"

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
        }.forEach { file -> minixPlugin(file) }

        from(files("../gradle/libs.versions.toml"))
    }
}

publishing {
    repositories.maven("https://repo.racci.dev/releases") {
        name = "RacciRepo"
        credentials(PasswordCredentials::class)
    }

    publications.create<MavenPublication>("maven") {
        artifactId = "catalog"
        from(components["versionCatalog"])
    }
}
