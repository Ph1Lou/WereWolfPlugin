package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Trapper extends RolesVillage implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Trapper(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }

    private boolean power = true;

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.trapper.tracking_message");
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.trapper.description"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (hasPower()) return;

        getAffectedPlayers()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .peek(playerWW -> stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" "))
                .forEach(playerWW -> stringBuilder.append(game.getScore()
                        .updateArrow(player, playerWW.getLocation())));

        event.setActionBar(stringBuilder.toString());
    }
}
