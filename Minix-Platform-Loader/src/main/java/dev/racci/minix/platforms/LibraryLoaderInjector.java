package dev.racci.minix.platforms;

import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import sun.misc.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.function.*;


/**
 * A helper class that uses reflection to inject dependencies into Spigot's library loader.
 */
public class LibraryLoaderInjector {

    private LibraryLoaderInjector() {}

    /**
     * Injects a jar file into a plugin's dependencies for loading. Will ensure a shared classloader is used
     * if separate plugins inject using this method.
     */
    static void inject(Plugin plugin, File injectFile) throws ReflectiveOperationException {
        // Read library loader
        PluginClassLoader pluginClassLoader = (PluginClassLoader) plugin.getClass().getClassLoader();
        ClassLoader libraryLoader = getLibraryClassLoaderFor(pluginClassLoader);

        // Get or load a service which extends the built-in java Function class, so it can be shared across classloaders
        ServicesManager services = Bukkit.getServicesManager();
        @SuppressWarnings("unchecked")
        Optional<Function<File, URLClassLoader>> platformProvider = (Optional<Function<File, URLClassLoader>>) services.getKnownServices().stream()
                .filter(it -> it.isAssignableFrom(PlatformProvider.class))
                .map(services::load)
                .findFirst();

        URLClassLoader platformLoader = platformProvider.orElseGet(() -> {
            PlatformProviderImpl service = new PlatformProviderImpl();
            services.register(PlatformProvider.class, service, plugin, ServicePriority.Low);
            return service;
        }).apply(injectFile);

        // Update the library loader to delegate to our platform *after* the plugin's own libraries
        var newLoader = new DelegateClassLoader(List.of(libraryLoader, platformLoader));
        Bukkit.getServicesManager();
        setLibraryClassLoaderFor(pluginClassLoader, newLoader);
    }

    static ClassLoader getLibraryClassLoaderFor(ClassLoader pluginClassLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        long libraryLoaderOffset = unsafe.objectFieldOffset(libraryLoaderField);

        ClassLoader libraryLoader = (ClassLoader) unsafe.getObject(pluginClassLoader, libraryLoaderOffset);
        if (libraryLoader == null) {
            // If null, create
            libraryLoader = new URLClassLoader(new URL[0]);
            unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader);
        }
        return libraryLoader;
    }

    static void setLibraryClassLoaderFor(ClassLoader pluginClassLoader, ClassLoader libraryLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader);
    }
}
