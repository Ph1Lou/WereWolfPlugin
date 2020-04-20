package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class DeathManagementLG {

    final MainLG main;

    public DeathManagementLG(MainLG main) {
        this.main = main;
    }


    public void deathStep1(String playerName) {

        PlayerLG plg = main.playerLG.get(playerName);
        String killerName = plg.getKiller();

        if (plg.isRole(RoleLG.ANCIEN) && plg.hasPower()) {

            plg.setPower(false);

            if (Bukkit.getPlayer(playerName) != null) {

                Player player = Bukkit.getPlayer(playerName);

                if (main.playerLG.containsKey(killerName) && main.playerLG.get(killerName).isCamp(Camp.VILLAGE)) {
                    player.setMaxHealth(Math.max(1, player.getMaxHealth() - 6));
                }
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
            resurrection(playerName);
        } else if (main.playerLG.containsKey(killerName) && (main.playerLG.get(killerName).isCamp(Camp.LG) || main.playerLG.get(killerName).isRole(RoleLG.LOUP_GAROU_BLANC))) {

            if (main.config.configValues.get(ToolLG.AUTO_REZ_INFECT) && plg.isRole(RoleLG.INFECT) && plg.hasPower()) {
                plg.setPower(false);
                resurrection(playerName);
                return;
            }

            plg.setCanBeInfect(true);

            for (String infect_name : main.playerLG.keySet()) {

                if (main.playerLG.get(infect_name).isState(State.LIVING) && main.playerLG.get(infect_name).isRole(RoleLG.INFECT) && !infect_name.equals(playerName) && main.playerLG.get(infect_name).hasPower() && Bukkit.getPlayer(infect_name) != null) {
                    TextComponent infect_msg = new TextComponent(String.format(main.text.powerUse.get(RoleLG.INFECT), playerName));
                    infect_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg " + main.text.getText(236) + " " + playerName));
                    Bukkit.getPlayer(infect_name).spigot().sendMessage(infect_msg);
                }
            }
        } else {
            plg.setDeathTime(main.score.getTimer() - 7);
            deathStep2(playerName);
        }
    }

    private void deathStep2(String playerName) {

        if (main.config.configValues.get(ToolLG.AUTO_REZ_WITCH) && main.playerLG.get(playerName).isRole(RoleLG.SORCIERE) && main.playerLG.get(playerName).hasPower()) {
            main.playerLG.get(playerName).setPower(false);
            resurrection(playerName);
            return;
        }

        for (String witch_name : main.playerLG.keySet()) {

            if (main.playerLG.get(witch_name).isState(State.LIVING) && main.playerLG.get(witch_name).isRole(RoleLG.SORCIERE) && !witch_name.equals(playerName) && main.playerLG.get(witch_name).hasPower() && Bukkit.getPlayer(witch_name) != null) {
                TextComponent witch_msg = new TextComponent(String.format(main.text.powerUse.get(RoleLG.SORCIERE), playerName));
                witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg " + main.text.getText(253) + " " + playerName));
                Bukkit.getPlayer(witch_name).spigot().sendMessage(witch_msg);
            }
        }
	}
	
	public void deathTimer() {

        for (String playerName : main.playerLG.keySet()) {

            PlayerLG plg = main.playerLG.get(playerName);

            if (plg.isState(State.JUDGEMENT)) {

                int timer = main.score.getTimer() - plg.getDeathTime();

                if (timer > 7 && plg.canBeInfect()) {
                    plg.setCanBeInfect(false);
                    deathStep2(playerName);
                } else if (timer > 14) {
					
					if(plg.hasBeenStolen() ) {

                        if (main.playerLG.get(plg.getKiller()).isState(State.LIVING)) {
                            main.roleManage.thief_recover_role(plg.getKiller(), playerName);
                        } else {
                            plg.setDeathTime(main.score.getTimer());
                            plg.setStolen(false);
                            deathStep1(playerName);
                        }
                    } else death(playerName);
                }
            }
        }
    }


    public void death(String playerName) {

        World world = Bukkit.getWorld("world");
        PlayerLG plg = main.playerLG.get(playerName);
        RoleLG role = plg.getRole();
        if (plg.isThief()) {
            role = RoleLG.VOLEUR;
        } else if ((plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE_DECHU)) && !plg.hasPower()) {
            role = RoleLG.ANGE;
        }
        main.config.roleCount.put(role, main.config.roleCount.get(role) - 1);
        plg.setState(State.MORT);
        main.score.removePlayerSize();

        if (main.config.configValues.get(ToolLG.SHOW_ROLE_TO_DEATH)) {
            Bukkit.broadcastMessage(String.format(main.text.getText(28), playerName, main.text.translateRole.get(role)));
        } else Bukkit.broadcastMessage(String.format(main.text.getText(29), playerName));

        for (ItemStack i : plg.getItemDeath()) {
            if (i != null) {
                if (role.equals(RoleLG.LOUP_PERFIDE) || role.equals(RoleLG.PETITE_FILLE)) {
                    i.removeEnchantment(Enchantment.KNOCKBACK);
                }
                world.dropItem(plg.getSpawn(), i);
            }
		}
		
		for(Player p:Bukkit.getOnlinePlayers()) {

            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);

            if (p.getName().equals(playerName)) {
                p.setGameMode(GameMode.SPECTATOR);
                TextComponent msg = new TextComponent(main.text.getText(186));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
                p.spigot().sendMessage(msg);
            }
        }

		if(main.playerLG.containsKey(plg.getKiller()) && plg.isCamp(Camp.VILLAGE) && main.playerLG.get(plg.getKiller()).isRole(RoleLG.LOUP_AMNESIQUE)  && main.playerLG.get(plg.getKiller()).hasPower()) {
            main.roleManage.newLG(plg.getKiller());
            main.playerLG.get(plg.getKiller()).setPower(false);
        }

		if(main.playerLG.containsKey(plg.getKiller()) && main.playerLG.get(plg.getKiller()).isRole(RoleLG.TUEUR_EN_SERIE)){
			if(Bukkit.getPlayer(plg.getKiller())!=null){
				Player killer = Bukkit.getPlayer(plg.getKiller());
				killer.setMaxHealth(killer.getMaxHealth()+2);
			}
		}
		
		if (role.equals(RoleLG.TRUBLION)) {
			troublemakerDeath();
        }
        if (!plg.getTargetOf().isEmpty()) {
            targetDeath(playerName);
        }
        if (!plg.getDisciple().isEmpty()) {
            masterDeath(playerName);
        }
        if (role.equals(RoleLG.SOEUR)) {
            sisterDeath(playerName);
        }
        if (!plg.getLovers().isEmpty()) {
            checkLovers(playerName);
        }
        if (!plg.getCursedLovers().isEmpty()) {
            checkCursedLovers(playerName);
        }
        if (!main.isState(StateLG.FIN)) {
            main.endlg.check_victory();
        } else return;

        if (main.config.configValues.get(ToolLG.EVENT_SEER_DEATH) && (role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE))) {
            main.eventslg.event1();
            main.config.configValues.put(ToolLG.EVENT_SEER_DEATH, false);
        }
    }


    private void checkLovers(String playerName) {

        int i = 0;

        while (i < main.loversManage.loversRange.size() && !main.loversManage.loversRange.get(i).contains(playerName)) {
            i++;
        }

        if (i < main.loversManage.loversRange.size()) {

            main.loversManage.loversRange.get(i).remove(playerName);

            while (!main.loversManage.loversRange.get(i).isEmpty() && main.playerLG.get(main.loversManage.loversRange.get(i).get(0)).isState(State.MORT)) {
                main.loversManage.loversRange.get(i).remove(0);
            }

            if (!main.loversManage.loversRange.get(i).isEmpty()) {
                String c1 = main.loversManage.loversRange.get(i).get(0);
                PlayerLG plc1 = main.playerLG.get(c1);
                Bukkit.broadcastMessage(String.format(main.text.getText(30), c1));
                if (Bukkit.getPlayer(c1) != null) {
                    if (plc1.isState(State.LIVING)) {
                        Player player = Bukkit.getPlayer(c1);
                        plc1.setSpawn(player.getLocation());
                        plc1.clearItemDeath();
                        plc1.setItemDeath(player.getInventory().getContents());
                        plc1.addItemDeath(player.getInventory().getHelmet());
                        plc1.addItemDeath(player.getInventory().getChestplate());
                        plc1.addItemDeath(player.getInventory().getBoots());
                        plc1.addItemDeath(player.getInventory().getLeggings());
                    }
                }
                main.playerLG.get(playerName).setKiller("§dLove");
                death(c1);
            } else {
                main.loversManage.loversRange.remove(i);
                main.config.roleCount.put(RoleLG.COUPLE, main.config.roleCount.get(RoleLG.COUPLE) - 1);
            }
        }
    }

    private void checkCursedLovers(String playerName) {

        int i = 0;

        while (i < main.loversManage.cursedLoversRange.size() && !main.loversManage.cursedLoversRange.get(i).contains(playerName)) {
            i++;
        }

        if (i < main.loversManage.cursedLoversRange.size()) {

            main.loversManage.cursedLoversRange.get(i).remove(playerName);

            String killerName = main.playerLG.get(playerName).getKiller();

            if (Bukkit.getPlayer(killerName) != null) {
                Player killer = Bukkit.getPlayer(killerName);
                killer.sendMessage(main.text.getText(44));
                killer.setMaxHealth(Math.max(killer.getMaxHealth() - 2, 1));
            }

            if (main.loversManage.cursedLoversRange.get(i).size() == 1) {
                main.loversManage.cursedLoversRange.remove(i);
                main.config.roleCount.put(RoleLG.COUPLE_MAUDIT, main.config.roleCount.get(RoleLG.COUPLE_MAUDIT) - 1);
            }
        }
    }

    public void resurrection(String playerName) {

        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            for (PotionEffectType p : main.roleManage.effect_recover(playerName)) {
                player.addPotionEffect(new PotionEffect(p, Integer.MAX_VALUE, 0, false, false));
            }
        }
        transportation(playerName, Math.random() * Bukkit.getOnlinePlayers().size(), main.text.getText(31));
        main.playerLG.get(playerName).setState(State.LIVING);
        if (!main.isState(StateLG.FIN)) {
            main.endlg.check_victory();
        }
    }

    private void troublemakerDeath() {
        int i = 0;
        for (String p : main.playerLG.keySet()) {
            if (main.playerLG.get(p).isState(State.LIVING)) {
                transportation(p, i, main.text.getText(32));
                i++;
            }
        }
    }

    private void sisterDeath(String playerName) {

        for (String sister_name : main.playerLG.keySet()) {
            if (main.playerLG.get(sister_name).isState(State.LIVING) && main.playerLG.get(sister_name).isRole(RoleLG.SOEUR) && Bukkit.getPlayer(sister_name) != null) {
                Bukkit.getPlayer(sister_name).sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.SOEUR), playerName, main.playerLG.get(playerName).getKiller()));
            }
        }
    }

    private void targetDeath(String playerName) {

        for (String angel_name : main.playerLG.get(playerName).getTargetOf()) {

            if (main.playerLG.get(angel_name).isState(State.LIVING) && Bukkit.getPlayer(angel_name) != null) {

                Player ange = Bukkit.getPlayer(angel_name);

                if (main.playerLG.get(angel_name).isRole(RoleLG.ANGE_DECHU)) {
                    if (main.playerLG.get(playerName).getKiller().equals(angel_name)) {
                        ange.setMaxHealth(ange.getMaxHealth() + 6);
                        ange.sendMessage(main.text.powerUse.get(RoleLG.ANGE_DECHU));
                    }
                } else {
                    ange.setMaxHealth(ange.getMaxHealth() - 4);
                    ange.sendMessage(main.text.powerUse.get(RoleLG.ANGE_GARDIEN));
                    main.playerLG.get(angel_name).setCamp(Camp.VILLAGE);
                }
            }
        }

    }

    private void masterDeath(String playerName) {

        for (String savage_name : main.playerLG.get(playerName).getDisciple()) {

            if (main.playerLG.get(savage_name).isState(State.LIVING) && !main.playerLG.get(savage_name).isCamp(Camp.LG)) {
                main.roleManage.newLG(savage_name);
            }
        }
    }

    public void transportation(String playerName, double d, String message) {

        if (Bukkit.getPlayer(playerName) != null) {

            Player player = Bukkit.getPlayer(playerName);
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
