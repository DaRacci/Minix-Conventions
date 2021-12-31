plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka")
}

val runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
val runNumberDelimiter: String? by project
val addRunNumber: String? by project
val publishComponentName: String? by project

if (addRunNumber != "false" && runNumber != null) {
    version = "$version${runNumberDelimiter ?: '.'}$runNumber"
}

java {
    withSourcesJar()
}

tasks.dokkaHtml {
    outputDirectory.set(File("${(project.parent ?: project).projectDir}/Docs"))
}

publishing {
    repositories {
        maven("https://repo.racci.dev/releases") {
            name = "RacciRepo"
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        register("maven", MavenPublication::class) {
            from(components[publishComponentName ?: "java"])
        }
    }
}
