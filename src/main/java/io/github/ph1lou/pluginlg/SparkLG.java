package io.github.ph1lou.pluginlg;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import spark.Spark;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SparkLG {
    private int port= 4567;
    private String ip;
    private String name ="";
    private String hour;
    private String token = "";
    private Boolean host = false;
    final List<String> temp = new ArrayList<>(Collections.singletonList("HostBot"));
    final MainLG main;

    public SparkLG(MainLG main) {

        this.main = main;
        if (!main.getConfig().getBoolean("sendInfoToBotEnable")) return;

        if (main.getConfig().getString("port") != null) {
            this.port = main.getConfig().getInt("port");
            Spark.port(this.port);
        }
        if (main.getConfig().getString("token") != null) {
            this.token = main.getConfig().getString("token");
        }

        if (main.getConfig().getString("ip") == null) {
            ip = Bukkit.getIp();
        } else ip = main.getConfig().getString("ip");

        Spark.get("/status", (req, res) -> {
            Map<String, String> status = new HashMap<>();
            status.put("ad", String.valueOf(this.host));
            status.put("day", String.valueOf(main.getDay().ordinal()));
            status.put("host", main.score.getHost());
            status.put("hour", this.hour);
            status.put("lang", main.getConfig().getString("lang"));
            status.put("name", this.name);
            status.put("playerSize", String.valueOf(main.score.getPlayerSize()));
            status.put("roleSize", String.valueOf(main.score.getRole()));
            status.put("state", String.valueOf(main.getState().ordinal()));
            status.put("timer", String.valueOf(main.score.getTimer()));

            status.put("win", main.endlg.getVictoryTeam());
            return new Gson().toJson(status);
        });

        Spark.get("/config", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.config);
        });
        Spark.get("/players", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.playerLG);
        });

        Spark.get("/emote/:name", (request, response) -> {
            String name=request.params(":name");
            if(!temp.contains(name)){
                Bukkit.broadcastMessage(String.format(main.text.getText(267),name));
            }
            return temp.add(name);
        });
        Spark.get("/message/:message", (request, response) -> {
            String message=request.params(":message");
            Bukkit.broadcastMessage(message);
            return true;
        });
        updateDiscord();
    }


    public void setHost(String name, String hour) {
        this.host = true;
        this.name = name;
        this.hour = hour;
        updateDiscord();
    }

    public void pingBot() {

        try {

            URL url = new URL("https://pluginlg.ph1lou.fr/hostbot/ping/" + ip + "/" + port + "/" + token);
            url.openStream();
        } catch (IOException ignored) {
        }
    }

    public void updateDiscord() {

        if (!main.getConfig().getBoolean("sendInfoToBotEnable")) return;
        try {

            URL url = new URL("https://pluginlg.ph1lou.fr/hostbot/" + ip + "/" + port + "/" + token);
            url.openStream();
        } catch (IOException ignored) {
        }
    }
}
