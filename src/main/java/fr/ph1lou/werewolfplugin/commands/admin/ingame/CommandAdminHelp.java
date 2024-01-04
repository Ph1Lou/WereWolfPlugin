package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.ClickEvent;
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

                TextComponent textComponent = VersionUtils.getVersionUtils().createClickableText(
                        String.format("/a §b%s ",
                                game.translate(adminCommand.getMetaDatas().key())),
                        String.format("/a %s ",
                                game.translate(adminCommand.getMetaDatas().key())),
                        ClickEvent.Action.SUGGEST_COMMAND,
                        game.translate(adminCommand.getMetaDatas().descriptionKey()));

                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
