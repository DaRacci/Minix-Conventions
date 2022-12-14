import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.konan.properties.Properties
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.io.FileNotFoundException

fun loadPropertiesFromResources(
    propFileName: String
): Properties {
    val props = Properties()
    val inputStream = javaClass.classLoader!!.getResourceAsStream(propFileName)
        ?: throw FileNotFoundException("property file '$propFileName' not found in the classpath")
    inputStream.use { props.load(it) }
    return props
}

val savedProps: Properties = loadPropertiesFromResources("Minix-Conventions.properties")
val minVersion: String by savedProps
val minConventionsKotlinVersion: String by savedProps

val kotlinVersion: String? by project
val minixVersion: String? by project

// Let others read kotlinVersion and Minix version published with these conventions
if (kotlinVersion == null) {
    project.ext["kotlinVersion"] = minConventionsKotlinVersion
}

if (minixVersion == null) {
    project.ext["minixVersion"] = minVersion
}

plugins {
    java
    `kotlin-scripting`
}

fun commonKotlin() {
    kotlinExtension.apply {
        jvmToolchain(17)
        explicitApi()
        sourceSets.all {
            languageSettings {
                apiVersion = "1.7"
                languageVersion = "1.7"
            }
        }
    }

    dependencies {
        "implementation"(platform(kotlin("bom:$kotlinVersion")))
        "compileOnly"(kotlin("stdlib-jdk8"))
    }
}

val Project.trueRoot: Project
    get() {
        var root = this
        while (root.parent != null) {
            root = root.parent!!
        }
        return root
    }

val Project.recursiveSubprojects: Sequence<Project>
    get() = sequence {
        yield(this@recursiveSubprojects)
        subprojects.forEach { yieldAll(it.recursiveSubprojects) }
    }

fun applyToTarget(target: Project) {
    target.buildDir = trueRoot.buildDir.resolve(target.name.toLowerCase())

    target.plugins.withType<KotlinMultiplatformPlugin> {
        commonKotlin()
    }

    target.plugins.withType<KotlinBasePlugin> {
        commonKotlin()
    }

    configurations {
        val slim = maybeCreate("slim")

        compileClasspath.get().extendsFrom(slim)
        runtimeClasspath.get().extendsFrom(slim)
        apiElements.get().extendsFrom(slim)
    }

    target.apply<KtlintPlugin>()
    target.configure<KtlintExtension> {
        version.set("0.45.2")
        coloredOutput.set(true)
        outputToConsole.set(true)
        enableExperimentalRules.set(false)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.HTML)
            reporter(ReporterType.CHECKSTYLE)
        }
        baseline.set(file("$trueRoot/config/ktlint/baseline-${project.name}.xml"))
    }
}

@Suppress("UnstableApiUsage")
gradle.settingsEvaluated {
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

        repositories {
            mavenCentral()
            maven("https://repo.racci.dev/releases/")
            maven("https://repo.racci.dev/snapshots/")
        }
    }
}

fun Task.recDep() {
    dependsOn(gradle.includedBuilds.mapNotNull { runCatching { it.task(":$name") }.getOrNull() })
}

val trueRoot = project.trueRoot
if (trueRoot == project) {
    tasks {
        findByName("publish")?.recDep()
        findByName("publishToMavenLocal")?.recDep()
        findByName("ktlintFormat")?.recDep()
        findByName("apiDump")?.recDep()
        build.get().recDep()
        clean.get().recDep()
    }

    applyToTarget(project)
    recursiveSubprojects.forEach { target -> applyToTarget(target) }
} else {
    afterEvaluate {
        val subSlim = this.configurations.findByName("slim") ?: return@afterEvaluate
        subSlim.dependencies.forEach { dep -> trueRoot.dependencies { "slim"(dep) } }
    }
}
