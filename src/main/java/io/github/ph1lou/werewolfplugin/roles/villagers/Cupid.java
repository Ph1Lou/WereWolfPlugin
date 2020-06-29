package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.TimerLG;
import io.github.ph1lou.werewolfapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cupid extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Cupid(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
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
        return game.translate("werewolf.role.cupid.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.cupid.display";
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (hasPower()) {
            player.sendMessage(game.translate("werewolf.role.cupid.lover_designation_message", game.conversion(game.getConfig().getTimerValues().get(TimerLG.LOVER_DURATION))));
        } else {
            player.sendMessage(game.translate("werewolf.role.cupid.designation_perform",game.getPlayersWW().get(getAffectedPlayers().get(0)).getName(), game.getPlayersWW().get(getAffectedPlayers().get(1)).getName()));
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if(game.getConfig().getLimitPunch()==1){
            if(event.getEnchants().containsKey(Enchantment.ARROW_KNOCKBACK)){
                event.getFinalEnchants().put(Enchantment.ARROW_KNOCKBACK,event.getEnchants().get(Enchantment.ARROW_KNOCKBACK));
            }
        }
    }


    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player==null) return null;
        player.sendMessage(game.translate("werewolf.role.cupid.lover_designation_message", game.conversion(game.getConfig().getTimerValues().get(TimerLG.LOVER_DURATION))));
        return player;
    }
}
