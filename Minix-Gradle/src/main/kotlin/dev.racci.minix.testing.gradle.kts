plugins {
    id("dev.racci.minix.kotlin")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
    testImplementation("io.kotest:kotest-property:5.5.0")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("io.insert-koin:koin-test:3.2.2")
    testImplementation("io.insert-koin:koin-test-junit5:3.2.2")
}

tasks.test {
    useJUnitPlatform()
}
