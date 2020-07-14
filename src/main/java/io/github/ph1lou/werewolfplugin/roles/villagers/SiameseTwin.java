package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
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

        Bukkit.getPlayer(getPlayerUUID()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player==null) return null;
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
        return player;
    }
}
