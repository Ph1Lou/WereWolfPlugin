package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.doc.command",
        descriptionKey = "werewolf.commands.player.doc.description",
        argNumbers = 0)
public class CommandDoc implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        TextComponent textComponent1 = VersionUtils.getVersionUtils().createClickableText(game.translate(Prefix.ORANGE, "werewolf.commands.player.doc.link"),
                game.translate("werewolf.description.doc"),
                ClickEvent.Action.OPEN_URL);

        player.spigot().sendMessage(textComponent1);
    }
}
