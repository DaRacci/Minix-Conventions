package dev.racci.minix.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import strikt.api.expectCatching
import strikt.assertions.isSuccess
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PluginBaseTest {
    private lateinit var project: Project

    @BeforeAll
    fun setupProject() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(MinixGradlePlugin::class)
        System.setProperty("MINIX_TESTING_ENV", "true")
    }

    @AfterAll
    fun teardownProject() {
        System.clearProperty("MINIX_TESTING_ENV")
    }

    @Test
    fun `Plugin has extension registered`() {
        expectCatching { project.extensions.getByName("minix") }.isSuccess()
    }
}
