package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.CommandRegister;
import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.RegisterManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class CommandAdminHelp implements Commands {


    private final Main main;

    public CommandAdminHelp(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        RegisterManager registerManager = main.getRegisterManager();

        TextComponent textComponent1 = new TextComponent(game.translate("werewolf.commands.admin.help.help"));

        for (CommandRegister adminCommand : registerManager.getAdminCommandsRegister()) {
            if (!adminCommand.getDescription().isEmpty() &&
                    game.getModerationManager()
                            .checkAccessAdminCommand(adminCommand.getKey(), player, false)) {

                TextComponent textComponent = new TextComponent(
                        String.format("/a §b%s ",
                                game.translate(adminCommand.getKey())));

                textComponent.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        game.translate(adminCommand.getDescription()))
                                        .create()));
                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
