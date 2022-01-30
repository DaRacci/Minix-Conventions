package dev.racci.minix.platforms;

import org.jetbrains.annotations.*;

import java.net.*;
import java.util.*;

/**
 * Classloader that contains a list of loaders that will be delegated to.
 */
public final class DelegateClassLoader extends URLClassLoader {
    private final Collection<ClassLoader> parents;

    public DelegateClassLoader(@NotNull Collection<ClassLoader> parents) {
        super(new URL[0]);
        this.parents = parents;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        for(ClassLoader loader : this.parents) {
            try {
                loader.loadClass(name);
            } catch(ClassNotFoundException | NoClassDefFoundError ignore) {}
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public String toString() {
        return "DelegateClassLoader{parents=" + parents + "}";
    }
}
