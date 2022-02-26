package fr.ph1lou.werewolfplugin.statistiks;

import com.google.gson.Gson;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.FileUtils_;
import fr.ph1lou.werewolfplugin.save.Serializer;
import fr.ph1lou.werewolfplugin.utils.Contributor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

public class StatistiksUtils {

    private static String[] messages = {};
    private static int index = 0;
    private static final List<Contributor> contributors = new ArrayList<>(Collections.singleton(new Contributor(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396"), 0)));

    public static String getMessage(){
        return messages.length == 0 ? "" : messages[index++%messages.length];
    }

    public static List<Contributor> getContributors(){
        return contributors;
    }

    public static void loadMessages(Main main){

        String language = main.getConfig().getString("lang");
        switch (language){
            case "fr":
                language="fr_FR";
                break;
            case "en":
                language="en_EN";
                break;
        }
        try {

            URL url = new URL("https://api.ph1lou.fr/messages/"+language);
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

                messages = new Gson().fromJson(response.toString(), String[].class);
            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
    }

    public static void loadContributors(Main main){

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

    public static void postGame(Main main, @NotNull GameReview gameReview){

        if (gameReview.getWinnerCampKey() == null) return;

        String jsonInputString = Serializer.serialize(gameReview);
        File file = new File(main.getDataFolder() + File.separator + "statistiks", gameReview.getGameUUID() + ".json");

        FileUtils_.save(file, jsonInputString);


        if (gameReview.getPlayersCount() < 17) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because player size < 17");
            return;
        }

        if (main.getWereWolfAPI().getTimer() < 3600) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because game duration < 1h");
            return;
        }

        if(((GameManager) main.getWereWolfAPI()).isCrack()){
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because Server Crack");
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

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                TextComponent msg = new TextComponent(response.toString());
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        String.format("https://werewolf.ph1lou.fr/game-view/%s", gameReview.getGameUUID().toString())));
                BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getOnlinePlayers()
                        .forEach(player -> player.spigot().sendMessage(msg)), 100);
            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
    }
}
