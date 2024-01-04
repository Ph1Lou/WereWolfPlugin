package fr.ph1lou.werewolfplugin.statistiks;

import com.google.gson.Gson;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.FileUtils_;
import fr.ph1lou.werewolfplugin.save.Serializer;
import fr.ph1lou.werewolfplugin.utils.Contributor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatistiksUtils {

    private static final List<Contributor> contributors = new ArrayList<>(Collections.singleton(new Contributor(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396"), 0)));
    private static List<String> messages = new ArrayList<>();
    private static int index = 0;

    public static String getMessage() {
        return messages.size() == 0 ? "" : messages.get(index++ % messages.size());
    }

    public static List<? extends Contributor> getContributors() {
        return contributors;
    }

    public static void loadMessages() {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        String language = main.getConfig().getString("lang");

        try {
            URL url = new URL("https://api.ph1lou.fr/messages/" + language);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                messages = Arrays.stream(new Gson().fromJson(response.toString(), String[].class))
                        .map(s -> game.translate(Prefix.LIGHT_BLUE) + s).collect(Collectors.toList());
            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
    }

    public static void loadContributors() {

        try {

            URL url = new URL("https://api.ph1lou.fr/users/contributor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                contributors.addAll(Arrays.asList(new Gson().fromJson(response.toString(), Contributor[].class)));

            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
    }

    public static void postGame(Main main, @NotNull GameReview gameReview) {

        String jsonInputString = Serializer.serialize(gameReview);
        File file = new File(main.getDataFolder() + File.separator + "statistics", gameReview.getGameUUID() + ".json");

        FileUtils_.save(file, jsonInputString);

        if (gameReview.getWinnerCampKey() == null || gameReview.getWinnerCampKey().equals(StatisticsEvents.DEBUG)) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistics no send because game not ended");
            return;
        }

        if (gameReview.getPlayersCount() < 17) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistics no send because player size < 17");
            return;
        }

        if (main.getWereWolfAPI().getTimer() < 3600) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistics no send because game duration < 1h");
            return;
        }

        if (((GameManager) main.getWereWolfAPI()).isCrack()) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistics no send because Server Crack");
            return;
        }

        try {

            URL url = new URL("https://api.ph1lou.fr/games/create");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            } catch (Exception ignored) {
            }

            try (BufferedReader ignored1 = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

                TextComponent msg = VersionUtils.getVersionUtils().createClickableText(main.getWereWolfAPI().translate(Prefix.ORANGE, "werewolf.statistics"),
                        String.format("https://werewolf.ph1lou.fr/game-view/%s", gameReview.getGameUUID().toString()),
                        ClickEvent.Action.OPEN_URL);
                Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(msg));
            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
    }
}
