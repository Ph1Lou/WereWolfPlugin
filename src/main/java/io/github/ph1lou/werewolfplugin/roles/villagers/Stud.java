package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.StudLoverEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Stud extends RolesVillage implements Power {
    private boolean power = true;

    public Stud(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.stud.description");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        if (!hasPower()) return;

        PlayerWW killerWW = getPlayerWW().getLastKiller();

        if (killerWW == null) return;

        if (!killerWW.isState(StatePlayer.ALIVE)) return;

        for (LoverAPI loverAPI : getPlayerWW().getLovers()) {

            if (!loverAPI.isKey(RolesBase.CURSED_LOVER.getKey())) {
                if (loverAPI.getLovers().contains(killerWW)) return;
            }
        }

        Bukkit.getPluginManager().callEvent(new StudLoverEvent(getPlayerWW(), killerWW));

        setPower(false);

        Lover lover = new Lover(game, new ArrayList<>(Arrays.asList(getPlayerWW(), killerWW)));

        Bukkit.getPluginManager().registerEvents(lover, (Plugin) main);

        game.getLoversManager().addLover(lover);

        lover.announceLovers();

        event.setCancelled(true);

        game.resurrection(getPlayerWW());
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
