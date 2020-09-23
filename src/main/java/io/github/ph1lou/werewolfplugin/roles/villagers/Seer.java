package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.ChestEvent;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Seer extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private int dayNumber=-8;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Seer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {

        super(main,game,uuid);
        setPower(false);
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

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }

        if (game.getConfig().getConfigValues().get("werewolf.menu.global.seer_every_other_day") && event.getNumber() == dayNumber + 1) {
            return;
        }

        setPower(true);
        dayNumber = event.getNumber();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }


        player.sendMessage(game.translate("werewolf.role.seer.see_camp_message", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.power_duration"))));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.seer.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.seer.display";
    }

    @Override
    public void recoverPotionEffect(@NotNull Player player) {
        super.recoverPotionEffect(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();
        if (!game.getConfig().getConfigValues().get("werewolf.menu.global.event_seer_death")) return;

        if(!uuid.equals(getPlayerUUID())) return;

        Bukkit.getPluginManager().callEvent(new ChestEvent());
        game.getConfig().getConfigValues().put("werewolf.menu.global.event_seer_death", false);
    }


}
