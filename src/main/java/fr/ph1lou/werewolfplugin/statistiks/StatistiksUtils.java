package fr.ph1lou.werewolfplugin.statistiks;

import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.FileUtils_;
import fr.ph1lou.werewolfplugin.save.Serializer;
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

public class StatistiksUtils {

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

                TextComponent msg = new TextComponent(main.getWereWolfAPI().translate(response.toString()));
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
