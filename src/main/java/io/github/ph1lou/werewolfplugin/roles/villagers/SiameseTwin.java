package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
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

        if (getPlayerUUID() == null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player==null) return null;
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
        return player;
    }
}
