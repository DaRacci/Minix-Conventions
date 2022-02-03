package dev.racci.minix.platforms;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

interface PlatformProvider extends Function<File, URLClassLoader> {}

class PlatformProviderImpl implements PlatformProvider {
    private final Map<File, URLClassLoader> alreadyLoaded = new HashMap<>();

    @Override
    public URLClassLoader apply(File file) {
        URLClassLoader cached = alreadyLoaded.get(file);
        if(cached == null) {
            try {
                cached = new URLClassLoader(new URL[]{file.toURI().toURL()});
                alreadyLoaded.put(file, cached);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }
}