package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.events.HostEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHost implements Commands {


    private final Main main;

    public CommandHost(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();
        ModerationManagerAPI moderationManager = game.getModerationManager();
        Player host = Bukkit.getPlayer(args[0]);

        if (host == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = host.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.remove", args[0]));
            moderationManager.getHosts().remove(uuid);
            if (moderationManager.getModerators().contains(uuid)) {
                if (game.isState(StateGame.LOBBY) && game.getPlayersWW().containsKey(uuid)) {
                    game.getScore().removePlayerSize();
                    game.getPlayersWW().remove(uuid);
                }
            }
        } else {
            if (game.isState(StateGame.LOBBY) && !game.getPlayersWW().containsKey(uuid)) {
                game.finalJoin(host);
            }
            moderationManager.getHosts().add(uuid);
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.add", args[0]));
        }
        Bukkit.getPluginManager().callEvent(new HostEvent(uuid, moderationManager.getHosts().contains(uuid)));
    }
}
