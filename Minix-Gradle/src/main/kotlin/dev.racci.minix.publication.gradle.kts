import gradle.kotlin.dsl.accessors._6f77941ef037b0b91092ede508229a8e.java
import gradle.kotlin.dsl.accessors._6f77941ef037b0b91092ede508229a8e.publishing
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

val publishingExtension = extensions.create<MinixPublishingExtension>("minixPublishing", project)

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

java.withSourcesJar()

if (publishingExtension.addRunNumber && publishingExtension.runNumber != null) {
    version = "$version${publishingExtension.runNumberDelimiter}${publishingExtension.runNumber}"
}

fun isSnapshot(version: String): Boolean = publishingExtension.runNumber == null || version.endsWith("SNAPSHOT")

afterEvaluate {
    publishing {
        repositories {
            maven("https://repo.racci.dev/") {
                url = if (isSnapshot(version.toString())) {
                    url.resolve("snapshots")
                } else url.resolve("releases")

                name = "RacciRepo"
                credentials(PasswordCredentials::class)
            }
        }
        publications {
            register("maven", MavenPublication::class) {
                from(components[publishingExtension.publishComponentName])
                if (isSnapshot(version) && !version.endsWith("-SNAPSHOT")) {
                    version = "$version-SNAPSHOT"
                }
            }
        }
    }
}

open class MinixPublishingExtension(
    target: Project
) {
    @get:Input
    @get:Optional
    val runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

    @Input
    var runNumberDelimiter: String = target.properties["runNumberDelimiter"]?.toString() ?: "."

    @Input
    var addRunNumber: Boolean = target.properties["addRunNumber"]?.toString().toBoolean()

    @Input
    var publishComponentName: String = target.properties["publishComponentName"]?.toString() ?: "java"
}
