package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Thief extends RolesNeutral implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Thief(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.thief.description");
    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.DAMAGE_RESISTANCE,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                1200,
                0,
                false,
                false));
        killer.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.ABSORPTION,
                        1200,
                        0,
                        false,
                        false));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstDeathEvent(FirstDeathEvent event){

        UUID uuid = event.getUuid();

        PlayerWW plg = game.getPlayersWW().get(uuid);

        if(plg.getLastKiller()==null) return;

        if(!plg.getLastKiller().equals(getPlayerUUID())) return;

        if(!hasPower())return;

        event.setCancelled(true);

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (!game.isState(StateGame.END)) {
                if (game.getPlayersWW()
                        .get(getPlayerUUID())
                        .isState(StatePlayer.ALIVE)
                        && hasPower()) {
                    thief_recover_role(getPlayerUUID(), uuid);
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
                        if (!game.isState(StateGame.END)) {
                            Bukkit.getPluginManager().callEvent(
                                    new FirstDeathEvent(uuid));
                        }

                    }, 20L);
                }
            }

        },7*20);
    }


    public void thief_recover_role(UUID killerUUID,UUID playerUUID) {

        PlayerWW plg = game.getPlayersWW().get(playerUUID);
        Roles role = plg.getRole();
        PlayerWW klg = game.getPlayersWW().get(killerUUID);
        String killerName = klg.getName();
        Player killer = Bukkit.getPlayer(killerUUID);
        boolean isInfected = klg.getRole().getInfected();


        if (killer != null) {

            setPower(false);
            klg.setThief(true);
            HandlerList.unregisterAll((Listener) klg.getRole());
            Roles roleClone = role.publicClone();
            klg.setRole(roleClone);
            Objects.requireNonNull(roleClone).setPlayerUUID(killerUUID);
            Bukkit.getPluginManager().registerEvents((Listener) roleClone, (Plugin) main);

            if (isInfected) {
                roleClone.setInfected();
            } else if (roleClone.isWereWolf()) {
                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(killerUUID));
            }

            killer.sendMessage(game.translate("werewolf.role.thief.realized_theft",
                    game.translate(role.getKey())));
            killer.sendMessage(game.translate("werewolf.announcement.review_role"));

            killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            Bukkit.getPluginManager().callEvent(new StealEvent(killerUUID,
                    playerUUID,
                    roleClone.getKey()));


            klg.getRole().recoverPotionEffect();
            klg.getRole().recoverPowerAfterStolen();

            if (klg.getCursedLovers() != null ||
                    klg.getAmnesiacLoverUUID() != null) {
                Bukkit.getConsoleSender()
                        .sendMessage("[WereWolfPlugin] Thief in special lover");
            } else if (!klg.getLovers().isEmpty() &&
                    !game.getConfig().getConfigValues()
                            .get(ConfigsBase.POLYGAMY.getKey())) {
                Bukkit.getConsoleSender()
                        .sendMessage("[WereWolfPlugin] Thief in lover & no polygamy");
            } else if (!klg.getLovers().isEmpty() || !plg.getLovers().isEmpty()) {

                if (!klg.getLovers().contains(playerUUID)) {

                    plg.getLovers()
                            .stream()
                            .map(game::getPlayerWW)
                            .filter(Objects::nonNull)
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .peek(playerWW -> {
                                playerWW.addLover(killerUUID);
                                playerWW.removeLover(playerUUID);
                                killer.sendMessage(game.translate(
                                        "werewolf.role.lover.description",
                                        playerWW.getName()));
                                Sounds.SHEEP_SHEAR.play(killer);
                            })
                            .map(PlayerWW::getRole)
                            .map(Roles::getPlayerUUID)
                            .map(Bukkit::getPlayer)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .ifPresent(player -> {
                                player.sendMessage(game.translate(
                                        "werewolf.role.lover.description",
                                        killerName));
                                Sounds.SHEEP_SHEAR.play(player);
                            });

                    plg.clearLovers();

                    game.getPlayersWW().values()
                            .stream()
                            .map(PlayerWW::getRole)
                            .filter(roles -> roles.isKey(RolesBase.CUPID.getKey()))
                            .map(roles -> (AffectedPlayers) roles)
                            .filter(affectedPlayers -> affectedPlayers
                                    .getAffectedPlayers().contains(playerUUID))
                            .findFirst()
                            .ifPresent(affectedPlayers -> {
                                affectedPlayers.addAffectedPlayer(killerUUID);
                                affectedPlayers.removeAffectedPlayer(playerUUID);
                            });
                    thiefLoversRange(killerUUID, playerUUID);
                }
            }
            else if (plg.getAmnesiacLoverUUID()!=null) {

                UUID uuid = plg.getAmnesiacLoverUUID();
                plg.setAmnesiacLoverUUID(null);
                Player pc = Bukkit.getPlayer(uuid);
                PlayerWW llg = game.getPlayersWW().get(uuid);

                if(llg.isState(StatePlayer.ALIVE)){

                    klg.setAmnesiacLoverUUID(uuid);
                    llg.setAmnesiacLoverUUID(killerUUID);

                    if(plg.getRevealAmnesiacLover()) {
                        klg.setRevealAmnesiacLover(true);
                        if (pc != null) {
                            pc.sendMessage(game.translate("werewolf.role.lover.description",
                                    killerName));
                            Sounds.PORTAL_TRAVEL.play(pc);
                        }
                        killer.sendMessage(game.translate("werewolf.role.lover.description",
                                llg.getName()));

                        game.getAmnesiacLoversRange()
                                .stream()
                                .filter(uuids -> uuids.contains(playerUUID))
                                .findFirst()
                                .ifPresent(uuids -> {
                                    uuids.add(killerUUID);
                                    uuids.remove(playerUUID);
                                });
                    }
                }
            }
            else if (plg.getCursedLovers()!=null) {

                UUID uuid = plg.getCursedLovers();
                plg.setCursedLover(null);
                PlayerWW llg = game.getPlayersWW().get(uuid);
                Player pc = Bukkit.getPlayer(uuid);

                if(llg.isState(StatePlayer.ALIVE)) {

                    klg.setCursedLover(uuid);
                    llg.setCursedLover(killerUUID);

                    if (pc != null) {
                        pc.sendMessage(game.translate(
                                "werewolf.role.cursed_lover.description",
                                killerName));
                        Sounds.SHEEP_SHEAR.play(pc);
                    }
                    killer.sendMessage(game.translate(
                            "werewolf.role.cursed_lover.description",
                            llg.getName()));
                    Sounds.SHEEP_SHEAR.play(killer);
                    VersionUtils.getVersionUtils().setPlayerMaxHealth(killer,
                            VersionUtils.getVersionUtils()
                                    .getPlayerMaxHealth(killer) + 2);

                    game.getCursedLoversRange()
                            .stream()
                            .filter(uuids -> uuids.contains(playerUUID))
                            .findFirst()
                            .ifPresent(uuids -> {
                                uuids.add(killerUUID);
                                uuids.remove(playerUUID);
                            });
                }
            }
        }
        game.death(playerUUID);
    }

    public void thiefLoversRange(UUID killerUUID, UUID playerUUID) {


        int cp = -1;
        int ck = -1;


        for (int i = 0; i < game.getLoversRange().size(); i++) {
            List<UUID> loverList = game.getLoversRange().get(i);
            if (loverList.contains(playerUUID) && !loverList.contains(killerUUID)) {
                loverList.remove(playerUUID);
                loverList.add(killerUUID);
                cp = i;
            } else if (!loverList.contains(playerUUID) &&
                    loverList.contains(killerUUID)) {
                ck = i;
            }
        }
        if (cp != -1 && ck != -1) {
            game.getLoversRange().get(ck).remove(killerUUID);
            game.getLoversRange().get(cp).addAll(game.getLoversRange().get(ck));
            game.getLoversRange().remove(ck);
        }
    }
    
    @EventHandler
    public void onDay(DayEvent event) {
        restoreResistance();
    }

    @EventHandler
    public void onNight(NightEvent event){
        restoreResistance();
    }


    public void restoreResistance() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!hasPower()) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }
}
