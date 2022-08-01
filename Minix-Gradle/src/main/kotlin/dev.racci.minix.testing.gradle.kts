plugins {
    id("dev.racci.minix.kotlin")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.4.1")
    testImplementation("io.kotest:kotest-property:5.4.1")
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("io.insert-koin:koin-test:3.2.0")
    testImplementation("io.insert-koin:koin-test-junit5:3.2.0")
}

tasks.test {
    useJUnitPlatform()
}
