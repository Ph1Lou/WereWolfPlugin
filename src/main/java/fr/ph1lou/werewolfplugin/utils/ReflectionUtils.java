package fr.ph1lou.werewolfplugin.utils;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtils {

    public static Set<Class<?>> findAllClasses(Plugin plugin, String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');

        URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
        JarURLConnection jarConnection = (JarURLConnection) new URL("jar", "", "file:" + jarUrl.getPath() + "!/").openConnection();
        try (JarFile jarFile = jarConnection.getJarFile()) {
            for (JarEntry entry : java.util.Collections.list(jarFile.entries())) {
                String name = entry.getName();
                if (name.endsWith(".class") && name.startsWith(path)) {
                    String className = name.replace('/', '.').replace(".class", "");
                    classes.add(Class.forName(className, false, plugin.getClass().getClassLoader()));
                }
            }
        }
        return classes;
    }
}
