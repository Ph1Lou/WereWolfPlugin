package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.GrimEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GrimyWereWolf extends RolesWereWolf implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public GrimyWereWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
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
                .setDescription(() -> game.translate("werewolf.role.grimy_werewolf.description"))
                .setEffects(() -> game.translate("werewolf.description.werewolf"))
                .build();
    }


    @Override
    public void recoverPower() {
        if (!game.getConfig().isTrollSV()) {
            game.getConfig().addOneRole(RolesBase.WEREWOLF.getKey());
        }
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().getRole().equals(this)) return;

        if (this.power) {
            game.getConfig().removeOneRole(RolesBase.WEREWOLF.getKey());
            this.power = false;
        } else if (!this.affectedPlayer.isEmpty()) {
            game.getConfig().removeOneRole(this.affectedPlayer.get(0).getRole().getKey());
            Bukkit.broadcastMessage(game.translate("werewolf.role.grimy_werewolf.actualize", game.translate(this.affectedPlayer.get(0).getRole().getKey())));
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (event.isCancelled()) return;

        if (!event.getPlayerWW().getLastKiller().isPresent()) return;

        if (!event.getPlayerWW().getLastKiller().get().equals(this.getPlayerWW())) return;

        if (!this.power) return;

        this.power = false;


        GrimEvent grimEvent = new GrimEvent(this.getPlayerWW(), event.getPlayerWW());
        Bukkit.getPluginManager().callEvent(grimEvent);

        if (grimEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        getPlayerWW().sendMessageWithKey("werewolf.role.grimy_werewolf.perform", event.getPlayerName(), game.translate(event.getRole()));

        this.affectedPlayer.add(event.getPlayerWW());

        event.setRole(RolesBase.WEREWOLF.getKey());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTag event) {

        WereWolfAPI game = main.getWereWolfAPI();

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        if (!this.affectedPlayer.contains(playerWW)) return;

        if (game.getConfig().isConfigActive(ConfigsBase.SHOW_ROLE_TO_DEATH.getKey())) {
            event.setSuffix(event.getSuffix()
                    .replace(game.translate(playerWW.getRole().getKey()),
                            "")
                    + game.translate(RolesBase.WEREWOLF.getKey()));
        } else if (game.getConfig().isConfigActive(ConfigsBase.SHOW_ROLE_CATEGORY_TO_DEATH.getKey())) {
            event.setSuffix(event.getSuffix()
                    .replace(game.translate(playerWW.getRole().getCamp().getKey()),
                            "")
                    + game.translate(Camp.WEREWOLF.getKey()));
        }

    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }
}
