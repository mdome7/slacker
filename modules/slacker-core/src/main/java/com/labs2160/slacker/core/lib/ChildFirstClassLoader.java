package com.labs2160.slacker.core.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class ChildFirstClassLoader extends URLClassLoader {

    final private URL [] localClasspath;

    final private ClassLoader system;

    public ChildFirstClassLoader(URL [] classpath, ClassLoader parent) {
        super(classpath, parent);
        localClasspath = classpath;
        system = getSystemClassLoader();
    }

    public URL [] getLocalResources() {
        return localClasspath;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                try {
                    c = super.loadClass(name, resolve);
                } catch (ClassNotFoundException e2) {
                    if (system != null) c = system.loadClass(name);
                }
            }
        }
        if (resolve) resolveClass(c);
        return c;
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) url = super.getResource(name);
        if (url == null && system != null) url = system.getResource(name);
        return url;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        /**
         * Similar to super, but local resources are enumerated before parent
         * resources
         */
        Enumeration<URL> systemUrls = null;
        if (system != null) {
            systemUrls = system.getResources(name);
        }
        Enumeration<URL> localUrls = findResources(name);
        Enumeration<URL> parentUrls = null;
        if (getParent() != null) {
            parentUrls = getParent().getResources(name);
        }
        final List<URL> urls = new ArrayList<URL>();
        if (localUrls != null) {
            while (localUrls.hasMoreElements()) {
                URL local = localUrls.nextElement();
                urls.add(local);
            }
        }
        if (systemUrls != null) {
            while (systemUrls.hasMoreElements()) {
                urls.add(systemUrls.nextElement());
            }
        }
        if (parentUrls != null) {
            while (parentUrls.hasMoreElements()) {
                urls.add(parentUrls.nextElement());
            }
        }
        return new Enumeration<URL>() {
            Iterator<URL> iter = urls.iterator();

            @Override
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            @Override
            public URL nextElement() {
                return iter.next();
            }
        };
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
        }
        return null;
    }
}