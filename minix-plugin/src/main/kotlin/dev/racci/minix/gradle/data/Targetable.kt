package dev.racci.minix.gradle.data

import org.gradle.api.Named

/**
 * A wrapper around a string that allows for easy prefixing for source sets and targets.
 *
 * @property mainString The base string to use.
 */
@JvmInline
public value class Targetable(private val mainString: String) : Disambiguatable {
    override fun getName(): String {
        return mainString
    }

    override fun disambiguate(named: Named): String {
        return disambiguate(named.name)
    }

    override fun disambiguate(simpleName: String): String {
        return Disambiguatable.disambiguate(get(), simpleName)
    }
}
