package io.github.ph1lou.pluginlg;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import spark.Spark;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparkLG {
    private int port= 4567;
    private String ip;
    private String name ="";
    private String hour;
    private Boolean host=false;
    final List<String> temp = new ArrayList<>(Collections.singletonList("HostBot"));
    final MainLG main;

    public SparkLG(MainLG main){

        this.main=main;
        if(!main.getConfig().getBoolean("sendInfoToBotEnable")) return;

        if(main.getConfig().getString("port")!=null){
            this.port=main.getConfig().getInt("port");
            Spark.port(this.port);
        }

        if(main.getConfig().getString("ip")==null){
            ip=Bukkit.getIp();
        }
        else ip=main.getConfig().getString("ip");

        Spark.get("/host", (req, res)->main.score.getHost());
        Spark.get("/player", (req, res)->main.score.getPlayerSize());
        Spark.get("/role", (req, res)->main.score.getRole());
        Spark.get("/ad", (req, res)->this.host);
        Spark.get("/win", (req, res)->main.endlg.getVictoryTeam());
        Spark.get("/state", (req, res)-> main.getState().ordinal());
        Spark.get("/day", (req, res)-> main.getDay().ordinal());
        Spark.get("/name", (req, res)->this.name);
        Spark.get("/hour", (req, res)->this.hour);
        Spark.get("/config", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.config);
        });
        Spark.get("/players", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.playerLG);
        });
        Spark.get("/stuffs", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.stufflg);
        });
        Spark.get("/lang", (request, response) -> {
            response.type("application/json");
            return main.serialize.serialize(main.lang);
        });
        Spark.get("/lovers", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(main.couple_manage.couple_range);
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


    public void setHost(String name,String hour)  {
        this.host=true;
        this.name=name;
        this.hour=hour;
        updateDiscord();
    }

    public void updateDiscord() {

        if(!main.getConfig().getBoolean("sendInfoToBotEnable")) return;
        try {
            URL url = new URL("http://62.210.45.7:4567/hostbot/"+ip+"/"+port);
            url.openStream();
        }catch(IOException ignored){
        }
    }
}
