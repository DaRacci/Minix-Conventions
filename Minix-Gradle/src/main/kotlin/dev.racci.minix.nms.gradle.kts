val serverVersion: String by project
val useTentacles: String? by project
val removeDev: String? by project

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.racci.dev/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
}

//var publish: Boolean = false
//project.afterEvaluate {
//    publish = try {
//        publishing.publications {
//            println("Creating temp publication")
//            val createMeta by creating(MavenPublication::class) {
//                from(components["java"])
//            }
//        }
//        true
//    } catch (e: Exception) {
//        println("Publishing is disabled")
//        false
//    }
//}

tasks {
    named("assemble") { dependsOn(reobfJar) }

    withType<PublishToMavenLocal> { dependsOn(reobfJar) }

//    withType<RemapJar> {
//        if (publish) dependsOn("generateMetadataFileForTempPublication")
//        doLast {
//            if (removeDev.toBoolean()) { inputJar.orNull?.asFile?.delete() }
//            if (!publish) return@doLast
//
//            (components["java"] as AdhocComponentWithVariants).apply {
//                listOf("apiElements", "runtimeElements").forEach { base ->
//                    withVariantsFromConfiguration(configurations[base]) {
//                        configurationVariant.artifacts.removeIf { it.file == inputJar }
//                    }
//                }
//            }
//
//            var metadata: File by Delegates.notNull()
//            tasks.getByName<GenerateModuleMetadata>("generateMetadataFileForCreateMetaPublication") {
//                metadata = outputFile.get().asFile
//            }
//
//            if (publishing.publications.findByName("maven") == null) return@doLast
//            val maven by publishing.publications.getting(MavenPublication::class) {
//                artifact(outputJar) {
//                    builtBy(reobfJar)
//                    classifier = null
//                }
//                artifact(kotlinSourcesJar) {
//                    builtBy(kotlinSourcesJar)
//                    classifier = "sources"
//                }
//                artifact(metadata) {
//                    builtBy(metadata)
//                    classifier = "module"
//                }
//            }
//        }
//    }
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
