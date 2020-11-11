package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class SiameseTwin extends RolesVillage {

    public SiameseTwin(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    @Override
    public @NotNull String getDescription() {

        StringBuilder list = new StringBuilder();

        game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isKey(
                        RolesBase.SIAMESE_TWIN.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));

        return game.translate("werewolf.role.siamese_twin.description") +
                "\n§f" +
                game.translate("werewolf.role.siamese_twin.siamese_twin_list",
                        list.toString());
    }


    @Override
    public void recoverPowerAfterStolen() {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
    }

    @Override
    public void recoverPower() {
        super.recoverPower();
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        double health = game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.SIAMESE_TWIN.getKey()))
                .map(Roles::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .mapToDouble(player -> player.getHealth() /
                        VersionUtils.getVersionUtils().getPlayerMaxHealth(player))
                .average()
                .orElse(0);

        game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.SIAMESE_TWIN.getKey()))
                .map(Roles::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> health *
                        VersionUtils.getVersionUtils().getPlayerMaxHealth(player)
                        > 10)
                .forEach(player -> {
                    if (health * VersionUtils.getVersionUtils()
                            .getPlayerMaxHealth(player) + 1
                            < player.getHealth()) {
                        Sounds.BURP.play(player);
                    }
                    player.setHealth(health *
                            VersionUtils.getVersionUtils()
                                    .getPlayerMaxHealth(player));
                });

    }
}
