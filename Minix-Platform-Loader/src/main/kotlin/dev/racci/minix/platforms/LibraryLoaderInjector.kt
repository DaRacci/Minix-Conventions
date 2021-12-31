@file:Suppress("UNCHECKED_CAST")
package dev.racci.minix.platforms

import sun.misc.Unsafe
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.PluginClassLoader
import java.io.File
import java.net.URLClassLoader
import java.util.function.Function

/**
 * A helper class that uses reflection to inject dependencies into Spigot's library loader.
 */
object LibraryLoaderInjector {

    /**
     * Injects a jar file into a plugin's dependencies for loading. Will ensure a shared classloader is used
     * if separate plugins inject using this method.
     */
    @Throws(ReflectiveOperationException::class)
    fun inject(
        plugin: Plugin,
        injectFile: File
    ) {
        // Read library loader
        val pluginClassLoader = plugin.javaClass.classLoader as PluginClassLoader
        val libraryLoader = getLibraryClassLoaderFor(pluginClassLoader)

        // Get or load a service which extends the built-in java Function class, so it can be shared across classloaders
        val services = Bukkit.getServicesManager()
        val platformLoader = (
            services.knownServices.asSequence()
                .filter { it.name == PlatformProvider::class.java.name }
                .map { services.load(it) }
                .firstOrNull() as? Function<File, URLClassLoader>
                ?: run {
                    val service = PlatformProviderImpl()
                    services.register(PlatformProvider::class.java, service, plugin, ServicePriority.Low)
                    service
                }
            ).apply(injectFile) as ClassLoader

        // Update the library loader to delegate to our platform *after* the plugin's own libraries
        val newLoader = DelegateClassLoader(listOf(libraryLoader, platformLoader))
        Bukkit.getServicesManager()
        setLibraryClassLoaderFor(pluginClassLoader, newLoader)
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun getLibraryClassLoaderFor(pluginClassLoader: ClassLoader): ClassLoader {
        val unsafeField = Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        val unsafe = unsafeField[null] as Unsafe
        val libraryLoaderField = PluginClassLoader::class.java.getDeclaredField("libraryLoader")
        val libraryLoaderOffset = unsafe.objectFieldOffset(libraryLoaderField)
        return unsafe.getObject(pluginClassLoader, libraryLoaderOffset) as? ClassLoader
            ?: URLClassLoader(arrayOfNulls(0)).also {
                unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), it)
            }
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun setLibraryClassLoaderFor(
        pluginClassLoader: ClassLoader,
        libraryLoader: ClassLoader
    ) {
        val unsafeField = Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        val unsafe = unsafeField[null] as Unsafe
        val libraryLoaderField = PluginClassLoader::class.java.getDeclaredField("libraryLoader")
        unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader)
    }
}
