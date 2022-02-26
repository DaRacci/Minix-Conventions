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
//            val reobfJar = tasks.findByName("reobfJar") as? RemapJar
//            val kotlinSourcesJar = tasks.findByName("kotlinSourcesJar") as? Jar
//            if (reobfJar != null) {
//                artifact(reobfJar.outputJar) {
//                    builtBy(reobfJar)
//                    classifier = null
//                }
//                if (kotlinSourcesJar != null) {
//                    artifact(kotlinSourcesJar) {
//                        builtBy(kotlinSourcesJar)
//                        classifier = "sources"
//                    }
//                }
//                pom.withXml {
//                    val depNode = Node(asNode(), "dependencies")
//                    configurations["lib"].resolvedConfiguration.firstLevelModuleDependencies.forEach { dep ->
//                        depNode.children().cast<groovy.util.NodeList>().firstOrNull() {
//                            val node = it as Node
//                            node["groupId"] == dep.moduleGroup &&
//                                node["artifactId"] == dep.moduleName &&
//                                node["version"] == dep.moduleVersion &&
//                                node["scope"] == "provided"
//                        } ?: run {
//                            val node = Node(depNode, "dependency")
//                            node.appendNode("groupId", dep.moduleGroup)
//                            node.appendNode("artifactId", dep.moduleName)
//                            node.appendNode("version", dep.moduleVersion)
//                            node.appendNode("scope", "provided")
//                        }
//                    }
//                }
//            } else
            from(components[publishComponentName ?: "java"])
        }
    }
}
