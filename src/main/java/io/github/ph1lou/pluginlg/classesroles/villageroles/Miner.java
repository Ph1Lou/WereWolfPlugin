package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Miner extends RolesVillage{

    public Miner(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.MINER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.miner.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.miner.display");
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,0,false,false));
    }
}
