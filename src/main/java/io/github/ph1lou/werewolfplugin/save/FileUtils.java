package io.github.ph1lou.werewolfplugin.save;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class FileUtils {

    public static void createFile(File file) throws IOException {

        if (!file.exists()) {

            if(file.getParentFile().mkdirs()){
                System.out.println("[WereWolfPlugin] Create parent directory of" + file.getName());
            }
            if(file.createNewFile()){
                System.out.println("[WereWolfPlugin] Create " + file.getName());
            }
        }
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
        System.out.println("[WereWolfPlugin] Copying ->" + source + "\n\tto ->" + destination);
        File file = new File(destination);
        try {
            createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (source != null) {
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

