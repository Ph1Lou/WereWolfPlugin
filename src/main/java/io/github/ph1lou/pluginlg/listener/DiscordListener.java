package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {

    final MainLG main;

    public DiscordListener(MainLG main) {
        this.main=main;
    }

/*
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        String name=event.getMember().getEffectiveName();
        if(!main.host.temp.contains(name) && event.getMessageId().equals(main.host.getId())){
            Bukkit.broadcastMessage(String.format(main.text.getText(267),name));
            main.host.temp.add(name);
        }
    }*/
}
