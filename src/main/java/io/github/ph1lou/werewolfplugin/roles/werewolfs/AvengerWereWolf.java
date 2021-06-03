package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.DeathAvengerListEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.RegisterAvengerListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvengerWereWolf extends RoleWereWolf implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public AvengerWereWolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.avenger_werewolf.description", game.getConfig().getDistanceAvengerWerewolf()))
                .setPower(game.translate("werewolf.role.avenger_werewolf.power"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.affectedPlayers.contains(event.getPlayerWW())) {
            return;
        }

        DeathAvengerListEvent event1 = new DeathAvengerListEvent(this.getPlayerWW(), event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            return;
        }


        this.getPlayerWW().sendMessageWithKey("werewolf.role.avenger_werewolf.remove", event.getPlayerWW().getName());
        this.getPlayerWW().addPlayerMaxHealth(2);
    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Bukkit.getOnlinePlayers()
                .stream()
                .map(player1 -> game.getPlayerWW(player1.getUniqueId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(playerWW -> {
                    if (player.getWorld().equals(playerWW.getLocation().getWorld())) {
                        return player.getLocation().distance(playerWW.getLocation()) < game.getConfig().getDistanceAvengerWerewolf();
                    }
                    return false;
                })
                .forEach(playerWW -> {
                    if (!this.affectedPlayers.contains(playerWW)) {
                        RegisterAvengerListEvent event1 = new RegisterAvengerListEvent(this.getPlayerWW(), playerWW);

                        Bukkit.getPluginManager().callEvent(event1);

                        if (event1.isCancelled()) {
                            return;
                        }

                        this.affectedPlayers.add(playerWW);
                        this.getPlayerWW().sendMessageWithKey("werewolf.role.avenger_werewolf.add", playerWW.getName());
                    }
                });
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().removePlayerMaxHealth(6);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayers.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayers.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return new ArrayList<>(this.affectedPlayers);
    }
}
