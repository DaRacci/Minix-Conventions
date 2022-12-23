
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory.SubDependencyFactory
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.jetbrains.kotlin.utils.addToStdlib.cast
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

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

tasks.create("validate-dependencies") {
    group = "verification"
    description = "Validates that declared dependencies are valid."

    dependsOn(tasks.generateCatalogAsToml) // Verify valid toml

    doLast {
        fun <T : KCallable<*>, R> T.access(fn: T.() -> R): R {
            val originalState = this.isAccessible
            this.isAccessible = true
            val value = fn()
            this.isAccessible = originalState
            return value
        }

        repositories {
            fun forVerify(
                url: String,
                extraConfig: MavenArtifactRepository.() -> Unit = {}
            ) = maven(url) {
                mavenContent { onlyForConfigurations("resolvingConfiguration") }
                extraConfig()
            }

            mavenCentral {
                mavenContent { onlyForConfigurations("resolvingConfiguration") }
            }
            gradlePluginPortal {
                content { onlyForConfigurations("resolvingConfiguration") }
            }
            forVerify("https://repo.racci.dev/releases") {
                mavenContent {
                    releasesOnly()
                    includeGroupByRegex("dev\\.racci.*")
                }
            }
            forVerify("https://papermc.io/repo/repository/maven-public/") {
                mavenContent {
                    releasesOnly()
                    includeGroupByRegex("io\\.papermc.*")
                    includeModule("com.mojang", "authlib")
                }
            }
            forVerify("https://oss.sonatype.org/content/repositories/snapshots/") {
                mavenContent { snapshotsOnly(); includeGroup("org.incendo.interfaces") }
            }
            forVerify("https://repo.opencollab.dev/maven-snapshots/") {
                mavenContent { includeGroup("org.geysermc.floodgate") }
            }
            forVerify("https://repo.md-5.net/content/groups/public/") {
                mavenContent { includeGroup("LibsDisguises") }
            }
            forVerify("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
                mavenContent { includeGroup("me.clip") }
            }
            forVerify("https://repo.dmulloy2.net/repository/public/") {
                mavenContent { includeGroup("com.comphenix.protocol") }
            }
            forVerify("https://repo.fvdh.dev/releases") {
                mavenContent { includeGroup("net.frankheijden.serverutils") }
            }
            forVerify("https://jitpack.io") {
                mavenContent {
                    includeGroup("com.willfp")
                    includeModule("com.github.angeschossen", "LandsAPI")
                    includeModule("com.github.LoneDev6", "api-itemsadder")
                    includeModule("com.github.BeYkeRYkt.LightAPI", "lightapi-bukkit-common")
                }
            }
        }

        val libraryDependencies = mutableSetOf<Provider<MinimalExternalModuleDependency>>()
        val factoryQueue = LibrariesForLibs::class.declaredMemberProperties
            .map { it.access { getter.call(libs) } }
            .filterIsInstance<SubDependencyFactory>()
            .toCollection(ArrayDeque())

        do {
            val subFactory = factoryQueue.removeFirst()
            subFactory::class.declaredFunctions.forEach { function ->
                when (val returnedValue = function.access { call(subFactory) }) {
                    is SubDependencyFactory -> factoryQueue.addLast(returnedValue)
                    is Provider<*> -> libraryDependencies.add(returnedValue.cast())
                }
            }
        } while (factoryQueue.isNotEmpty())

        val resolvingConfiguration by configurations.creating {
            isTransitive = false
        }
        resolvingConfiguration.dependencies.addAll(libraryDependencies.map { project.dependencies.create(it.get()) })
        resolvingConfiguration.resolve()
    }
}
