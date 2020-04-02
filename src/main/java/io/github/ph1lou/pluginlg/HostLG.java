package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.listener.DiscordListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HostLG {

    final MainLG main;
    private String id="";
    private String message="";
    private String playername="";
    private String channelId = "";
    public final List<String> temp = new ArrayList<>(Collections.singletonList("HostBot"));
    public JDA jda;

    public HostLG(MainLG main){
        try {
            jda = new JDABuilder(main.getConfig().getString("token"))
                    .addEventListeners(new DiscordListener(main))
                    .build();
            channelId=main.getConfig().getString("channelId");
        } catch (LoginException e) {
            e.printStackTrace();
        }
        this.main=main;
    }

    public void sendHostToDiscord(String playername, String message){
        this.message=message;
        this.playername=playername;
        createEmbed();
    }

    private void createEmbed() {

        EmbedBuilder em = new EmbedBuilder();
        em.setAuthor(playername);
        em.setColor(Color.CYAN);
        em.setDescription(main.score.getHost()+" lance à "+message+" une Game de LG UHC\nIP: " +Bukkit.getIp());
        em.setTitle("Annonce host ");
        StringBuilder sb = new StringBuilder();
        for(String p:main.playerLG.keySet()){
            sb.append(p).append(" ");
        }
        em.setFooter(sb.toString());
        if(!id.isEmpty()){
            Objects.requireNonNull(jda.getTextChannelById(channelId)).editMessageById(id,em.build()).queue();
        }
        else {
            Objects.requireNonNull(jda.getTextChannelById(channelId)).sendMessage(em.build()).queue(m -> {
                m.addReaction("✔").queue();
                this.id=m.getId();
            });
        }
    }

    public String getId(){
        return this.id;
    }

}
