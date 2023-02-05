package dev.racci.minix.gradle.data

import org.gradle.api.Named
import org.gradle.api.tasks.SourceSet
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public fun KotlinTarget.disambiguate(simpleName: String): String = Disambiguatable.disambiguate(name, simpleName)

public fun KotlinTarget.disambiguate(name: Named): String = Disambiguatable.disambiguate(this.name, name.name)

public fun KotlinSourceSet.disambiguate(simpleName: String): String = Disambiguatable.disambiguate(name, simpleName)

public fun KotlinSourceSet.disambiguate(name: Named): String = Disambiguatable.disambiguate(this.name, name.name)

public interface Disambiguatable : Named {
    private val defaultName: String?
        get() = when (this) {
            is KotlinSourceSet -> SourceSet.MAIN_SOURCE_SET_NAME
            is KotlinTarget -> ""
            else -> null
        }

    public fun get(): String = name

    public fun disambiguate(simpleName: String): String = maybeDisambiguate(name, defaultName, simpleName)

    public fun disambiguate(named: Named): String = disambiguate(name, named.name)

    public companion object {
        public fun disambiguate(
            named: String,
            simpleName: String
        ): String {
            val nonEmptyParts = listOf(named, simpleName).mapNotNull { it.takeIf(String::isNotEmpty) }
            return nonEmptyParts.drop(1).joinToString(
                separator = "",
                prefix = nonEmptyParts.firstOrNull().orEmpty(),
                transform = String::capitalized
            )
        }

        public fun maybeDisambiguate(
            named: String,
            mainName: String?,
            simpleName: String
        ): String = when {
            named == mainName || named.isEmpty() -> simpleName
            else -> disambiguate(named, simpleName)
        }
    }
}
