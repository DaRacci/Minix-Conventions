package dev.racci.minix.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupportTypeTest {
    private lateinit var project: Project

    @BeforeAll
    fun setupEnv() { setupEnvImpl() }

    @AfterAll
    fun teardownEnv() { teardownEnvImpl() }

    @BeforeEach
    fun setupProject() {
        project = ProjectBuilder.builder().build()
    }
}
