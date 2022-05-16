package fr.ph1lou.werewolfplugin.utils;

import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static Set<Class<?>> findAllClasses(Plugin plugin, String packageName) throws IOException {
        return ClassPath.from(plugin.getClass().getClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
