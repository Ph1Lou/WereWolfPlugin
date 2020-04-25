package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.listener.gamelisteners.roleslisteners.ListenerRoles;
import io.github.ph1lou.pluginlg.listener.gamelisteners.roleslisteners.ListenerRolesDefault;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class CycleLG {

    final GameManager game;
    public final Map<RoleLG, ListenerRoles> rolesListener = new HashMap<>();
    private final ListenerRoles defaultListener = new ListenerRolesDefault();

    public CycleLG(GameManager game) {
        this.game = game;
        for (RoleLG role : RoleLG.values()) {
            try {
                ListenerRoles listener;
                Class<? extends ListenerRoles> listenerClass = role.getListener();

                if (listenerClass != null) {
                    listener = listenerClass.getDeclaredConstructor().newInstance();
                    listener.init(game);
                    rolesListener.put(role, listener);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
	
	public void night() {
        
        if (!game.isState(StateLG.LG)) return;

        game.score.groupSizeChange();

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(p.getName())) { 
                
                PlayerLG plg = game.playerLG.get(p.getName());
                
                p.sendMessage(String.format(game.text.getText(124), game.score.getTimer() / game.config.timerValues.get(TimerLG.DAY_DURATION) / 2 + 1));


                if (plg.isState(State.LIVING)) {

                    p.playSound(p.getLocation(), Sound.DOOR_CLOSE, 1, 20);
                    if (plg.isCamp(Camp.LG)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
                    }
                    this.rolesListener.getOrDefault(plg.getRole(), defaultListener).onNight(p);
                }
            }
        }
    }


    public void selectionEnd() {

        if (!game.isState(StateLG.LG)) return;

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(p.getName())) {

                PlayerLG plg = game.playerLG.get(p.getName());

                if (plg.isState(State.LIVING)) {
                    this.rolesListener.getOrDefault(plg.getRole(), defaultListener).onSelectionEnd(p, plg);
                }
            }
        }
    }
	public void preDay() {

        if (!game.isState(StateLG.LG)) return;

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(p.getName())) {

                PlayerLG plg = game.playerLG.get(p.getName());

                if (plg.isState(State.LIVING)) {
                    this.rolesListener.getOrDefault(plg.getRole(), defaultListener).onDayWillCome(p);
                }
            }
        }
    }

	public void preVoteResult() {


        if (!game.isState(StateLG.LG)) return;

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(p.getName())) {

                PlayerLG plg = game.playerLG.get(p.getName());

                if (plg.isState(State.LIVING)) {
                    this.rolesListener.getOrDefault(plg.getRole(), defaultListener).onVoteEnd(p, plg);
                }
            }
        }
	}
				
				
	public void day() {

        if (!game.isState(StateLG.LG)) return;

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(p.getName())) {

                PlayerLG plg = game.playerLG.get(p.getName());

                p.sendMessage(String.format(game.text.getText(16), game.score.getTimer() / game.config.timerValues.get(TimerLG.DAY_DURATION) / 2 + 1));

                if (plg.isState(State.LIVING)) {

                    p.playSound(p.getLocation(), Sound.DOOR_OPEN, 1, 20);

                    if (game.config.configValues.get(ToolLG.VOTE) && game.config.timerValues.get(TimerLG.VOTE_BEGIN) < 0) {
                        p.sendMessage(String.format(game.text.getText(17), game.score.conversion(game.config.timerValues.get(TimerLG.VOTE_DURATION))));
                    }

                    if (plg.isCamp(Camp.LG)) {
                        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    }

                    if (plg.getLostHeart() > 0) {
                        p.setMaxHealth(p.getMaxHealth() + plg.getLostHeart());
                        plg.clearLostHeart();
                    }

                    if (plg.hasDamn()) {
                        plg.setDamn(false);
                        p.removePotionEffect(PotionEffectType.JUMP);
                        p.sendMessage(game.text.getText(19));
                    }
                    if (plg.hasSalvation()) {
                        plg.setSalvation(false);
                        if (!((plg.isRole(RoleLG.ANCIEN) || plg.isRole(RoleLG.VOLEUR)) && plg.hasPower())) {
                            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        }
                        p.sendMessage(game.text.getText(20));
                    }
                    this.rolesListener.getOrDefault(plg.getRole(), defaultListener).onDay(p, plg);
                }
			}
		}	
	}

}
