plugins {
    `version-catalog`
    `maven-publish`
}
//
// java.util.Properties()
//    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
//    .forEach { key, value -> project.ext["$key"] = value }

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

version = "$kotlinVersion-$runNumber"

catalog {
    versionCatalog {
        version("version", version.toString())

        fun minixPlugin(string: String) {
            val id = buildString {
                append("dev.racci.minix.")
                append(string)
            }

            println("Adding plugin $id")

            plugin("minix-$string", id).version(version.toString())
        }

        minixPlugin("nms")
        minixPlugin("kotlin")
        minixPlugin("copyJar")
        minixPlugin("purpurmc")
        minixPlugin("publication")

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

tasks.create("build") {
    dependsOn("publishToMavenLocal")
}
