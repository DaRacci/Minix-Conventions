
import org.jetbrains.kotlin.konan.properties.Properties
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask
import java.io.FileNotFoundException

plugins {
    java
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

fun loadPropertiesFromResources(
    propFileName: String,
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

ktlint {
    version.set("0.43.2")
    coloredOutput.set(true)
    outputToConsole.set(true)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.HTML)
        reporter(ReporterType.CHECKSTYLE)
    }
}

tasks {

    compileKotlin {
        dependsOn(ktlintFormat)
    }

    withType<BaseKtLintCheckTask> {
        workerMaxHeapSize.set("1024m")
    }
}

if (kotlinVersion != null && minConventionsKotlinVersion != kotlinVersion) {
    logger.error(
        """
    kotlinVersion property ($kotlinVersion) is not the same as the one
    applied by the Minix conventions plugin $minConventionsKotlinVersion.
    
    Will be using $minConventionsKotlinVersion for Kotlin plugin and stdlib version.
    Try to remove kotlinVersion from gradle.properties or ensure you are on the same version.
        """.trimIndent()
    )
}

// Let others read kotlinVersion and Minix version published with these conventions
if (kotlinVersion == null) {
    project.ext["kotlinVersion"] = minConventionsKotlinVersion
}

if (minixVersion == null) {
    project.ext["minixVersion"] = minVersion
}

repositories {
    mavenCentral()
    maven("https://repo.racci.dev/releases/")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
}
