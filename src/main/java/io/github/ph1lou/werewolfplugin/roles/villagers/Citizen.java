package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.VoteEndEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Citizen extends RolesVillage implements LimitedUse, AffectedPlayers, Power {

    private int use = 0;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Citizen(GetWereWolfAPI main, PlayerWW playerWW, String key) {
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
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @EventHandler
    public void onVoteEnd(VoteEndEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (player == null) return;


        if (getUse() < 2) {
            player.spigot().sendMessage(seeVote());
        }

        if (hasPower()) {
            player.spigot().sendMessage(cancelVote());
        }
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.citizen.description");
    }


    @Override
    public void recoverPower() {

    }


    public TextComponent cancelVote() {

        TextComponent cancelVote = new TextComponent(
                game.translate("werewolf.role.citizen.click"));
        cancelVote.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s",
                                game.translate("werewolf.role.citizen.command_2"))));
        cancelVote.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(game.translate("werewolf.role.citizen.cancel"))
                                .create()));


        TextComponent cancel =
                new TextComponent(game.translate("werewolf.role.citizen.cancel_vote_message",
                        hasPower() ? 1 : 0));

        cancel.addExtra(cancelVote);

        cancel.addExtra(new TextComponent(game.translate("werewolf.role.citizen.time_left",
                game.getScore().conversion(
                        game.getConfig().getTimerValues().get(
                                TimersBase.CITIZEN_DURATION.getKey())))));


        return cancel;

    }


    public TextComponent seeVote() {

        TextComponent seeVote = new TextComponent(
                game.translate("werewolf.role.citizen.click"));
        seeVote.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.role.citizen.command_1"))));
        seeVote.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(game.translate("werewolf.role.citizen.see"))
                        .create()));


        TextComponent see = new TextComponent(
                game.translate("werewolf.role.citizen.see_vote_message",
                        2 - getUse()));
        see.addExtra(seeVote);


        see.addExtra(new TextComponent(game.translate("werewolf.role.citizen.time_left",
                game.getScore().conversion(
                        game.getConfig().getTimerValues().get(
                                TimersBase.CITIZEN_DURATION.getKey())))));


        return see;

    }


}
