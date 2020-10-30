package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModerationManager implements ModerationManagerAPI {

    private final List<UUID> queue = new ArrayList<>();
    private final List<UUID> whiteListedPlayers = new ArrayList<>();
    private final List<UUID> hosts = new ArrayList<>();
    private final List<UUID> moderators = new ArrayList<>();
    private final GameManager game;

    public ModerationManager(GameManager game) {
        this.game = game;
    }


    public void checkQueue() {

        if (!game.isState(StateGame.LOBBY)) return;

        List<UUID> temp = new ArrayList<>(queue);
        int i = 0;


        while (!temp.isEmpty() && game.getConfig().getPlayerMax() > game.getPlayersWW().size()) {

            Player player = Bukkit.getPlayer(temp.get(0));
            if (player != null && (!game.getConfig().isWhiteList() || getWhiteListedPlayers().contains(temp.get(0)))) {
                queue.remove(i);
                game.join(player);
            } else i++;
            temp.remove(0);
        }
    }

    public void addQueue(Player player) {

        UUID uuid = player.getUniqueId();

        if (!getQueue().contains(uuid)) {
            queue.add(uuid);
            Bukkit.broadcastMessage(game.translate("werewolf.announcement.queue", player.getName(), queue.indexOf(uuid) + 1));
            player.sendMessage(game.translate("werewolf.announcement.rank"));
        }
    }

    @Override
    public List<UUID> getWhiteListedPlayers() {
        return whiteListedPlayers;
    }

    @Override
    public void setWhiteListedPlayers(List<UUID> whiteListedPlayers) {
        this.whiteListedPlayers.clear();
        this.whiteListedPlayers.addAll(whiteListedPlayers);
    }

    @Override
    public void addPlayerOnWhiteList(UUID whiteListedPlayer) {
        this.whiteListedPlayers.add(whiteListedPlayer);
    }

    @Override
    public void removePlayerOnWhiteList(UUID whiteListedPlayer) {
        this.whiteListedPlayers.remove(whiteListedPlayer);
    }

    @Override
    public List<UUID> getHosts() {
        return hosts;
    }

    @Override
    public void setHosts(List<UUID> hostsUUIDs) {
        this.hosts.clear();
        this.hosts.addAll(hostsUUIDs);
    }

    @Override
    public void addHost(UUID host) {
        this.hosts.add(host);
    }

    @Override
    public void removeHost(UUID host) {
        this.hosts.remove(host);
    }

    @Override
    public List<UUID> getModerators() {
        return moderators;
    }

    @Override
    public void setModerators(List<UUID> moderatorsUUIDs) {
        this.moderators.clear();
        this.moderators.addAll(moderatorsUUIDs);
    }

    @Override
    public void addModerator(UUID moderator) {
        this.moderators.add(moderator);
    }

    @Override
    public void removeModerator(UUID moderator) {
        this.moderators.remove(moderator);
    }

    @Override
    public List<UUID> getQueue() {
        return this.queue;
    }


}
