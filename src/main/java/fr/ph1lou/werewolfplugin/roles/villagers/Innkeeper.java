package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.innkeeper.ClientDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.innkeeper.ClientKillEvent;
import fr.ph1lou.werewolfapi.events.roles.innkeeper.InnkeeperHostEvent;
import fr.ph1lou.werewolfapi.events.roles.innkeeper.InnkeeperInfoMeetEvent;
import fr.ph1lou.werewolfapi.events.roles.innkeeper.InnkeeperSpeedEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Role(key = RoleBase.INNKEEPER, category = Category.VILLAGER, attributes = {RoleAttribute.VILLAGER,
        RoleAttribute.MINOR_INFORMATION}, configValues = @IntValue(key = IntValueBase.INNKEEPER_DETECTION_RADIUS,
        defaultValue = 10, meetUpValue = 10, step = 1, item = UniversalMaterial.IRON_DOOR))
public class Innkeeper extends RoleVillage implements IPower {
    private static final float defaultWalkSpeed = 0.2f;
    private final List<ClientData> clientDatas = new ArrayList<>();
    private final List<ClientData> previousClientDatas = new ArrayList<>();
    private boolean power = false;
    private int availableRooms = 3;

    public Innkeeper(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(
                        game.translate("werewolf.roles.innkeeper" + ".description"))
                .setEffects(game.translate("werewolf.roles.innkeeper.effect"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onKill(FinalDeathEvent event) {

        if (!this.isAbilityEnabled() || !this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        clientDatas.stream()
                .filter(clientData -> clientData.playerWW.equals(event.getPlayerWW()))
                .findFirst()
                .ifPresent(clientData -> {
                    String role;
                    if (clientData.playerWW.getLastKiller()
                            .isPresent()) {
                        role = game.translate(clientData.playerWW.getLastKiller()
                                .get()
                                .getRole()
                                .getKey());
                    } else {
                        role = "pve";
                    }
                    ClientDeathEvent clientDeathEvent = new ClientDeathEvent(getPlayerWW(), clientData.playerWW, role);
                    Bukkit.getPluginManager().callEvent(clientDeathEvent);
                    if (!clientDeathEvent.isCancelled()) {
                        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.dead",
                                Formatter.role(clientDeathEvent.getRole()));
                        availableRooms--;
                        clientData.watching = false;
                        clientDatas.remove(clientData);
                        if (availableRooms == 0) {
                            Player player = Bukkit.getPlayer(getPlayerUUID());
                            if (player != null) {
                                InnkeeperSpeedEvent innkeeperSpeedEvent = new InnkeeperSpeedEvent(getPlayerWW());
                                Bukkit.getPluginManager().callEvent(innkeeperSpeedEvent);
                                if (!innkeeperSpeedEvent.isCancelled()) {
                                    player.setWalkSpeed(defaultWalkSpeed * 1.1f);
                                    getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.speed");
                                }
                            }
                        }
                    }
                });
        event.getPlayerWW()
                .getLastKiller()
                .ifPresent(killer -> {
                    if (clientDatas.stream()
                            .anyMatch(clientData -> clientData.playerWW.equals(killer))) {
                        ClientKillEvent clientKillEvent = new ClientKillEvent(getPlayerWW(), killer);
                        Bukkit.getPluginManager().callEvent(clientKillEvent);
                        if (!clientKillEvent.isCancelled()) {
                            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.kill");
                            clientDatas.forEach(clientData -> clientData.watching = false);
                        }
                    }
                });
    }

    @Override
    public void disableAbilitiesRole() {
        this.power = false;
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.isAbilityEnabled() || !this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        power = true;
        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.available");
        previousClientDatas.clear();
        previousClientDatas.addAll(clientDatas);
        clientDatas.clear();
    }

    @EventHandler
    public void onNight(NightEvent event) {
        power = false;

        if (!this.isAbilityEnabled() || !this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        clientDatas.forEach(clientData -> new BukkitRunnable() {
            @Override
            public void run() {
                if (game.isDay(Day.DAY) || !clientData.watching || game.isState(StateGame.END)) {
                    cancel();
                } else {
                    game.getPlayersWW()
                            .stream()
                            .filter(iPlayerWW -> !iPlayerWW.equals(getPlayerWW()))
                            .filter(iPlayerWW -> !iPlayerWW.equals(clientData.playerWW))
                            .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE))
                            .filter(iPlayerWW -> {
                                if(iPlayerWW.getLocation().getWorld() == clientData.playerWW.getLocation().getWorld()){
                                    return iPlayerWW.getLocation()
                                                    .distance(clientData.playerWW.getLocation()) <= game.getConfig()
                                                    .getValue(IntValueBase.INNKEEPER_DETECTION_RADIUS);
                                }
                                return false;
                            })
                            .forEach(clientData.seenPlayers::add);
                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Main.class), 0, 20 * 5));
    }

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (event.getPlayer()
                .getUniqueId() != getPlayerUUID() || !this.getPlayerWW()
                .isState(StatePlayer.ALIVE)) {
            return;
        }

        if (availableRooms == 0 || !power) {
            return;
        }

        if (game.isDay(Day.NIGHT)) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getRightClicked()
                        .getUniqueId())
                .orElse(null);
        if (playerWW == null)
            return;

        Optional<ClientData> clientDataOptional = previousClientDatas.stream()
                .filter(clientData -> clientData.playerWW.equals(playerWW))
                .findFirst();
        if (clientDataOptional.isPresent()) {
            ClientData clientData = clientDataOptional.get();
            if (!clientData.seenPlayers.isEmpty()) {
                List<IPlayerWW> playerWWS = new ArrayList<>(clientData.seenPlayers);
                Collections.shuffle(playerWWS, game.getRandom());
                InnkeeperInfoMeetEvent innkeeperInfoMeetEvent = new InnkeeperInfoMeetEvent(getPlayerWW(),
                        playerWWS.get(0),
                        playerWWS.size());

                this.setPower(false);

                Bukkit.getPluginManager()
                        .callEvent(innkeeperInfoMeetEvent);
                if (!innkeeperInfoMeetEvent.isCancelled()) {
                    getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.seen_players",
                            Formatter.number(playerWWS.size()), Formatter.player(playerWWS.get(0)
                                    .getName()));
                }
            } else {
                getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.innkeeper.no_seen_players");
            }
            return;
        }
        if (clientDatas.stream()
                .anyMatch(clientData -> clientData.playerWW.equals(playerWW))) {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.innkeeper.already");
        } else if (clientDatas.size() < availableRooms) {
            InnkeeperHostEvent hostEvent = new InnkeeperHostEvent(getPlayerWW(), playerWW);
            Bukkit.getPluginManager()
                    .callEvent(hostEvent);
            if (!hostEvent.isCancelled()) {
                clientDatas.add(new ClientData(playerWW));
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.innkeeper.add_client",
                        Formatter.player(playerWW.getName()));
            }
        } else {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.innkeeper.no_more_room");
        }
    }

    @Override
    public void setPower(boolean b) {
        this.power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    private static class ClientData {
        private final IPlayerWW playerWW;
        private final Set<IPlayerWW> seenPlayers = new HashSet<>();
        private boolean watching = true;

        public ClientData(IPlayerWW playerWW) {
            this.playerWW = playerWW;
        }
    }
}
