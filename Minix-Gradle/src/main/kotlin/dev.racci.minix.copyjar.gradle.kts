plugins {
    java
    id("com.github.johnrengelman.shadow")
}

val copyJar: String? by project
val pluginPath: String? by project.properties

if (copyJar != "false" &&
    pluginPath != null
) {
    tasks {
        register<Copy>("copyJar") {
            from(if (tasks.findByName("reobfJar") != null) getByName("reobfJar") else shadowJar)
            into(pluginPath ?: return@register)
            doLast {
                println("Copied to plugin directory $pluginPath")
            }
        }

        named<DefaultTask>("build") {
            dependsOn("copyJar")
        }
    }
}
