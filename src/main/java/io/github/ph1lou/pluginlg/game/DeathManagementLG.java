package io.github.ph1lou.pluginlg.game;

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

    final GameManager game;

    public DeathManagementLG(GameManager game) {
        this.game = game;
    }


    public void deathStep1(String playerName) {

        PlayerLG plg = game.playerLG.get(playerName);
        String killerName = plg.getKiller();

        if (plg.isRole(RoleLG.ANCIEN) && plg.hasPower()) {

            plg.setPower(false);

            if (Bukkit.getPlayer(playerName) != null) {

                Player player = Bukkit.getPlayer(playerName);

                if (game.playerLG.containsKey(killerName) && game.playerLG.get(killerName).isCamp(Camp.VILLAGE)) {
                    player.setMaxHealth(Math.max(1, player.getMaxHealth() - 6));
                }
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
            resurrection(playerName);
        } else if (game.playerLG.containsKey(killerName) && (game.playerLG.get(killerName).isCamp(Camp.LG) || game.playerLG.get(killerName).isRole(RoleLG.LOUP_GAROU_BLANC))) {

            if (game.config.configValues.get(ToolLG.AUTO_REZ_INFECT) && plg.isRole(RoleLG.INFECT) && plg.hasPower()) {
                plg.setPower(false);
                resurrection(playerName);
                return;
            }

            plg.setCanBeInfect(true);

            for (String infect_name : game.playerLG.keySet()) {

                if (game.playerLG.get(infect_name).isState(State.LIVING) && game.playerLG.get(infect_name).isRole(RoleLG.INFECT) && !infect_name.equals(playerName) && game.playerLG.get(infect_name).hasPower() && Bukkit.getPlayer(infect_name) != null) {
                    TextComponent infect_msg = new TextComponent(String.format(game.text.powerUse.get(RoleLG.INFECT), playerName));
                    infect_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg infecter " + playerName));
                    Bukkit.getPlayer(infect_name).spigot().sendMessage(infect_msg);
                }
            }
        } else {
            plg.setDeathTime(game.score.getTimer() - 7);
            deathStep2(playerName);
        }
    }

    private void deathStep2(String playerName) {

        if (game.config.configValues.get(ToolLG.AUTO_REZ_WITCH) && game.playerLG.get(playerName).isRole(RoleLG.SORCIERE) && game.playerLG.get(playerName).hasPower()) {
            game.playerLG.get(playerName).setPower(false);
            resurrection(playerName);
            return;
        }

        for (String witch_name : game.playerLG.keySet()) {

            PlayerLG plg = game.playerLG.get(witch_name);

            if (plg.isState(State.LIVING) && plg.isRole(RoleLG.SORCIERE) && !witch_name.equals(playerName) && plg.hasPower() && Bukkit.getPlayer(witch_name) != null) {
                TextComponent witch_msg = new TextComponent(String.format(game.text.powerUse.get(RoleLG.SORCIERE), playerName));
                witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg sauver " + playerName));
                Bukkit.getPlayer(witch_name).spigot().sendMessage(witch_msg);
            }
        }
	}
	
	public void deathTimer() {

        for (String playerName : game.playerLG.keySet()) {

            PlayerLG plg = game.playerLG.get(playerName);

            if (plg.isState(State.JUDGEMENT)) {

                int timer = game.score.getTimer() - plg.getDeathTime();

                if (timer > 7 && plg.canBeInfect()) {
                    plg.setCanBeInfect(false);
                    deathStep2(playerName);
                } else if (timer > 14) {
					
					if(plg.hasBeenStolen() ) {

                        if (game.playerLG.get(plg.getKiller()).isState(State.LIVING)) {
                            game.roleManage.thief_recover_role(plg.getKiller(), playerName);
                        } else {
                            plg.setDeathTime(game.score.getTimer());
                            plg.setStolen(false);
                            deathStep1(playerName);
                        }
                    } else death(playerName);
                }
            }
        }
    }


    public void death(String playerName) {

        World world = game.getWorld();
        PlayerLG plg = game.playerLG.get(playerName);
        RoleLG role = plg.getRole();
        if (plg.isThief()) {
            role = RoleLG.VOLEUR;
        } else if ((plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE_DECHU)) && !plg.hasPower()) {
            role = RoleLG.ANGE;
        }
        game.config.roleCount.put(role, game.config.roleCount.get(role) - 1);
        plg.setState(State.MORT);
        game.score.removePlayerSize();

        for (ItemStack i : plg.getItemDeath()) {
            if (i != null) {
                if (role.equals(RoleLG.LOUP_PERFIDE) || role.equals(RoleLG.PETITE_FILLE)) {
                    i.removeEnchantment(Enchantment.KNOCKBACK);
                }
                world.dropItem(plg.getSpawn(), i);
            }
		}
		
		for(Player p:Bukkit.getOnlinePlayers()) {
            if(game.playerLG.containsKey(p.getName())){
                p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);
                if (game.config.configValues.get(ToolLG.SHOW_ROLE_TO_DEATH)) {
                    p.sendMessage(String.format(game.text.getText(28), playerName, game.text.translateRole.get(role)));
                } else p.sendMessage(String.format(game.text.getText(29), playerName));
                if (p.getName().equals(playerName)) {
                    p.setGameMode(GameMode.SPECTATOR);
                    TextComponent msg = new TextComponent(game.text.getText(186));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
                    p.spigot().sendMessage(msg);
                    if(game.getSpectatorMode()==0){
                        p.performCommand("lg quit");
                    }
                }
            }
        }

		if(game.playerLG.containsKey(plg.getKiller()) && plg.isCamp(Camp.VILLAGE) && game.playerLG.get(plg.getKiller()).isRole(RoleLG.LOUP_AMNESIQUE)  && game.playerLG.get(plg.getKiller()).hasPower()) {
            game.roleManage.newLG(plg.getKiller());
            game.playerLG.get(plg.getKiller()).setPower(false);
        }

		if(game.playerLG.containsKey(plg.getKiller()) && game.playerLG.get(plg.getKiller()).isRole(RoleLG.TUEUR_EN_SERIE)){
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
        if (!game.isState(StateLG.FIN)) {
            game.endlg.check_victory();
        } else return;

        if (game.config.configValues.get(ToolLG.EVENT_SEER_DEATH) && (role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE))) {
            game.eventslg.event1();
            game.config.configValues.put(ToolLG.EVENT_SEER_DEATH, false);
        }
    }


    private void checkLovers(String playerName) {

        int i = 0;

        while (i < game.loversManage.loversRange.size() && !game.loversManage.loversRange.get(i).contains(playerName)) {
            i++;
        }

        if (i < game.loversManage.loversRange.size()) {

            game.loversManage.loversRange.get(i).remove(playerName);

            while (!game.loversManage.loversRange.get(i).isEmpty() && game.playerLG.get(game.loversManage.loversRange.get(i).get(0)).isState(State.MORT)) {
                game.loversManage.loversRange.get(i).remove(0);
            }

            if (!game.loversManage.loversRange.get(i).isEmpty()) {
                String c1 = game.loversManage.loversRange.get(i).get(0);
                PlayerLG plc1 = game.playerLG.get(c1);
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(game.playerLG.containsKey(p.getName())){
                        p.sendMessage(String.format(game.text.getText(30), c1));
                    }
                }
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
                game.playerLG.get(playerName).setKiller("§dLove");
                death(c1);
            } else {
                game.loversManage.loversRange.remove(i);
                game.config.roleCount.put(RoleLG.COUPLE, game.config.roleCount.get(RoleLG.COUPLE) - 1);
            }
        }
    }

    private void checkCursedLovers(String playerName) {

        int i = 0;

        while (i < game.loversManage.cursedLoversRange.size() && !game.loversManage.cursedLoversRange.get(i).contains(playerName)) {
            i++;
        }

        if (i < game.loversManage.cursedLoversRange.size()) {

            game.loversManage.cursedLoversRange.get(i).remove(playerName);

            String cursedLover = game.loversManage.cursedLoversRange.get(i).get(0);

            if (Bukkit.getPlayer(cursedLover) != null) {
                Player killer = Bukkit.getPlayer(cursedLover);
                killer.sendMessage(game.text.getText(44));
                killer.setMaxHealth(Math.max(killer.getMaxHealth() - 2, 1));
            }
            game.loversManage.cursedLoversRange.remove(i);
            game.config.roleCount.put(RoleLG.COUPLE_MAUDIT, game.config.roleCount.get(RoleLG.COUPLE_MAUDIT) - 1);
        }
    }

    public void resurrection(String playerName) {

        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            for (PotionEffectType p : game.roleManage.effect_recover(playerName)) {
                player.addPotionEffect(new PotionEffect(p, Integer.MAX_VALUE, 0, false, false));
            }
        }
        transportation(playerName, Math.random() * Bukkit.getOnlinePlayers().size(),game.text.getText(31));
        game.playerLG.get(playerName).setState(State.LIVING);
        if (!game.isState(StateLG.FIN)) {
            game.endlg.check_victory();
        }
    }

    private void troublemakerDeath() {
        int i = 0;
        for (String p : game.playerLG.keySet()) {
            PlayerLG plg = game.playerLG.get(p);
            if (plg.isState(State.LIVING)) {
                transportation(p, i, game.text.getText(32));
                i++;
            }
        }
    }

    private void sisterDeath(String playerName) {

        for (String sister_name : game.playerLG.keySet()) {
            PlayerLG plg= game.playerLG.get(sister_name);
            if (plg.isState(State.LIVING) && plg.isRole(RoleLG.SOEUR) && Bukkit.getPlayer(sister_name) != null) {
                Bukkit.getPlayer(sister_name).sendMessage(String.format(game.text.powerHasBeenUse.get(RoleLG.SOEUR), playerName, game.playerLG.get(playerName).getKiller()));
            }
        }
    }

    private void targetDeath(String playerName) {

        for (String angel_name : game.playerLG.get(playerName).getTargetOf()) {

            PlayerLG plg = game.playerLG.get(angel_name);

            if (plg.isState(State.LIVING) && Bukkit.getPlayer(angel_name) != null) {

                Player ange = Bukkit.getPlayer(angel_name);

                if (plg.isRole(RoleLG.ANGE_DECHU)) {
                    if (game.playerLG.get(playerName).getKiller().equals(angel_name)) {
                        ange.setMaxHealth(ange.getMaxHealth() + 6);
                        ange.sendMessage(game.text.powerUse.get(RoleLG.ANGE_DECHU));
                    }
                } else {
                    ange.setMaxHealth(ange.getMaxHealth() - 4);
                    ange.sendMessage(game.text.powerUse.get(RoleLG.ANGE_GARDIEN));
                    game.playerLG.get(angel_name).setCamp(Camp.VILLAGE);
                }
            }
        }

    }

    private void masterDeath(String playerName) {

        for (String savage_name : game.playerLG.get(playerName).getDisciple()) {

            if (game.playerLG.get(savage_name).isState(State.LIVING) && !game.playerLG.get(savage_name).isCamp(Camp.LG)) {
                game.roleManage.newLG(savage_name);
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
