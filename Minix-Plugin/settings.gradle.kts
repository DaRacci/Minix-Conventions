enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {

    versionCatalogs.create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}

pluginManagement {
    val kotlinVersion = java.util.Properties()
        .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
        .getProperty("kotlinVersion")

    plugins {
        kotlin("jvm") version kotlinVersion
    }
}
