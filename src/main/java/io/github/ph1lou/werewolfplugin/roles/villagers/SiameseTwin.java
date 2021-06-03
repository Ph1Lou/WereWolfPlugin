package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.*;
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

    public SiameseTwin(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {

        String extraLines;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST.getKey()) > 0) {
            extraLines= game.translate("werewolf.role.siamese_twin.siamese_twin_list", Utils.conversion(game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST.getKey())));
        } else {
            extraLines=  game.translate("werewolf.role.siamese_twin.siamese_twin_list", this.getBrother());
        }

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.siamese_twin.description"))
                .setPower(game.translate("werewolf.role.siamese_twin.power"))
                .addExtraLines(extraLines)
                .build();
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        this.getPlayerWW().sendMessageWithKey("werewolf.role.siamese_twin.siamese_twin_list", this.getBrother());
    }


    private String getBrother() {

        StringBuilder list = new StringBuilder();

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey(
                        RolesBase.SIAMESE_TWIN.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));

        return list.toString();
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().addPlayerMaxHealth(4);
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }


    @Override
    public void second() {

        double health = game.getPlayersWW()
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

        game.getPlayersWW()
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
