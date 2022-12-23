package dev.racci.minix.gradle.extensions

// public class MinixPublicationExtension : ExtensionBase() {
//
//    @Input
//    public var runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
//
//    @Input
//    public var runNumberDelimiter: String? = project.findProperty("minix.runNumberDelimiter") as? String
//
//    @Input
//    public var addRunNumber: String? = project.findProperty("minix.addRunNumber") as? String
//
//    @Input
//    public var publishComponentName: String? = project.findProperty("minix.publishComponentName") as? String
//
//    @Input
//    public var documentationDir: String = project.findProperty("minix.buildDir") as? String ?: "/docs"
//
//    @Input
//    public var configureMavenPublish: Boolean = true
//
//    @Input
//    public var preRelease: Boolean = run {
//        val preReleaseRegex = Regex("(?<![0-9])([a-zA-Z])(?![a-zA-Z])")
//        preReleaseRegex.containsMatchIn(project.version.toString())
//    }
//
//    @Input
//    public var repository: (Project, RepositoryHandler) -> Unit =
//        { project, handler -> // This should be changed if you aren't me since well only I have publishing credentials.
//            val preRelease = this.preRelease || project.version.toString().endsWith("-SNAPSHOT", true)
//            handler.maven("https://repo.racci.dev/${if (preRelease) "snapshots" else "releases"}") {
//                name = "RacciRepo"
//                credentials(PasswordCredentials::class)
//            }
//        }
//
//    override fun configure(project: Project) = with(project) {
//        applyPlugins(this)
//
//        afterEvaluate {
//            if (addRunNumber != "false" && runNumber != null) {
//                version = "$version${runNumberDelimiter ?: "."}$runNumber"
//            }
//        }
//
//        configureExtensions(this)
//        configureTasks(this)
//    }
//
//    private fun applyPlugins(project: Project) {
//        project.pluginManager.apply(MavenPublishPlugin::class)
//        project.pluginManager.apply(DokkaPlugin::class)
//    }
//
//    private fun configureTasks(project: Project) {
//        if (documentationDir == null) return
//
//        project.tasks.withType<DokkaTask>().whenTaskAdded {
//            outputDirectory.set(project.file(documentationDir))
//            dokkaSourceSets.configureEach {
//                includeNonPublic.set(false)
//                skipEmptyPackages.set(true)
//                displayName.set(project.name.split("-").getOrElse(1) { project.name })
//                platform.set(Platform.jvm)
//                jdkVersion.set(Constants.JDK_VERSION)
//                sourceLink { remoteLineSuffix.set("#L") }
//            }
//        }
//    }
//
//    private fun configureExtensions(project: Project) {
//        project.extensions.configure<JavaPluginExtension>("java") { withSourcesJar() }
//
//        project.extensions.configure<PublishingExtension>("publishing") {
//            repository(project, project.repositories)
//            publications.register("maven", MavenPublication::class) {
//                artifactId = project.rootProject.name
//                groupId = project.rootProject.group.toString()
//
//                if (!configureMavenPublish) return@register
//
//                if (publishComponentName != null) {
//                    return@register from(project.components[publishComponentName!!])
//                }
//
//                project.tasks.findByName("reobfJar")?.configure<RemapJar> {
//                    artifact(this.outputJar) {
//                        builtBy(this)
//                        classifier = null
//                    }
//                }
//
//                project.tasks.findByName("kotlinSourcesJar")?.configure<Jar> {
//                    artifact(this) {
//                        builtBy(this)
//                        classifier = "sources"
//                    }
//                }
//
//                if (artifacts.isEmpty()) {
//                    from(project.components["kotlin"])
//                }
//            }
//        }
//    }
// }