package dev.racci.minix.platforms;

import org.jetbrains.annotations.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Classloader that contains a list of loaders that will be delegated to.
 */
public class DelegateClassLoader extends URLClassLoader {

    private final Collection<? extends ClassLoader> delegates;

    public DelegateClassLoader(@NotNull Collection<? extends ClassLoader> delegates) {
        super(new URL[0]);
        this.delegates = delegates;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        for(ClassLoader loader : this.delegates) {
            try {
                loader.loadClass(name);
            } catch(ClassNotFoundException | NoClassDefFoundError ignore) {}
        }
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (ClassLoader loader : delegates) {
            try {
                return loader.loadClass(name);
            } catch (ClassNotFoundException ignored) {}
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public URL findResource(String name) {
        for (ClassLoader loader : delegates) {
            URL url = loader.getResource(name);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        List<URL> urls = new ArrayList<>();
        for (ClassLoader loader : delegates) {
            Enumeration<URL> e = loader.getResources(name);
            while (e.hasMoreElements()) {
                urls.add(e.nextElement());
            }
        }
        return Collections.enumeration(urls);
    }

    @Override
    public String toString() {
        return "DelegateClassLoader{parents=" + delegates + "}";
    }
}
