package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Trapper extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Trapper(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
        setPower(false);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate(
                "werewolf.role.trapper.tracking_message"));
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.trapper.description");
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        if (Bukkit.getPlayer(event.getPlayerUUID()) == null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (hasPower()) return;

        getAffectedPlayers()
                .stream()
                .map(game::getPlayerWW)
                .filter(Objects::nonNull)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .peek(playerWW -> stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" "))
                .map(PlayerWW::getRole)
                .map(Roles::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player1 -> stringBuilder.append(game.getScore()
                        .updateArrow(player, player1.getLocation())));

        event.setActionBar(stringBuilder.toString());
    }
}
