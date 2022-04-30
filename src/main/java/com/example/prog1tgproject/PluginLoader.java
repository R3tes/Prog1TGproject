package com.example.prog1tgproject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PluginLoader {

    public ArrayList<Plugin> getPlugins() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ArrayList<Plugin> plugins = new ArrayList<>();

        Set<Class> classes = findAllClassesUsingClassLoader("com.example.prog1tgproject.plugins");

        for (Class c :
                classes) {
            plugins.add((Plugin) c.getDeclaredConstructor().newInstance());
        }
        return plugins;
    }

    private Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }
}
