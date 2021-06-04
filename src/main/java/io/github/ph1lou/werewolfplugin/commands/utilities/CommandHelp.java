package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class CommandHelp implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IRegisterManager registerManager = RegisterManager.get();

        TextComponent textComponent1 = new TextComponent(game.translate("werewolf.commands.admin.help.help"));

        for (CommandRegister command : registerManager.getCommandsRegister()) {
            if (!command.getDescription().isEmpty()) {

                TextComponent textComponent = new TextComponent(
                        String.format("/ww §b%s ",
                                game.translate(command.getKey())));

                textComponent.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        game.translate(command.getDescription()))
                                        .create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,String.format("/ww %s ",
                        game.translate(command.getKey()))));
                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
