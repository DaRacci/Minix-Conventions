import dev.racci.minix.gradle.data.MCTarget

plugins {
    id("dev.racci.minix")
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.ktlint) apply false
    alias(libs.plugins.kotlin.plugin.dokka)
    alias(libs.plugins.minecraft.paperweight) apply false
}

allprojects {
    version = rootProject.libs.versions.kotlin.asProvider().get()
}

minix {
    publishing {
        runNumber = System.getenv("BUILD_NUMBER")
        create("nms") {
            appendRunNumberOrSnapshot = true
        }
        create("catalog") {
            appendRunNumberOrSnapshot = true
            componentName = "versionCatalog"
        }
    }

    minecraft.withMCTarget(
        project(":nms"),
        MCTarget.Platform.PAPER,
        "1.19.3",
        applyNMS = true,
        applyMinix = false
    )
}

tasks {
    register("publish") {
        group = "publishing"
        description = "Publishes all publications produced by this project."

        dependsOn(
            gradle.includedBuilds
                .filterNot { it.name == "minix-plugin" }
                .mapNotNull { runCatching { it.task(":publish") }.getOrNull() }
        )
    }

    register("publishToMavenLocal") {
        group = "publishing"
        description = "Publishes all publications produced by this project to the local Maven repository."

        dependsOn(
            gradle.includedBuilds
                .filterNot { it.name == "minix-plugin" }
                .mapNotNull { runCatching { it.task(":publishToMavenLocal") }.getOrNull() }
        )
    }
}
