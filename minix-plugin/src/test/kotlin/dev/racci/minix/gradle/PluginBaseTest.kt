package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByName
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

    @BeforeAll fun setupEnv() { setupEnvImpl() }

    @AfterAll fun teardownEnv() { teardownEnvImpl() }

    @BeforeAll
    fun setupProject() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(MinixGradlePlugin::class)
    }

    @Test
    fun `Plugin has extension registered`() {
        expectCatching { project.extensions.getByName<MinixBaseExtension>("mini") }.isSuccess()
    }
}
