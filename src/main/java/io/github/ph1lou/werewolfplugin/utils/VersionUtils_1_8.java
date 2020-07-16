package io.github.ph1lou.werewolfplugin.utils;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"deprecation"})
public class VersionUtils_1_8 extends VersionUtils {


    @Override
    public void setSkullOwner(SkullMeta skull, OfflinePlayer player, String name) {
        if (name != null) {
            skull.setOwner(name);
        }
    }

    @Override
    public void setPlayerMaxHealth(Player player, double maxHealth) {
        player.setMaxHealth(maxHealth);
    }

    @Override
    public double getPlayerMaxHealth(Player player) {
        return player.getMaxHealth();
    }


    @Override
    public void setGameRuleValue(World world, String gameRule, Object value) {
        world.setGameRuleValue(gameRule, value.toString());
    }


    @Override
    public void setTeamNameTagVisibility(Team team, boolean value) {
        team.setNameTagVisibility(value ? NameTagVisibility.ALWAYS : NameTagVisibility.NEVER);
    }


    @Override
    public void setItemUnbreakable(ItemMeta meta, boolean b) {


        try {
            Method spigot = NMSUtils.getMethod(meta.getClass(), "spigot");
            Object spigotInstance = spigot.invoke(meta);
            Method setUnbreakable = NMSUtils.getMethod(spigotInstance.getClass(), "setUnbreakable", boolean.class);
            setUnbreakable.invoke(spigotInstance, b);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        try {
            Object chatTitle = NMSUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = NMSUtils.getNMSClass("PacketPlayOutTitle").getConstructor(
                    NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSUtils.getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = NMSUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = NMSUtils.getNMSClass("PacketPlayOutTitle").getConstructor(
                    NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSUtils.getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    fadeInTime, showTime, fadeOutTime);

            NMSUtils.sendPacket(player, packet);
            NMSUtils.sendPacket(player, timingPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendActionBar(Player player, String message) {

        try {
            Class<?> craftPlayerClass = NMSUtils.getNMSClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = NMSUtils.getNMSClass("PacketPlayOutChat");
            Class<?> packetClass = NMSUtils.getNMSClass("Packet");
            if (Bukkit.getServer().getClass().getPackage().getName().equals("v1_8_R1")) {
                Class<?> chatSerializerClass = NMSUtils.getNMSClass("ChatSerializer");
                Class<?> iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = NMSUtils.getNMSClass("ChatComponentText");
                Class<?> iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = NMSUtils.getNMSClass("ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
                } catch (ClassNotFoundException cnfe) {
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
                }
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTabTitle(Player player, String header, String footer) {

    }

    @Override
    public void patchBiomes() {


        Field biomesField;
        try {
            Class<?> clazz = NMSUtils.getNMSClass("BiomeBase");

            biomesField = clazz.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & ~Modifier.FINAL);

            Object biomes = biomesField.get(null);
            Object plain = Array.get(biomes, 1);
            Array.set(biomes, 24, plain);
            Array.set(biomes, 10, plain);
            Array.set(biomes, 0, plain);
            Array.set(biomes, 25, plain);
            Array.set(biomes, 16, plain);
            Array.set(biomes, 24, plain);
            Array.set(biomes, 25, plain);
            Array.set(biomes, 21, plain);
            Array.set(biomes, 22, plain);
            Array.set(biomes, 23, plain);
            Array.set(biomes, 24, plain);
            Array.set(biomes, 34, plain);
            biomesField.set(null, biomes);

        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Location findBiome(World world) throws Exception {

        for (int i = -2000; i < 2000; i += 16) {
            for (int j = -2000; j < 2000; j += 16) {
                if (world.getBiome(i, j) == Biome.valueOf("ROOFED_FOREST")) {
                    return new Location(world, i, 151, j);
                }
            }
        }
        throw new Exception("No roofed found");
    }

    @Override
    public int biomeSize(Location location, World world) {

        int i = 0;
        Biome biome = world.getBiome(location.getBlockX(), location.getBlockZ());
        List<Location> locations = new ArrayList<>();
        List<Location> finalLocations = new ArrayList<>();
        locations.add(location);

        while (!locations.equals(finalLocations)) {

            Location location1 = locations.get(i);
            int x = location1.getBlockX();
            int z = location1.getBlockZ();

            for (int x1 = -1; x1 < 2; x1 += 2) {

                for (int z1 = -1; z1 < 2; z1 += 2) {

                    if (world.getBiome(x1 + x, z1 + z) == biome) {
                        Location location2 = new Location(world, x1 + x, 0, z1 + z);
                        if (!finalLocations.contains(location2) && !locations.contains(location2)) {
                            locations.add(location2);
                        }
                    }
                }
            }
            finalLocations.add(location1);
            i++;
            if (i > 30000) {
                return 33333;
            }
        }

        return i;
    }

}
