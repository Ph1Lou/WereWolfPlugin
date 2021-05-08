package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.permissions.HostEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHost implements ICommands {


    private final Main main;

    public CommandHost(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();
        IModerationManager moderationManager = game.getModerationManager();
        Player host = Bukkit.getPlayer(args[0]);

        if (host == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = host.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {

            if (moderationManager.getHosts().size() == 1) {
                player.sendMessage(game.translate("werewolf.commands.admin.host.one"));
                return;
            }
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.remove", host.getName()));
            moderationManager.getHosts().remove(uuid);

        } else {
            if (game.isState(StateGame.LOBBY) && game.getPlayerWW(uuid) == null &&
                    !game.getModerationManager().getModerators().contains(uuid)) {
                game.finalJoin(host);
            }
            moderationManager.addHost(uuid);
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.add", host.getName()));
        }
        Bukkit.getPluginManager().callEvent(new HostEvent(uuid, moderationManager.getHosts().contains(uuid)));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(host));
    }
}
