package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.TroubleMakerDeathEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Troublemaker extends RolesVillage implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Troublemaker(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new TroubleMakerDeathEvent(getPlayerWW()));
        Bukkit.broadcastMessage("werewolf.role.troublemaker.troublemaker_death");

        AtomicInteger i = new AtomicInteger();

        game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .forEach(playerWW -> {
                    game.getMapManager().transportation(playerWW,
                            i.get() * 2 * Math.PI / game.getScore().getPlayerSize());
                    i.getAndIncrement();
                });
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
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.troublemaker.description"))
                .setPower(() -> game.translate("werewolf.role.troublemaker.chat"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    public void onNightAnnounceWereWOlfChat(NightEvent event) {

        if (!game.getConfig().isConfigActive(ConfigsBase.WEREWOLF_CHAT.getKey())) return;

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.announce", game.getScore().conversion(game.getConfig().getTimerValue(TimersBase.WEREWOLF_CHAT_DURATION.getKey())), game.getConfig().getWereWolfChatMaxMessage());

    }

    @EventHandler
    public void onRequestAccessWereWolfChat(WereWolfCanSpeakInChatEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        event.setCanSpeak(true);
    }
}
