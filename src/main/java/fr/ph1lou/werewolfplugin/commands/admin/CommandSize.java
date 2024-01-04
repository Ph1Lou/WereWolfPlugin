package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.size.command",
        descriptionKey = "werewolf.commands.admin.size.description",
        argNumbers = 0,
        statesGame = StateGame.LOBBY,
        moderatorAccess = true)
public class CommandSize implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        World world = game.getMapManager().getWorld();

        Location location = world.getSpawnLocation();
        player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.size.begin"));
        int size = VersionUtils.getVersionUtils().biomeSize(location, world);
        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.size.result",
                Formatter.number(size)));

        TextComponent msg = VersionUtils.getVersionUtils().createClickableText(game.translate(Prefix.YELLOW, "werewolf.commands.admin.size.change"),
                String.format("/a %s", game.translate("werewolf.commands.admin.change.command")),
                ClickEvent.Action.RUN_COMMAND);
        player.spigot().sendMessage(msg);
    }
}
