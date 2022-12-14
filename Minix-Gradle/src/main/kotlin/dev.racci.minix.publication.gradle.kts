import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaTask

val Project.trueRoot: Project
    get() {
        var root = this
        while (root.parent != null) {
            root = root.parent!!
        }
        return root
    }

plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka")
}

val trueRoot = project.trueRoot
if (trueRoot == project) {
    allprojects {
        (tasks.findByName("dokkaHtml") as? DokkaTask)?.apply {
            outputDirectory.set(trueRoot.rootDir.resolve("docs"))
            dokkaSourceSets.configureEach {
                includeNonPublic.set(false)
                skipEmptyPackages.set(true)
                reportUndocumented.set(true)
                displayName.set(trueRoot.name)
                platform.set(Platform.jvm)
                jdkVersion.set(17)
            }
        }
    }
}

// tasks.withType<DokkaMultiModuleTask> { outputDirectory.set(File("$rootDir/docs")) }

val runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
val runNumberDelimiter: String? by project
val addRunNumber: String? by project
val publishComponentName: String? by project

if (addRunNumber != "false" && runNumber != null) {
    version = "$version${runNumberDelimiter ?: '.'}$runNumber"
}

java.withSourcesJar()

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
