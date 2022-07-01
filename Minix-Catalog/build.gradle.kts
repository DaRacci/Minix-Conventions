plugins {
    `version-catalog`
    `maven-publish`
}

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

version = "$kotlinVersion-$runNumber"

catalog {
    versionCatalog {
        from(rootProject.files("gradle/libs.versions.toml"))
    }
}

publishing {
    repositories.maven("https://repo.racci.dev/releases") {
        name = "RacciRepo"
        credentials(PasswordCredentials::class)
    }

    publications.create<MavenPublication>("maven") {
        artifactId = "catalog"
        from(components["versionCatalog"])
    }
}

tasks.create("build") {
    dependsOn("publishToMavenLocal")
}
