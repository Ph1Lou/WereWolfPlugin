package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.AroundLover;
import io.github.ph1lou.werewolfapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Cupid extends RolesVillage implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Cupid(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

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

    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.cupid.description")) +
                game.translate("werewolf.description.equipment", game.translate("werewolf.role.cupid.extra", game.getConfig().getLimitPowerBow() + 1)) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.cupid.items")) +
                game.translate("werewolf.description._");
    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if (hasPower()) {
            getPlayerWW().sendMessage(
                    game.translate("werewolf.role.cupid.lover_designation_message",
                            game.getScore().conversion(
                                    game.getConfig()
                                            .getTimerValue("werewolf.menu.timers.lover_duration"))));
            return;
        }

        if (affectedPlayer.size() < 2) return;

        PlayerWW loverWW1 = affectedPlayer.get(0);
        PlayerWW loverWW2 = affectedPlayer.get(1);


        getPlayerWW().sendMessage(
                game.translate("werewolf.role.cupid.designation_perform",
                        loverWW1.getName(),
                        loverWW2.getName()));
    }

    @EventHandler
    public void onAroundLover(AroundLover event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getPlayerWWS().contains(getPlayerWW())) {
            for (PlayerWW playerWW : affectedPlayer) {
                event.addPlayer(playerWW);
            }
            return;
        }

        for (PlayerWW playerWW : event.getPlayerWWS()) {
            if (affectedPlayer.contains(playerWW)) {
                event.addPlayer(getPlayerWW());
                break;
            }
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }


    @Override
    public void recoverPower() {

        getPlayerWW().sendMessage(game.translate(
                "werewolf.role.cupid.lover_designation_message",
                game.getScore().conversion(
                        game.getConfig().getTimerValue(TimersBase.LOVER_DURATION
                                .getKey()))));
    }
}
