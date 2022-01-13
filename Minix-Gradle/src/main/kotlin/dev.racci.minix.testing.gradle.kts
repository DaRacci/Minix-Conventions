import Dev_racci_minix_platform_gradle.Deps

plugins {
    id("dev.racci.minix.kotlin")
    java
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.0")
    testImplementation("io.kotest:kotest-property:5.0.0")
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation(Deps.koin.test)
    testImplementation(Deps.koin.testJunit5)
}

tasks.test {
    useJUnitPlatform()
}
