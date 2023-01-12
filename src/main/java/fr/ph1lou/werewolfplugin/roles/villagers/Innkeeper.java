package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Role(key = RoleBase.INNKEEPER,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER,
        configValues = @IntValue(
                key = IntValueBase.INNKEEPER_DETECTION_RADIUS,
                defaultValue = 10,
                meetUpValue = 10,
                step = 1,
                item = UniversalMaterial.IRON_DOOR))
public class Innkeeper extends RoleVillage {
    private final List<ClientData> clientDatas = new ArrayList<>();
    private int availableRooms = 3;

    public Innkeeper(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.innkeeper.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (clientDatas.stream()
                .anyMatch(cliendData -> cliendData.getPlayerWW().getPlayersKills().size() > cliendData.getKills())) {
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.kill");
            clientDatas.clear();
        }
        clientDatas.stream()
                .filter(cliendData -> cliendData.getPlayerWW().isState(StatePlayer.DEATH))
                .forEach(clientData -> {
                    Optional<IPlayerWW> lastKiller = clientData.getPlayerWW().getLastKiller();
                    if (lastKiller.isPresent()) {
                        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.dead",
                                Formatter.role(lastKiller.get().getRole().getDisplayRole()));
                        availableRooms--;
                        clientDatas.remove(clientData);
                    }
                });
        if (availableRooms == 0) {
            clientDatas.clear();
            Player player = Bukkit.getPlayer(getPlayerUUID());
            if (player != null) {
                player.setWalkSpeed(player.getWalkSpeed() * 1.1f);
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.speed");
            }
        }
    }

    @EventHandler
    public void onNight(NightEvent event) {
        clientDatas.forEach(cliendData -> {
            cliendData.refreshKills();
            cliendData.getSeenPlayers().clear();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (game.isDay(Day.DAY)) cancel();
                    game.getPlayersWW().stream()
                            .filter(iPlayerWW -> !iPlayerWW.equals(getPlayerWW()))
                            .filter(iPlayerWW -> !iPlayerWW.equals(cliendData.getPlayerWW()))
                            .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE))
                            .filter(iPlayerWW -> iPlayerWW.getLocation().distance(cliendData.getPlayerWW().getLocation()) <=
                                    game.getConfig().getValue(IntValueBase.INNKEEPER_DETECTION_RADIUS))
                            .forEach(iPlayerWW -> cliendData.getSeenPlayers().add(iPlayerWW));
                }
            }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Main.class), 0, 20 * 5);
        });
    }

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {

        if (event.getPlayer().getUniqueId() != getPlayerUUID() || !this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (availableRooms == 0) {
            return;
        }

        if (game.isDay(Day.NIGHT)) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getRightClicked().getUniqueId()).orElse(null);
        if (playerWW == null) return;

        Optional<ClientData> clientDataOptional = clientDatas.stream().filter(clientData -> clientData.getPlayerWW().equals(playerWW)).findFirst();
        if (clientDataOptional.isPresent()) {
            ClientData clientData = clientDataOptional.get();
            if (clientData.getSeenPlayers().size() != 0) {
                List<IPlayerWW> playerWWS = new ArrayList<>(clientData.getSeenPlayers());
                Collections.shuffle(playerWWS);
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW,"werewolf.roles.innkeeper.seen_players",
                        Formatter.number(playerWWS.size()), Formatter.player(playerWWS.get(0).getName()));
            } else {
                getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.innkeeper.no_seen_players");
            }
            return;
        }

        if (clientDatas.size() < availableRooms) {
            clientDatas.add(new ClientData(playerWW));
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW,"werewolf.roles.innkeeper.add_client",
                    Formatter.player(playerWW.getName()));
        } else {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.innkeeper.no_more_room");
        }
    }

    private static class ClientData {
        private final IPlayerWW playerWW;
        private final Set<IPlayerWW> seenPlayers = new HashSet<>();
        private int kills;

        public ClientData(IPlayerWW playerWW) {
            this.playerWW = playerWW;
        }

        public IPlayerWW getPlayerWW() {
            return playerWW;
        }

        public void refreshKills() {
            kills = playerWW.getPlayersKills().size();
        }

        public Set<IPlayerWW> getSeenPlayers() {
            return seenPlayers;
        }

        public int getKills() {
            return kills;
        }
    }
}
