package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.events.FirstDeathEvent;
import io.github.ph1lou.pluginlgapi.events.NewWereWolfEvent;
import io.github.ph1lou.pluginlgapi.events.StealEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Thief extends RolesNeutral implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Thief(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
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
    public String getDescription() {
        return game.translate("werewolf.role.thief.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.thief.display";
    }

    @Override
    public void recoverPotionEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if(event.getEntity() == null) return;
        if(event.getEntity().getKiller()==null) return;
        Player killer = event.getEntity().getKiller();

        if(!killer.getUniqueId().equals(getPlayerUUID())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0, false, false));
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event){

        UUID uuid = event.getUuid();

        PlayerWW plg = game.getPlayersWW().get(uuid);

        if(plg.getLastKiller()==null) return;

        if(!plg.getLastKiller().equals(getPlayerUUID())) return;

        if(!hasPower())return;

        setPower(false);


        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
                event.setCancelled(true);
                thief_recover_role(getPlayerUUID(), uuid);
            }
        },7*20);
    }


    public void thief_recover_role(UUID killerUUID,UUID playerUUID){

        PlayerWW plg = game.getPlayersWW().get(playerUUID);
        Roles role = plg.getRole();
        PlayerWW klg = game.getPlayersWW().get(killerUUID);
        String killerName = klg.getName();
        Boolean isInfected = klg.getRole().getInfected();

        Roles roleClone= role.publicClone();
        Bukkit.getPluginManager().registerEvents((Listener) roleClone, (Plugin) main);
        klg.setRole(roleClone);
        roleClone.setPlayerUUID(killerUUID);
        if(isInfected) {
            roleClone.setInfected(true);
        }
        else if(roleClone.isWereWolf()) {

            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(killerUUID));
        }
        klg.setThief(true);

        if (Bukkit.getPlayer(killerUUID)!=null) {

            Player killer = Bukkit.getPlayer(killerUUID);

            killer.sendMessage(game.translate("werewolf.role.thief.realized_theft", game.translate(role.getDisplay())));
            killer.sendMessage(game.translate("werewolf.announcement.review_role"));

            if (!klg.hasSalvation()) {
                killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
            klg.getRole().recoverPotionEffect(killer);
            klg.getRole().stolen(playerUUID);
            Bukkit.getPluginManager().callEvent(new StealEvent(killerUUID,playerUUID));

            if (!plg.getLovers().isEmpty() && !klg.getLovers().contains(playerUUID) && !plg.getLovers().contains(klg.getCursedLovers())) {

                for (UUID uuid1 : plg.getLovers()) {

                    PlayerWW llg =game.getPlayersWW().get(uuid1);
                    if(llg.isState(State.ALIVE) ){

                        klg.addLover(uuid1);
                        llg.addLover(killerUUID);
                        llg.removeLover(playerUUID);


                        if (Bukkit.getPlayer(uuid1) != null) {
                            Player pc = Bukkit.getPlayer(uuid1);
                            pc.sendMessage(game.translate("werewolf.role.lover.description", killerName));
                            pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
                        }
                        killer.sendMessage(game.translate("werewolf.role.lover.description", llg.getName()));
                        killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);

                    }
                }
                if (!klg.getLovers().contains(killerUUID)) {
                    plg.clearLovers();
                }

                for (UUID uuid2 : game.getPlayersWW().keySet()) {
                    PlayerWW plc =game.getPlayersWW().get(uuid2);
                    if (plc.getRole().isDisplay("werewolf.role.cupid.display")){
                        Cupid cupid = (Cupid) plc.getRole();
                        if(cupid.getAffectedPlayers().contains(playerUUID)) {
                            cupid.addAffectedPlayer(killerUUID);
                            cupid.removeAffectedPlayer(playerUUID);
                        }
                    }
                }
                thiefLoversRange(killerUUID, playerUUID);
            }


            if (plg.getAmnesiacLoverUUID()!=null && klg.getAmnesiacLoverUUID()==null && klg.getLovers().isEmpty()) {

                UUID uuid= plg.getAmnesiacLoverUUID();
                PlayerWW llg =game.getPlayersWW().get(uuid);

                if(llg.isState(State.ALIVE)){

                    klg.setAmnesiacLoverUUID(uuid);
                    llg.setAmnesiacLoverUUID(killerUUID);

                    if(plg.getRevealAmnesiacLover()){
                        klg.setRevealAmnesiacLover(true);
                        if (Bukkit.getPlayer(uuid) != null) {
                            Player pc = Bukkit.getPlayer(uuid);
                            pc.sendMessage(game.translate("werewolf.role.lover.description", killerName));
                            pc.playSound(pc.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
                        }
                        killer.sendMessage(game.translate("werewolf.role.lover.description", llg.getName()));
                        killer.playSound(killer.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);

                        for (int i = 0; i < game.getAmnesiacLoversRange().size(); i++) {
                            if (game.getAmnesiacLoversRange().get(i).contains(playerUUID)) {
                                game.getAmnesiacLoversRange().get(i).add(killerUUID);
                                game.getAmnesiacLoversRange().get(i).remove(playerUUID);
                                break;
                            }
                        }
                    }
                }
            }

            //Si le voleur est déjà en couple (maudit ou normal), il ne le vole pas

            if (plg.getCursedLovers()!=null && klg.getCursedLovers()==null) {

                UUID uuid = plg.getCursedLovers();
                PlayerWW llg =game.getPlayersWW().get(uuid);

                if (!klg.getLovers().contains(uuid) && !klg.getAmnesiacLoverUUID().equals(uuid)) {

                    if(llg.isState(State.ALIVE)){

                        klg.setCursedLover(uuid);
                        llg.setCursedLover(killerUUID);

                        if (Bukkit.getPlayer(uuid) != null) {
                            Player pc = Bukkit.getPlayer(uuid);
                            pc.sendMessage(game.translate("werewolf.role.cursed_lover.description", killerUUID));
                            pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
                        }
                        killer.sendMessage(game.translate("werewolf.role.cursed_lover.description", uuid));
                        killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
                        killer.setMaxHealth(killer.getMaxHealth() + 1);

                        for (int i = 0; i < game.getCursedLoversRange().size(); i++) {
                            if (game.getCursedLoversRange().get(i).contains(playerUUID)) {
                                game.getCursedLoversRange().get(i).add(killerUUID);
                                game.getCursedLoversRange().get(i).remove(playerUUID);
                                break;
                            }
                        }
                    }
                }
            }
        }
        game.death(playerUUID);
    }

    public void thiefLoversRange(UUID killerUUID, UUID playerUUID) {

        int cp = -1;
        int ck = -1;
        for (int i = 0; i < game.getLoversRange().size(); i++) {
            if (game.getLoversRange().get(i).contains(playerUUID) && !game.getLoversRange().get(i).contains(killerUUID)) {
                game.getLoversRange().get(i).remove(playerUUID);
                game.getLoversRange().get(i).add(killerUUID);
                cp = i;
            } else if (!game.getLoversRange().get(i).contains(playerUUID) && game.getLoversRange().get(i).contains(killerUUID)) {
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
        if(!hasPower()) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
    }
}
