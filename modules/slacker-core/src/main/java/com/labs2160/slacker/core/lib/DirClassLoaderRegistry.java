package com.labs2160.slacker.core.lib;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads all the JARs from their respective directories with each directory
 * getting its own ChildFirstClassLoader.
 * @author mdometita
 *
 */
public class DirClassLoaderRegistry {

    private final static Logger logger = LoggerFactory.getLogger(DirClassLoaderRegistry.class);

    final private Path baseDirectory;

    private Map<String,ClassLoader> classLoaders;

    public DirClassLoaderRegistry(Path dir) {
        baseDirectory = dir;
        classLoaders = new HashMap<>();
        initialize();
    }

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    public Set<String> getClassLoaderDirNames() {
        return classLoaders.keySet();
    }

    public ClassLoader getClassLoader(String subDirName) {
        return classLoaders.get(subDirName);
    }

    private void initialize() {
        final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException { return Files.isDirectory(entry); }
        };

        logger.info("Creating ClassLoaders for base directory: {}", baseDirectory.toString());

        try (DirectoryStream<Path> dirs = Files.newDirectoryStream(baseDirectory, filter)) {
            for (Path dir : dirs) {
                final String subDirName = dir.toFile().getName();
                logger.info("Loading directory: {}", subDirName);
                classLoaders.put(subDirName, createClassLoader(dir));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading plugins - " + e.getMessage(), e);
        }
    }

    private ClassLoader createClassLoader(Path path) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        try (DirectoryStream<Path> jars = Files.newDirectoryStream(path, "*.jar")) {
            for (Path jar : jars) {
                logger.info("Loading JAR: {}", jar.toString());
                urls.add(jar.toUri().toURL());
            }
        } catch (IOException e) {
            throw e;
        }
        return new ChildFirstClassLoader(urls.toArray(new URL[0]), DirClassLoaderRegistry.class.getClassLoader());
    }

}
