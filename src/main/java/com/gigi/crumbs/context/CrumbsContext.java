package com.gigi.crumbs.context;

import com.gigi.crumbs.annotation.Crumb;
import com.gigi.crumbs.annotation.CrumbRef;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CrumbsContext {

    private final Map<Class<?>, Object> crumbs = new HashMap<>();

    public void initialize(Class<?> clazz) {

        loadCrumbs(clazz);
        injectReferences();
        System.out.println("[CrumbsContext] Initialized");
    }

    public Object getCrumb(Class<?> clazz) {
        return crumbs.get(clazz);
    }

    private void injectReferences() {
        crumbs.values().forEach(crumb -> {
            Class<?> clazz = crumb.getClass();
            Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                if (Arrays.stream(field.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().equals(CrumbRef.class))) {
                    field.setAccessible(true);
                    try {
                        Object value = crumbs.get(field.getType());
                        field.set(crumb, value);
                        System.out.println(crumb);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Unable to set field value due to exception", e);
                    }
                }
            });
        });
    }

    private void loadCrumbs(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();

        Set<Class<?>> scannedCrumbs;
        try {
            scannedCrumbs = Arrays.stream(getClasses(packageName))
                    .filter(scannedClazz -> Arrays.stream(scannedClazz.getAnnotations())
                            .anyMatch(annotation -> annotation.annotationType().equals(Crumb.class)))
                    .collect(Collectors.toSet());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Error occurred on load classes", e);
        }

        scannedCrumbs.forEach(crumbClass -> {
            try {
                crumbs.put(crumbClass, crumbClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("A fatal error occurred on class instantiation", e);
            }
        });
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
