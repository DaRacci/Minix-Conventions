package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.assertions.isSameInstanceAs

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupportTypeTest {
    private lateinit var project: Project

    @BeforeEach
    fun setupProject() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `Support type is none`() {
        project.applyBase()
        expectThat(project.minix.kotlinSupport.orNull).isNotNull().isSameInstanceAs(MinixBaseExtension.KotlinType.NONE)
    }

    @Test
    fun `Support type is jvm`() {
        project.apply(plugin = "org.jetbrains.kotlin.jvm")
        project.plugins.forEach {
            println(it)
        }
        project.applyBase()
        expectThat(project.minix.kotlinSupport.orNull).isNotNull().isSameInstanceAs(MinixBaseExtension.KotlinType.JVM)
    }

    @Test
    fun `Support type is mpp`() {
        project.apply(plugin = "org.jetbrains.kotlin.multiplatform")
        project.plugins.forEach {
            println(it)
        }
        project.applyBase()
        expectThat(project.minix.kotlinSupport.orNull).isNotNull().isSameInstanceAs(MinixBaseExtension.KotlinType.MPP)
    }

    @Test
    fun `Support type is taken from property`() {
        project.setProperty("minix.kotlinSupport", "jvm")
        project.applyBase()
        expectThat(project.minix.kotlinSupport.orNull).isNotNull().isSameInstanceAs(MinixBaseExtension.KotlinType.JVM)
    }
}
