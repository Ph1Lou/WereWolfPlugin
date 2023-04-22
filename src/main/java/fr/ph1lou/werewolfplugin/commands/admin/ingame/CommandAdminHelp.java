package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.help.command",
        descriptionKey = "",
        moderatorAccess = true)
public class CommandAdminHelp implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IRegisterManager registerManager = Register.get();

        TextComponent textComponent1 = new TextComponent(game.translate(Prefix.GREEN, "werewolf.commands.admin.help.help"));

        for (Wrapper<ICommand, AdminCommand> adminCommand : registerManager.getAdminCommandsRegister()) {
            if (!adminCommand.getMetaDatas().descriptionKey().isEmpty()) {

                TextComponent textComponent = new TextComponent(
                        String.format("/a §b%s ",
                                game.translate(adminCommand.getMetaDatas().key())));

                textComponent.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        game.translate(adminCommand.getMetaDatas().descriptionKey()))
                                        .create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/a %s ",
                        game.translate(adminCommand.getMetaDatas().key()))));
                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
