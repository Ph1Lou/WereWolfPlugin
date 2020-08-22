package io.github.ph1lou.werewolfplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;

public class NMSUtils {

    private static final String version = getVersion();

    public static String getVersion() {
        if (version != null) {
            return version;
        } else {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            return name.substring(name.lastIndexOf(46) + 1) + ".";
        }
    }

    public static Method getMethod(Class<?> c, String name) throws ReflectiveOperationException {
        return getMethod(c, name, -1);
    }

    public static Method getMethod(Class<?> c, String name, int args) throws ReflectiveOperationException {
        for (Method method : c.getMethods()) {
            if (method.getName().equals(name) && (args == -1 || method.getParameterCount() == args)) {
                method.setAccessible(true);
                return method;
            }
        }

        for (Method method : c.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                method.setAccessible(true);
                return method;
            }
        }

        throw new ReflectiveOperationException("Method " + name + " not found in " + c.getName());
    }

    public static Method getMethod(Class<?> c, String name, Class<?>... argTypes) throws ReflectiveOperationException {

        for (Method method : c.getMethods()) {
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), argTypes)) {
                method.setAccessible(true);
                return method;
            }
        }

        for (Method method : c.getDeclaredMethods()) {
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), argTypes)) {
                method.setAccessible(true);
                return method;
            }
        }

        throw new ReflectiveOperationException("Method " + name + " not found in " + c.getName());
    }



    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        try {
            return getClassWithException(name);
        } catch (ClassNotFoundException ex1) {
            // Continue and try craft class
        }

        return getCraftClassWithException(name);
    }

    private static Class<?> getClassWithException(String name) throws ClassNotFoundException {
        String className = "net.minecraft.server." + getVersion() + name;
        return Class.forName(className);
    }

    private static Class<?> getCraftClassWithException(String name) throws ClassNotFoundException {
        String classname = "org.bukkit.craftbukkit." + getVersion() + name;
        return Class.forName(classname);
    }

}