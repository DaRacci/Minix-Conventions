package dev.racci.minix.gradle

import org.gradle.kotlin.dsl.provideDelegate
import java.util.Properties

public object Constants {
    private val properties = this::class.java.classLoader.getResourceAsStream("minix.properties").use { stream ->
        Properties().also { it.load(stream) }
    }

    public val JDK_VERSION: Int = properties["JDK_VERSION"].toString().toInt()

    public val MC_VERSION: String by properties

    public val KOTLIN_VERSION: String by properties

    public const val RACCI_REPO: String = "https://repo.racci.dev/"

    public object Dependencies {
        public val MINIX_VERSION: String by properties
    }
}
