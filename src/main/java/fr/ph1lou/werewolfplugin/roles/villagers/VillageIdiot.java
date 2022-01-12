package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
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

        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.role.village_idiot.description"))
                .setPower(this.game.translate(this.power ?
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
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (this.power) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.setSuffix(event.getSuffix() + " " + this.game.translate("werewolf.role.village_idiot.suffix"));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) {
            return;
        }
        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }
        if (!this.hasPower()) {
            return;
        }

        if (!isAbilityEnabled()) return;

        Optional<IPlayerWW> killerWW = this.getPlayerWW().getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (killerWW.get().getRole().isWereWolf()) {
            return;
        }

        this.setPower(false);

        VillageIdiotEvent villageIdiotEvent =
                new VillageIdiotEvent(this.getPlayerWW(), killerWW.get());
        Bukkit.getPluginManager().callEvent(villageIdiotEvent);

        if (villageIdiotEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }
        this.game.resurrection(getPlayerWW());
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player != null) {
            Bukkit.getPluginManager().callEvent(
                    new UpdateNameTagEvent(player));
        }
        this.getPlayerWW().removePlayerMaxHealth(4);
        event.setCancelled(true);
        Bukkit.broadcastMessage(this.game.translate(Prefix.YELLOW.getKey() , "werewolf.role.village_idiot.announce",
                Formatter.player(this.getPlayerWW().getName())));
    }
}

