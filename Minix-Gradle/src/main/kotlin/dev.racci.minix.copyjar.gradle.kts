val copyJar: String? by project
val pluginPath: String? by project.properties
val ci: String? by System.getenv().withDefault { null }

if (shouldCopy()) {
    tasks {
        register<Copy>("copyJar") {
            val sourceJar = findByName("reobfJar")
                ?: findByName("shadowJar")
                ?: findByName("compileKotlin")
                ?: named<Jar>("compileJava")

            from(sourceJar)
            into(pluginPath!!)
        }
    }
}

fun shouldCopy(): Boolean {
    return ci != "true" && copyJar != "false" && pluginPath != null
}
