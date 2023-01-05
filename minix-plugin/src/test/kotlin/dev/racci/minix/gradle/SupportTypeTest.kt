package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isSameInstanceAs

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

    @Test
    fun `Support type is none`() {
        project.applyBase()
        expectThat(project.minix.getSupportType(project)).isSameInstanceAs(MinixBaseExtension.KotlinType.NONE)
    }

    @Test
    fun `Support type is jvm`() {
        project.apply(plugin = "org.jetbrains.kotlin.jvm")
        project.applyBase()
        expectThat(project.minix.getSupportType(project)).isSameInstanceAs(MinixBaseExtension.KotlinType.JVM)
    }

    @Test
    fun `Support type is mpp`() {
        project.apply(plugin = "org.jetbrains.kotlin.multiplatform")
        project.applyBase()
        expectThat(project.minix.getSupportType(project)).isSameInstanceAs(MinixBaseExtension.KotlinType.MPP)
    }
}
