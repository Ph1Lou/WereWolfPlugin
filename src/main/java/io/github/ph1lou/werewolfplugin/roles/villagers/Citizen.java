package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Citizen extends RolesVillage implements LimitedUse, AffectedPlayers, Power {

    private int use = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Citizen(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
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


        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
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
    public String getDescription() {
        return game.translate("werewolf.role.citizen.description");
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
