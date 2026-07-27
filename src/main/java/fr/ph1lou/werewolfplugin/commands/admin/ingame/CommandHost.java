package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerBukkitArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.permissions.HostEvent;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.host.command",
        descriptionKey = "werewolf.commands.admin.host.description",
        argNumbers = 1,
        args = @Params(bukkitPlayers = @PlayerBukkitArg(index = 0)))
public class CommandHost implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        IModerationManager moderationManager = game.getModerationManager();
        Player host = ctx.bukkitPlayer(0);

        UUID uuid = host.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {

            if (moderationManager.getHosts().size() == 1) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.host.one"));
                return;
            }
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.commands.admin.host.remove",
                    Formatter.player(host.getName())));
            moderationManager.getHosts().remove(uuid);

        } else {
            if (game.isState(StateGame.LOBBY) && !game.getPlayerWW(uuid).isPresent() &&
                    !game.getModerationManager().getModerators().contains(uuid)) {
                ((GameManager) game).finalJoin(host);
            }
            moderationManager.addHost(uuid);
            Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.host.add",
                    Formatter.player(host.getName())));
        }
        Bukkit.getPluginManager().callEvent(new HostEvent(uuid, moderationManager.getHosts().contains(uuid)));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(host));
    }
}
