plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka")
}

project.beforeEvaluate {
    val lib = configurations.create("lib")
    plugins.withType<JavaPlugin> {
        extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            configurations.getByName(compileClasspathConfigurationName).extendsFrom(lib)
            configurations.getByName(runtimeClasspathConfigurationName).extendsFrom(lib)
        }
    }
}

val runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
val runNumberDelimiter: String? by project
val addRunNumber: String? by project
val publishComponentName: String? by project

if (addRunNumber != "false" && runNumber != null) {
    version = "$version${runNumberDelimiter ?: '.'}$runNumber"
}

java.withSourcesJar()

tasks.dokkaHtml {
    outputDirectory.set(file("/docs"))
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
