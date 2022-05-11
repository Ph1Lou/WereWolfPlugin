package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfplugin.commands.Admin;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ModerationManager implements IModerationManager {

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


        while (!temp.isEmpty() && game.getConfig().getPlayerMax() > game.getPlayersCount()) {

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

        if (!this.queue.contains(uuid)) {
            this.queue.add(uuid);
            Bukkit.broadcastMessage(this.game.translate(Prefix.YELLOW , "werewolf.announcement.queue",
                    Formatter.player(player.getName()),
                    Formatter.number(this.queue.indexOf(uuid) + 1)));
            player.sendMessage(this.game.translate("werewolf.announcement.rank"));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
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


    @Override
    public void alertModerators(String message) {
        alert(moderators, message);
    }

    @Override
    public void alertHostsAndModerators(String message) {
        alert(hosts, message);
        alert(moderators, message);
    }

    @Override
    public void alertHosts(String message) {
        alert(hosts, message);
    }


    private void alert(List<UUID> players, String message) {
        players.stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList())
                .forEach(player1 -> {
                    if (player1 != null) {
                        player1.sendMessage(message);
                    }
                });
    }

    @Override
    public boolean isStaff(UUID uuid) {
        return hosts.contains(uuid) || moderators.contains(uuid);
    }

    @Override
    public boolean checkAccessAdminCommand(String commandKey, Player player) {
        return checkAccessAdminCommand(commandKey, player, true);
    }

    @Override
    public boolean checkAccessAdminCommand(String commandKey, Player player, boolean seePermissionMessages) {
        return Admin.get().checkAccess(commandKey, player, seePermissionMessages);
    }

}
