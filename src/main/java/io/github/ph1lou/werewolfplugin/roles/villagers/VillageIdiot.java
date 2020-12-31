package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.events.VillageIdiotEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public class VillageIdiot extends RolesVillage implements Power {

    private boolean power = true;


    public VillageIdiot(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    public @NotNull String getDescription() {

        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.village_idiot.description")) +
                game.translate("werewolf.description._");
    }

    public void recoverPower() {
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTag event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (power) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.setSuffix(event.getSuffix() + game.translate("werewolf.role.village_idiot.suffix"));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) {
            return;
        }
        if (!event.getPlayerWW().equals(getPlayerWW())) {
            return;
        }
        if (!hasPower()) {
            return;
        }

        PlayerWW killerWW = this.getPlayerWW().getLastKiller();

        if (killerWW != null && !killerWW.getRole().isWereWolf()) {

            setPower(false);

            VillageIdiotEvent villageIdiotEvent = new VillageIdiotEvent(getPlayerWW(), killerWW);
            Bukkit.getPluginManager().callEvent(villageIdiotEvent);

            if (villageIdiotEvent.isCancelled()) {
                getPlayerWW().sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }
            game.resurrection(getPlayerWW());
            Player player = Bukkit.getPlayer(getPlayerUUID());
            if (player != null) {
                Bukkit.getPluginManager().callEvent(
                        new UpdateNameTagEvent(player));
            }
            getPlayerWW().removePlayerMaxHealth(4);
            event.setCancelled(true);
            Bukkit.broadcastMessage(game.translate("werewolf.role.village_idiot.announce", getPlayerWW().getName()));
        }

    }


}

