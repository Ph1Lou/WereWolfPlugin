package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.AmnesicWerewolf;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.SerialKiller;
import io.github.ph1lou.pluginlg.classesroles.villageroles.*;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.InfectFatherOfTheWolves;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.MischievousWereWolf;
import io.github.ph1lou.pluginlg.events.NewWereWolfEvent;
import io.github.ph1lou.pluginlg.events.TargetDeathEvent;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;


public class DeathManagementLG {

    private final GameManager game;
    private final MainLG main;

    public DeathManagementLG(MainLG main,GameManager game) {
        this.main=main;
        this.game = game;
    }


    public void deathStep1(UUID uuid) {

        PlayerLG plg = game.playerLG.get(uuid);
        UUID killerUUID = plg.getLastKiller();

        if (plg.getRole() instanceof Elder && ((Elder) plg.getRole()).hasPower()) {

            ((Elder) plg.getRole()).setPower(false);

            if (Bukkit.getPlayer(uuid) != null) {

                Player player = Bukkit.getPlayer(uuid);

                if (game.playerLG.containsKey(killerUUID) && game.playerLG.get(killerUUID).getRole().isCamp(Camp.VILLAGER)) {
                    player.setMaxHealth(Math.max(1, player.getMaxHealth() - 6));
                }
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
            resurrection(uuid);

        } else if (game.playerLG.containsKey(killerUUID) && game.roleManage.isWereWolf(killerUUID)) {

            if (game.config.getConfigValues().get(ToolLG.AUTO_REZ_INFECT) && plg.getRole() instanceof InfectFatherOfTheWolves) {
                InfectFatherOfTheWolves infectFatherOfTheWolves= (InfectFatherOfTheWolves) plg.getRole();
                if(infectFatherOfTheWolves.hasPower()){
                    infectFatherOfTheWolves.setPower(false);
                    resurrection(uuid);
                    return;
                }
            }

            plg.setCanBeInfect(true);

            for (UUID playerUUID : game.playerLG.keySet()) {

                PlayerLG plg2 = game.playerLG.get(playerUUID);

                if (plg2.isState(State.ALIVE) && plg2.getRole() instanceof InfectFatherOfTheWolves) {

                    InfectFatherOfTheWolves infectFatherOfTheWolves = (InfectFatherOfTheWolves) plg2.getRole();

                    if(!playerUUID.equals(uuid) && infectFatherOfTheWolves.hasPower() && Bukkit.getPlayer(playerUUID) != null){
                        TextComponent infect_msg = new TextComponent(game.translate("werewolf.role.infect_father_of_the_wolves.infection_message", plg.getName()));
                        infect_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ww " +game.translate("werewolf.role.infect_father_of_the_wolves.command")+" "+ uuid));
                        Bukkit.getPlayer(playerUUID).spigot().sendMessage(infect_msg);
                    }
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if(plg.isState(State.JUDGEMENT)){
                    deathStep2(uuid);
                }
            },7*20);

        } else {
            deathStep2(uuid);
        }
    }

    private void deathStep2(UUID uuid) {

        PlayerLG plg=game.playerLG.get(uuid);
        plg.setCanBeInfect(false);
        if (game.config.getConfigValues().get(ToolLG.AUTO_REZ_WITCH) && plg.getRole() instanceof Witch) {
            Witch witch = (Witch) plg.getRole();
            if(witch.hasPower()){
                witch.setPower(false);
                resurrection(uuid);
                return;
            }
        }

        for (UUID playerUUID : game.playerLG.keySet()) {

            PlayerLG plg2 = game.playerLG.get(playerUUID);

            if (plg2.isState(State.ALIVE) && plg2.getRole() instanceof Witch ) {

                Witch witch = (Witch) plg2.getRole();

                if(!playerUUID.equals(uuid) && witch.hasPower() && Bukkit.getPlayer(playerUUID) != null){
                    TextComponent witch_msg = new TextComponent(game.translate("werewolf.role.witch.resuscitation_message", plg.getName()));
                    witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ww "+game.translate("werewolf.role.witch.command") +" "+ uuid));
                    Bukkit.getPlayer(playerUUID).spigot().sendMessage(witch_msg);
                }
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(plg.isState(State.JUDGEMENT)){
                death(uuid);
            }
        },7*20);
	}

    public void death(UUID playerUUID) {

        World world = game.getWorld();
        PlayerLG plg = game.playerLG.get(playerUUID);

        plg.setDeathTime(game.score.getTimer());
        RolesImpl role = plg.getRole();
        RoleLG roleLG = role.getRoleEnum();

        if (plg.isThief()) {
            role=null;
            roleLG= RoleLG.THIEF;
        }

        game.config.getRoleCount().put(roleLG, game.config.getRoleCount().get(roleLG) - 1);

        if (game.config.getConfigValues().get(ToolLG.SHOW_ROLE_TO_DEATH)) {
            Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message_with_role", plg.getName(), game.translate(roleLG.getKey())));
        } else Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message", plg.getName()));

        plg.setState(State.DEATH);
        game.score.removePlayerSize();

        for (ItemStack i : plg.getItemDeath()) {
            if (i != null) {
                if (role instanceof MischievousWereWolf || role instanceof LittleGirl) {
                    i.removeEnchantment(Enchantment.KNOCKBACK);
                }
                world.dropItem(plg.getSpawn(), i);
            }
        }
        for (ItemStack i : game.stufflg.getDeathLoot()) {
            if (i != null) {
                world.dropItem(plg.getSpawn(), i);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);

            if (p.getUniqueId().equals(playerUUID)) {
                p.setGameMode(GameMode.SPECTATOR);
                TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
                p.spigot().sendMessage(msg);
                if (game.getSpectatorMode() == 0) {
                    p.kickPlayer(game.translate("werewolf.check.death_spectator"));
                }
            }
        }


        if (game.playerLG.containsKey(plg.getLastKiller())) {

            PlayerLG klg = game.playerLG.get(plg.getLastKiller());

            if (klg.getRole() instanceof AmnesicWerewolf){
                AmnesicWerewolf amnesicWerewolf = (AmnesicWerewolf) klg.getRole();
                if(plg.getRole().isCamp(Camp.VILLAGER) && !amnesicWerewolf.getTransformed()){
                    Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(plg.getLastKiller()));
                    amnesicWerewolf.setTransformed(true);
                }
            }
            else if(klg.getRole() instanceof SerialKiller){
                SerialKiller serialKiller = (SerialKiller) klg.getRole();
                if(Bukkit.getPlayer(plg.getLastKiller())!=null){
                    Player killer = Bukkit.getPlayer(plg.getLastKiller());
                    killer.setMaxHealth(killer.getMaxHealth()+2);
                    killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
                    killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                serialKiller.setPower(false);
            }
		}
		
		if (role instanceof Troublemaker) {
			troublemakerDeath();
        }
        Bukkit.getPluginManager().callEvent(new TargetDeathEvent(playerUUID));
        if (role instanceof Sister) {
            sisterDeath(playerUUID);
        }
        if (!plg.getLovers().isEmpty()) {
            checkLovers(playerUUID);
        }
        if (plg.getCursedLovers()!=null) {
            checkCursedLovers(playerUUID);
        }
        if (plg.getAmnesiacLoverUUID()!=null) {
            checkAmnesiacLovers(playerUUID);
        }
        if (!game.isState(StateLG.END)) {
            game.endlg.check_victory();
        } else return;

        if (game.config.getConfigValues().get(ToolLG.EVENT_SEER_DEATH) && role instanceof Seer) {
            game.eventslg.event1();
            game.config.getConfigValues().put(ToolLG.EVENT_SEER_DEATH, false);
        }
    }


    private void checkLovers(UUID playerUUID) {

        int i = 0;

        while (i < game.loversManage.loversRange.size() && !game.loversManage.loversRange.get(i).contains(playerUUID)) {
            i++;
        }

        if (i < game.loversManage.loversRange.size()) {

            game.loversManage.loversRange.get(i).remove(playerUUID);

            while (!game.loversManage.loversRange.get(i).isEmpty() && game.playerLG.get(game.loversManage.loversRange.get(i).get(0)).isState(State.DEATH)) {
                game.loversManage.loversRange.get(i).remove(0);
            }

            if (!game.loversManage.loversRange.get(i).isEmpty()) {
                UUID c1 = game.loversManage.loversRange.get(i).get(0);
                PlayerLG plc1 = game.playerLG.get(c1);
                Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death",game.playerLG.get(c1).getName()));
                if (Bukkit.getPlayer(c1) != null) {
                    if (plc1.isState(State.ALIVE)) {
                        Player player = Bukkit.getPlayer(c1);
                        plc1.setSpawn(player.getLocation());
                        plc1.clearItemDeath();

                        Inventory inv = Bukkit.createInventory(null, 45);

                        for (int j = 0; j < 40; j++) {
                            inv.setItem(j, player.getInventory().getItem(j));
                        }
                        plc1.setItemDeath(inv.getContents());
                    }
                }
                death(c1);
            } else {
                game.loversManage.loversRange.remove(i);
                game.config.getRoleCount().put(RoleLG.LOVER, game.config.getRoleCount().get(RoleLG.LOVER) - 1);
            }
        }
    }

    private void checkAmnesiacLovers(UUID playerUUID) {

        int i = 0;

        while (i < game.loversManage.amnesiacLoversRange.size() && !game.loversManage.amnesiacLoversRange.get(i).contains(playerUUID)) {
            i++;
        }

        if (i < game.loversManage.amnesiacLoversRange.size()) {

            game.loversManage.amnesiacLoversRange.get(i).remove(playerUUID);
            UUID c1 = game.loversManage.amnesiacLoversRange.get(i).get(0);
            game.loversManage.amnesiacLoversRange.remove(i);
            PlayerLG plc1 = game.playerLG.get(c1);
            Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death", game.playerLG.get(c1).getName()));
            if (Bukkit.getPlayer(c1) != null) {
                if (plc1.isState(State.ALIVE)) {
                    Player player = Bukkit.getPlayer(c1);
                    plc1.setSpawn(player.getLocation());
                    plc1.clearItemDeath();

                    Inventory inv = Bukkit.createInventory(null, 45);

                    for (int j = 0; j < 40; j++) {
                        inv.setItem(j, player.getInventory().getItem(j));
                    }
                    plc1.setItemDeath(inv.getContents());
                }
            }
            game.config.getRoleCount().put(RoleLG.AMNESIAC_LOVER, game.config.getRoleCount().get(RoleLG.AMNESIAC_LOVER) - 1);
            death(c1);
        }
    }

    private void checkCursedLovers(UUID playerUUID) {

        int i = 0;

        while (i < game.loversManage.cursedLoversRange.size() && !game.loversManage.cursedLoversRange.get(i).contains(playerUUID)) {
            i++;
        }

        if (i < game.loversManage.cursedLoversRange.size()) {

            game.loversManage.cursedLoversRange.get(i).remove(playerUUID);

            UUID cursedLover = game.loversManage.cursedLoversRange.get(i).get(0);

            if (Bukkit.getPlayer(cursedLover) != null) {
                Player killer = Bukkit.getPlayer(cursedLover);
                killer.sendMessage(game.translate("werewolf.role.cursed_lover.death_cursed_lover"));
                killer.setMaxHealth(Math.max(killer.getMaxHealth() - 2, 1));
            }
            game.loversManage.cursedLoversRange.remove(i);
            game.config.getRoleCount().put(RoleLG.CURSED_LOVER, game.config.getRoleCount().get(RoleLG.CURSED_LOVER) - 1);
        }
    }

    public void resurrection(UUID playerUUID) {

        PlayerLG plg = game.playerLG.get(playerUUID);

        if (Bukkit.getPlayer(playerUUID) != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            plg.getRole().recoverPotionEffect(player);
        }
        transportation(playerUUID, Math.random() * Bukkit.getOnlinePlayers().size(),game.translate("werewolf.announcement.resurrection"));
        plg.setState(State.ALIVE);
        if (!game.isState(StateLG.END)) {
            game.endlg.check_victory();
        }
    }

    private void troublemakerDeath() {
        int i = 0;
        for (UUID uuid : game.playerLG.keySet()) {
            PlayerLG plg = game.playerLG.get(uuid);
            if (plg.isState(State.ALIVE)) {
                transportation(uuid, i, game.translate("werewolf.role.troublemaker.troublemaker_death"));
                i++;
            }
        }
    }

    private void sisterDeath(UUID playerUUID) {

        PlayerLG plg =game.playerLG.get(playerUUID);

        for (UUID uuid : game.playerLG.keySet()) {
            PlayerLG plg2= game.playerLG.get(uuid);
            if (plg2.isState(State.ALIVE) && plg2.getRole() instanceof Sister && Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(game.translate("werewolf.role.sister.reveal_killer", plg.getName(), game.playerLG.get(plg.getLastKiller()).getName()));
            }
        }
    }


    public void transportation(UUID playerUUID, double d, String message) {

        if (Bukkit.getPlayer(playerUUID) != null) {

            Player player = Bukkit.getPlayer(playerUUID);
            World world = player.getWorld();
            WorldBorder wb = world.getWorldBorder();
            double a = d * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(message);
            player.removePotionEffect(PotionEffectType.WITHER);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400,-1,false,false));
			player.teleport(new Location(world,x,world.getHighestBlockYAt(x,z)+100,z));
		}
	}


}
