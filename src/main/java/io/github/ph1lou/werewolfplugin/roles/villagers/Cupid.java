package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.AroundLover;
import io.github.ph1lou.werewolfapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Cupid extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Cupid(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

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

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.cupid.description");
    }


    @Override
    public void recoverPowerAfterStolen() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (hasPower()) {
            player.sendMessage(
                    game.translate("werewolf.role.cupid.lover_designation_message",
                            game.getScore().conversion(
                                    game.getConfig()
                                            .getTimerValues()
                                            .get("werewolf.menu.timers.lover_duration"))));
        } else {
            player.sendMessage(
                    game.translate("werewolf.role.cupid.designation_perform",
                            game.getPlayersWW().get(
                                    getAffectedPlayers().get(0))
                                    .getName(),
                            game.getPlayersWW().get(
                                    getAffectedPlayers().get(1))
                                    .getName()));
        }
    }

    @EventHandler
    public void onLover(AroundLover event) {

        if (!Objects.requireNonNull(
                game.getPlayerWW(
                        getPlayerUUID())).isState(StatePlayer.ALIVE)) return;

        if (event.getUuidS().contains(getPlayerUUID())) {
            for (UUID uuid : affectedPlayer) {
                event.addPlayer(uuid);
            }
            return;
        }

        for (UUID uuid : event.getUuidS()) {
            if (affectedPlayer.contains(uuid)) {
                event.addPlayer(getPlayerUUID());
                break;
            }
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }


    @Override
    public void recoverPower() {


        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        player.sendMessage(game.translate(
                "werewolf.role.cupid.lover_designation_message",
                game.getScore().conversion(
                        game.getConfig().getTimerValues()
                                .get(TimersBase.LOVER_DURATION
                                        .getKey()))));
    }
}
