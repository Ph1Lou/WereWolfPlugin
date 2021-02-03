package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.EnchantedEvent;
import io.github.ph1lou.werewolfapi.events.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.events.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlutePlayer extends RolesNeutral implements Power, AffectedPlayers {


    private boolean power = false;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public FlutePlayer(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {

        if (!hasPower()) return;


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(false);

        getPlayerWW().sendMessageWithKey("werewolf.check.end_selection");
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

        getPlayerWW().sendMessageWithKey("werewolf.role.flute_player.power",
                game.getScore()
                        .conversion(game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {

        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.flute_player.description")) +
                (affectedPlayer.isEmpty() ? "" : game.translate("werewolf.role.flute_player.affected", enchantedList()));
    }


    @EventHandler
    public void onDetectVictory(WinConditionsCheckEvent event){

        if(event.isCancelled()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        int counter = 1;
        int playerAlive = 0;

        for (PlayerWW playerWW : game.getPlayerWW()) {
            if (playerWW.isState(StatePlayer.ALIVE)) {
                playerAlive++;
            }
        }

        for (PlayerWW playerWW : affectedPlayer) {
            if (playerWW.isState(StatePlayer.ALIVE)) {
                counter++;
            }
        }

        if (counter == playerAlive) {

            if (!affectedPlayer.isEmpty()) {
                PlayerWW playerWW1 = affectedPlayer.get(0);
                if (playerWW1.isState(StatePlayer.ALIVE)) {
                    affectedPlayer.remove(playerWW1);
                    game.death(playerWW1);
                }
            }
            if(playerAlive==1){
                event.setCancelled(true);
                event.setVictoryTeam(getKey());
            }
        }

    }

    @EventHandler
    public void onEnchantedPlayer(EnchantedEvent event) {

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        String enchantedList = enchantedList();

        for (PlayerWW playerWW : affectedPlayer) {
            playerWW.sendMessageWithKey("werewolf.role.flute_player.list", enchantedList);
        }
    }


    public String enchantedList() {
        StringBuilder sb = new StringBuilder();

        for (PlayerWW playerWW : affectedPlayer) {
            if (playerWW.isState(StatePlayer.ALIVE)) {
                sb.append(playerWW.getName()).append(" ");
            }
        }
        return sb.toString();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void setPower(boolean aBoolean) {
        this.power = aBoolean;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }


}
