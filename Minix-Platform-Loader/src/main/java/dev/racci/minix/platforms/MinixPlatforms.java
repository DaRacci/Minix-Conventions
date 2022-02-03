package dev.racci.minix.platforms;

import org.bukkit.plugin.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class MinixPlatforms {
    private MinixPlatforms() {}

    /**
     * Loads a .platform file in the plugins folder that starts with a platformName.
     */
    public static void load(@NotNull Plugin plugin, String platformName) throws ReflectiveOperationException {
        load(plugin, file -> file.getName().endsWith(".platform") && file.getName().startsWith(platformName));
    }

    /**
     * Loads a file matching a predicate out of the plugin folder.
     */
    public static void load(@NotNull Plugin plugin, Predicate<File> predicate) throws ReflectiveOperationException {
        File[] files = plugin.getDataFolder().getParentFile().listFiles();
        if (files == null) return;
        Optional<File> injectFile = Arrays.stream(files)
                .filter(predicate)
                .findFirst();
        if (injectFile.isEmpty()) return;

        LibraryLoaderInjector.inject(plugin, injectFile.get());
    }
}