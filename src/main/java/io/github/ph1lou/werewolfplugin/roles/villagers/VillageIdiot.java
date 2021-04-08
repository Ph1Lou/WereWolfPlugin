package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VillageIdiot extends RoleVillage implements IPower {

    private boolean power = true;


    public VillageIdiot(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.village_idiot.description"))
                .setPower(() -> game.translate(power ?
                        "werewolf.role.village_idiot.power_on" :
                        "werewolf.role.village_idiot.power_off"))
                .build();
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
    public void onUpdateNameTag(UpdatePlayerNameTag event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (power) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.setSuffix(event.getSuffix() + " " + game.translate("werewolf.role.village_idiot.suffix"));
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

        Optional<IPlayerWW> killerWW = this.getPlayerWW().getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (killerWW.get().getRole().isWereWolf()) {
            return;
        }

        setPower(false);

        VillageIdiotEvent villageIdiotEvent =
                new VillageIdiotEvent(getPlayerWW(), killerWW.get());
        Bukkit.getPluginManager().callEvent(villageIdiotEvent);

        if (villageIdiotEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
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
        Bukkit.broadcastMessage(game.translate("werewolf.role.village_idiot.announce",
                getPlayerWW().getName()));
    }
}

