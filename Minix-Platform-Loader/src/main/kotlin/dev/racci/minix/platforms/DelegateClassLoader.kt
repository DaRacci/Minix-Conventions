package dev.racci.minix.platforms

import java.net.URLClassLoader

/**
 * Classloader that contains a list of loaders that will be delegated to.
 */
class DelegateClassLoader internal constructor(
    private val parents: Collection<ClassLoader>
) : URLClassLoader(arrayOfNulls(0)) {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(
        name: String,
        resolve: Boolean
    ): Class<*> {
        for (loader in parents) {
            try {
                return loader.loadClass(name)
            } catch (ignored: NoClassDefFoundError) {
            } catch (ignored: ClassNotFoundException) {
            }
        }
        return super.loadClass(name, resolve)
    }

    override fun toString(): String = "DelegateClassLoader{parents=$parents}"
}
