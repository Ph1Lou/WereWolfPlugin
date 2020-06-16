package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SiameseTwin extends RolesVillage {

    public SiameseTwin(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.siamese_twin.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.siamese_twin.display";
    }


    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Bukkit.getPlayer(getPlayerUUID()).setMaxHealth(26);
    }

    @Override
    public void recoverPower(Player player) {
        player.setMaxHealth(26);
        player.setHealth(26);
    }
}
