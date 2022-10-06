enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {

    versionCatalogs.create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
