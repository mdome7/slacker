package com.labs2160.slacker.core.lib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 */
public abstract class ClassLoaderUtil {

    private ClassLoaderUtil() {}

    private static final Class[] parameters = new Class[] { URL.class };

    public static void addFile(String s) throws IOException {
        addFile(getSystemClassLoader(), new File(s));
    }

    public static void addFile(ClassLoader cl, String s) throws IOException {
        addFile(cl, new File(s));
    }

    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }

    public static void addFile(ClassLoader cl, File f) throws IOException {
        addURL(cl, f.toURI().toURL());
    }

    public static void addURL(URL u) throws IOException {
        addURL(getSystemClassLoader(), u);
    }


    public static void addURL(ClassLoader cl, URL u) throws IOException {
        URLClassLoader urlCl = (URLClassLoader) cl;
        Class sysclass = urlCl.getClass();
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(cl, new Object[] { u });
        } catch (Throwable t) {
            throw new IOException("Error adding URL " + u + " to system classloader - ", t);
        }
    }

    private static URLClassLoader getSystemClassLoader() {
        return (URLClassLoader) ClassLoader.getSystemClassLoader();
    }
}
