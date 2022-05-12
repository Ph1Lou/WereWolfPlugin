package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.doc.command",
        descriptionKey = "werewolf.commands.doc.description",
        argNumbers = 0)
public class CommandDoc implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        TextComponent textComponent1 = new TextComponent(game.translate(Prefix.ORANGE , "werewolf.commands.doc.link"));

        textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, game.translate("werewolf.description.doc")));

        player.spigot().sendMessage(textComponent1);
    }
}
