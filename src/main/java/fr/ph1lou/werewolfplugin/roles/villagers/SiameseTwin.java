package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Role(key = RoleBase.SIAMESE_TWIN,
        category = Category.VILLAGER, 
        attributes = RoleAttribute.VILLAGER,
        requireDouble = true)
public class SiameseTwin extends RoleVillage {

    public SiameseTwin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {

        String extraLines;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) > 0) {
            extraLines= game.translate("werewolf.roles.siamese_twin.siamese_twin_list",
                    Formatter.format("&list&",Utils.conversion(game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST))));
        } else {
            extraLines=  game.translate("werewolf.roles.siamese_twin.siamese_twin_list",
                    Formatter.format("&list&",this.getBrother()));
        }

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.siamese_twin.description"))
                .setPower(game.translate("werewolf.roles.siamese_twin.power"))
                .addExtraLines(extraLines)
                .build();
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,"werewolf.roles.siamese_twin.siamese_twin_list",
                Formatter.format("&list&",this.getBrother()));
    }


    private String getBrother() {

        StringBuilder list = new StringBuilder();

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey(
                        RoleBase.SIAMESE_TWIN))
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
                .filter(roles -> roles.isKey(RoleBase.SIAMESE_TWIN))
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
                .filter(roles -> roles.isKey(RoleBase.SIAMESE_TWIN))
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
