package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Gravedigger extends RoleVillage implements IAffectedPlayers {

    public Gravedigger(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private final List<GravediggerClue> clues = new ArrayList<>();
    private int secondsCount = 0;

    @Override
    public String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.role.gravedigger.description")).build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(this.getPlayerWW())) {
            return;
        }

        if (getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }

        Location deathLocation = playerWW.isState(StatePlayer.ALIVE) ? playerWW.getLocation() : playerWW.getSpawn();
        Set<IPlayerWW> nearbyPlayers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> {
                    try {
                        return deathLocation.distance(player.getLocation()) < 70;
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(player -> player.isState(StatePlayer.ALIVE))
                .filter(player -> !player.equals(playerWW))
                .collect(Collectors.toSet());

        clues.add(new GravediggerClue(playerWW, deathLocation, nearbyPlayers));

        if (!isAbilityEnabled()) {
            return;
        }

        double angle;
        Location playerLocation = getPlayerWW().getLocation();
        if (playerWW.getRole().getCamp() != Camp.VILLAGER) {
            angle = game.getRandom().nextDouble() * 2 * Math.PI;
        } else {
            double diffX = deathLocation.getX() - playerLocation.getX();
            double diffZ = deathLocation.getZ() - playerLocation.getZ();
            angle = Math.atan2(diffZ, diffX);
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        double baseX = Math.cos(angle);
        double baseZ = Math.sin(angle);

        for (int i = 0; i < 20; i++) {
            Location effectLoc = new Location(playerLocation.getWorld(), playerLocation.getX() + i * baseX, playerLocation.getY(), playerLocation.getZ() + i * baseZ);
            player.playEffect(effectLoc, Effect.MOBSPAWNER_FLAMES, null);
        }

    }

    @Override
    public void second() {
        if (!(++secondsCount % 10 == 0)) {
            return;
        }
        secondsCount = 0;
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!isAbilityEnabled()) return;

        Location location = getPlayerWW().getLocation();
        double distanceOrigin = location.length();
        clues.forEach(clue -> {
            //double comparisons are faster than Location comparisons
            if (clue.getDistanceToOrigin() + 20 < distanceOrigin || clue.getDistanceToOrigin() - 20 > distanceOrigin) {
                return;
            }
            if (clue.getDeathLocation().distance(location) > 20) {
                return;
            }
            clue.incrementCount();
            switch (clue.getCount()) {
                case 6:
                    affectedPlayers.add(clue.getPlayerWW());
                    getPlayerWW().sendMessageWithKey("werewolf.role.gravedigger.clue_player", fr.ph1lou.werewolfapi.player.utils.Formatter.format("&player&", clue.getPlayerWW().getName()),
                            fr.ph1lou.werewolfapi.player.utils.Formatter.format("&number&", Integer.toString(clue.getNearbyPlayers().size())));
                    return;
                case 12:
                    getPlayerWW().sendMessageWithKey("werewolf.role.gravedigger.clue_role", fr.ph1lou.werewolfapi.player.utils.Formatter.format("&victim&", clue.getPlayerWW().getName()),
                            fr.ph1lou.werewolfapi.player.utils.Formatter.format("&role&", game.translate(clue.getPlayerWW().getRole().getKey())),
                            fr.ph1lou.werewolfapi.player.utils.Formatter.format("&players&", buildNamesString(clue.getNamesList(), 1)));
                    return;
                case 18:
                    getPlayerWW().sendMessageWithKey("werewolf.role.gravedigger.clue_nearby", fr.ph1lou.werewolfapi.player.utils.Formatter.format("&players&",  buildNamesString(clue.getNamesList(), 2)),
                            fr.ph1lou.werewolfapi.player.utils.Formatter.format("&victim&", clue.getPlayerWW().getName()));
                    return;
                case 24:
                    getPlayerWW().sendMessageWithKey("werewolf.role.gravedigger.clue_nearby", fr.ph1lou.werewolfapi.player.utils.Formatter.format("&players&", buildNamesString(clue.getNamesList(), 3)),
                            Formatter.format("&victim&", clue.getPlayerWW().getName()));
                    clues.remove(clue);
                    return;
                default:
                    return;
            }

        });
    }

    private String buildNamesString(List<String> names, int thirds) {
        int nReadable = names.size() * thirds / 3;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (i < nReadable) {
                list.add(names.get(i));
            } else {
                list.add(ChatColor.MAGIC + " Coucou");
            }
        }
        return list.toString();
    }

    private class GravediggerClue {

        private final IPlayerWW playerWW;
        private final Location deathLocation;
        private final double distanceToOrigin;
        private final Set<IPlayerWW> nearbyPlayers;
        private List<String> playerNames;
        private int count;

        private GravediggerClue(IPlayerWW playerWW, Location deathLocation, Set<IPlayerWW> nearbyPlayers) {
            this.playerWW = playerWW;
            this.deathLocation = deathLocation;
            this.distanceToOrigin = deathLocation.length();
            this.nearbyPlayers = nearbyPlayers;
            this.playerNames = nearbyPlayers.stream().map(IPlayerWW::getName).collect(Collectors.toList());
            Collections.shuffle(playerNames);
            this.count = 0;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            count++;
        }

        public IPlayerWW getPlayerWW() {
            return playerWW;
        }

        public Location getDeathLocation() {
            return deathLocation;
        }

        public double getDistanceToOrigin() {
            return distanceToOrigin;
        }

        public Set<IPlayerWW> getNearbyPlayers() {
            return nearbyPlayers;
        }

        public List<String> getNamesList() {
            return playerNames;
        }

    }
}
