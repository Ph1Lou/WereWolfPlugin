package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SerialKiller extends RolesNeutral implements Power {

    public SerialKiller(GameManager game, UUID uuid) {
        super(game,uuid);
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
    public RoleLG getRoleEnum() {
        return RoleLG.SERIAL_KILLER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.serial_killer.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.serial_killer.display");
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player= Bukkit.getPlayer(getPlayerUUID());

        if (Bukkit.getPlayer(uuid)!=null) {
            player.setMaxHealth(Bukkit.getPlayer(uuid).getMaxHealth()+game.playerLG.get(uuid).getLostHeart());
        }
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        if(!hasPower()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,-1,false,false));
    }
}
