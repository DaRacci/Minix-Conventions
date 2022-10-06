import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

val copyJar: String? by project
val pluginPath: String? by project.properties
val CI: String? by System.getenv()

if (shouldCopy()) {
    tasks {
        register<Copy>("copyJar") {
            val sourceJar = findByName("reobfJar") ?: getByName("shadowJar", ShadowJar::class)

            from(sourceJar)
            into(pluginPath!!)
            doLast {
                println("Copied to plugin directory $pluginPath")
            }
        }
    }
}

fun shouldCopy(): Boolean {
    return CI != "true" && copyJar != "false" && pluginPath == null
}
