package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Priestess extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Priestess(WereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);

        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!this.affectedPlayer.contains(event.getPlayerWW())) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        getPlayerWW().sendMessageWithKey("werewolf.role.priestess.werewolf_death");

        this.affectedPlayer.remove(event.getPlayerWW());

        getPlayerWW().addPlayerMaxHealth(2);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.priestess.perform",
                game.getConfig().getDistancePriestess(),
                Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.priestess.description"))
                .setItems(() -> game.translate("werewolf.role.priestess.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getAura() {
        return Aura.LIGHT;
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void sendDeathMessage(AnnouncementDeathEvent event) {

        if (event.getTargetPlayer().equals(this.getPlayerWW())) {
            return; //la prêtresse voit les vrais rôles
        }

        if (event.getTargetPlayer().equals(event.getPlayerWW())) {
            return; //le mort voit son vrai rôle
        }

        IPlayerWW playerWW = event.getTargetPlayer();

        if (playerWW.getRole().isNeutral()) {
            if (getPlayerWW().isState(StatePlayer.ALIVE) && game.getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.role.priestess.magic");
            }
        } else if (game.getRandom().nextFloat() < 0.8) {

            if (getPlayerWW().isState(StatePlayer.ALIVE) && playerWW.getRole().isWereWolf()) {
                event.setRole("werewolf.role.priestess.magic");
            }
        } else {
            if (getPlayerWW().isState(StatePlayer.ALIVE) && !playerWW.getRole().isWereWolf()) {
                event.setRole("werewolf.role.priestess.magic");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTag event) {

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix("");
    }
}
