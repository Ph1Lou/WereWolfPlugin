package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SiameseTwin extends RoleVillage {

    public SiameseTwin(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.siamese_twin.description"))
                .setPower(() -> game.translate("werewolf.role.siamese_twin.power"))
                .addExtraLines(() -> {
                    if (game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) > 0) {
                        return game.translate("werewolf.role.siamese_twin.siamese_twin_list", Utils.conversion(game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey())));
                    } else {
                        return game.translate("werewolf.role.siamese_twin.siamese_twin_list", this.getBrother());
                    }
                })
                .build();
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        getPlayerWW().sendMessageWithKey("werewolf.role.siamese_twin.siamese_twin_list", this.getBrother());
    }


    private String getBrother() {

        StringBuilder list = new StringBuilder();

        game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isKey(
                        RolesBase.SIAMESE_TWIN.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));

        return list.toString();
    }


    @Override
    public void recoverPower() {
        getPlayerWW().addPlayerMaxHealth(4);
    }


    @Override
    public void second() {

        double health = game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.SIAMESE_TWIN.getKey()))
                .map(IRole::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .mapToDouble(player -> player.getHealth() /
                        VersionUtils.getVersionUtils().getPlayerMaxHealth(player))
                .average()
                .orElse(0);

        game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.SIAMESE_TWIN.getKey()))
                .map(IRole::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> health *
                        VersionUtils.getVersionUtils().getPlayerMaxHealth(player)
                        > 10)
                .forEach(player -> {
                    if (health * VersionUtils.getVersionUtils()
                            .getPlayerMaxHealth(player) + 1
                            < player.getHealth()) {
                        Sound.BURP.play(player);
                    }
                    player.setHealth(health *
                            VersionUtils.getVersionUtils()
                                    .getPlayerMaxHealth(player));
                });

    }
}
