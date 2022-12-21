import gradle.kotlin.dsl.accessors._ca59d7b33a587bae1dcf00e1f22a5064.java
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

val publishingExtension = extensions.create<MinixPublishingExtension>("minixPublishing", project)

val trueRoot = project.trueRoot
if (trueRoot == project) {
    allprojects {
        (tasks.findByName("dokkaHtml") as? DokkaTask)?.apply {
            outputDirectory.set(trueRoot.projectDir.resolve("docs"))
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

plugins {
    java
    id("org.jetbrains.dokka")
}

java.withSourcesJar()

fun isSnapshot(version: String): Boolean = publishingExtension.runNumber == null || version.endsWith("SNAPSHOT")

afterEvaluate {
    if (publishingExtension.addRunNumber && publishingExtension.runNumber != null) {
        version = "$version${publishingExtension.runNumberDelimiter}${publishingExtension.runNumber}"
    }

    if (publishingExtension.noPublishing) return@afterEvaluate

    apply<MavenPublishPlugin>()

    extensions.configure<PublishingExtension> {
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
    @Input
    @Internal
    var noPublishing: Boolean = false

    @get:Input
    @get:Optional
    var runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

    @Input
    var runNumberDelimiter: String = target.properties["runNumberDelimiter"]?.toString() ?: "."

    @Input
    var addRunNumber: Boolean = target.properties["addRunNumber"]?.toString().toBoolean()

    @Input
    var publishComponentName: String = target.properties["publishComponentName"]?.toString() ?: "java"
}
