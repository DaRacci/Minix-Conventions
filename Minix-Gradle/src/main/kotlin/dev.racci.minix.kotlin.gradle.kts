import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException

plugins {
    java
    kotlin("jvm")
    id("dev.racci.minix.platform")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

detekt {
    toolVersion = "1.19.0"
    source = files("src/main/java", "src/main/kotlin")
    parallel = true
    config = detektFile
    buildUponDefaultConfig = false
    allRules = false
    disableDefaultRuleSets = false
    debug = true
    ignoreFailures = false
    basePath = projectDir.path
}

val detektFile: ConfigurableFileCollection get() {
    val tempFile = org.jetbrains.kotlin.konan.file.createTempFile("detekt")
    val inputStream = javaClass.classLoader!!.getResourceAsStream("detekt.yml")!!
    inputStream.use { tempFile.writeBytes(it.readAllBytes()) }
    return files(tempFile.path)
}

fun loadPropertiesFromResources(
    propFileName: String
): Properties {
    val props = Properties()
    val inputStream = javaClass.classLoader!!.getResourceAsStream(propFileName)
        ?: throw FileNotFoundException("property file '$propFileName' not found in the classpath")
    inputStream.use { props.load(it) }
    return props
}

val savedProps = loadPropertiesFromResources("Minix-Conventions.properties")
val minConventionsVersion: String by savedProps
val minVersion: String by savedProps
val minConventionsKotlinVersion: String by savedProps

val kotlinVersion: String? by project
val minixVersion: String? by project
val detektVersion: String? by project

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
if(kotlinVersion == null) {
    project.ext["kotlinVersion"] = minConventionsKotlinVersion
}

if(minixVersion == null) {
    project.ext["minixVersion"] = minVersion
}

repositories {
    mavenCentral()
    maven("https://repo.racci.dev/releases/")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    implementation(platform("dev.racci:Minix-Platform:$minConventionsVersion"))
}
