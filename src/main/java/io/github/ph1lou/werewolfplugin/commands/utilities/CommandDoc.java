package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class CommandDoc implements Commands {


    private final Main main;

    public CommandDoc(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        TextComponent textComponent1 = new TextComponent(game.translate("werewolf.commands.doc.link"));

        textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.google.com/spreadsheets/d/1jkos7zslxBl6NCWy4FXuQXtFkvtPLa9e6UOlpmcsDm0/edit?usp=sharing"));

        player.spigot().sendMessage(textComponent1);
    }
}
