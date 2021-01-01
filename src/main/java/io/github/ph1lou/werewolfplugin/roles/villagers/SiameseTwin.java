package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sounds;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SiameseTwin extends RolesVillage {

    public SiameseTwin(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {


        StringBuilder list = new StringBuilder();

        game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isKey(
                        RolesBase.SIAMESE_TWIN.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));

        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.siamese_twin.description")) +
                game.translate("werewolf.description.power", game.translate("werewolf.role.siamese_twin.power")) +
                game.translate("werewolf.role.siamese_twin.siamese_twin_list",
                        list.toString());
    }


    @Override
    public void recoverPower() {
        getPlayerWW().addPlayerMaxHealth(4);
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        getPlayerWW().addPlayerMaxHealth(4);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        double health = game.getPlayerWW()
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

        game.getPlayerWW()
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
