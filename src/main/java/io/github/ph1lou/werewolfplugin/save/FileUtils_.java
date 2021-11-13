package io.github.ph1lou.werewolfplugin.save;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class FileUtils_ {

    public static void createFile(File file) throws IOException {

        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                Bukkit.getLogger().info("[WereWolfPlugin] Create Parent Directory for " + file.getName());
            }
            if (file.createNewFile()) {
                Bukkit.getLogger().info("[WereWolfPlugin] Create " + file.getName());
            }
        }
    }

    public static void loadConfig(Main main, String name){

        GameManager game = (GameManager) main.getWereWolfAPI();

        File file = new File(main.getDataFolder() + File.separator + "configs" + File.separator, name + ".json");

        if (file.exists()) {
            game.setConfig(Serializer.deserialize(loadContent(file)));
            game.setRoleInitialSize(0);
            game.getModerationManager().checkQueue();
            ((Configuration) game.getConfig()).addRegister((RegisterManager) main.getRegisterManager());
            game.getListenersLoader().update();
        }

        IConfiguration config = game.getConfig();
        IRegisterManager register = main.getRegisterManager();

        for (RoleRegister roleRegister : register.getRolesRegister()) {
            String key = roleRegister.getKey();
            game.setRoleInitialSize(game.getRoleInitialSize() + config.getRoleCount(key));
        }

        save(file, Serializer.serialize(game.getConfig()));
    }

    public static void save(File file, String text) {

        try {
            createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (final FileWriter fw = new FileWriter(file)) {
            fw.write(text);
            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJson(File file, JsonObject jsonObject) {
        try {
            createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (final Writer fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {

            jsonObject.writeTo(fw, WriterConfig.PRETTY_PRINT);
            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream source, String destination) {

        if (source == null) return;

        Bukkit.getLogger().info("[WereWolfPlugin] Copying ->" + source + "\n\tto ->" + destination);
        File file = new File(destination);

        try {
            createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = source.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convert(InputStream inputStream) {

        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String loadContent(File file) {

        if (file.exists()) {

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8))) {


                final StringBuilder text = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    text.append(line);
                }

                return text.toString();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}

