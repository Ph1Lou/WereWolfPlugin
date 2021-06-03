package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.events.roles.falsifier_werewolf.NewDisplayRole;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FalsifierWereWolf extends RoleWereWolf {

    private Aura displayAura;

    public FalsifierWereWolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        this.setDisplayCamp(Camp.VILLAGER.getKey());
        this.setDisplayRole(RolesBase.VILLAGER.getKey());
        this.displayAura = Aura.LIGHT;
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        List<UUID> players = new ArrayList<>();
        for (IPlayerWW playerWW1 : game.getPlayersWW()) {
            if (playerWW1.isState(StatePlayer.ALIVE) && !playerWW1.equals(getPlayerWW())) {
                players.add(playerWW1.getUUID());
            }
        }
        if (players.size() <= 0) {
            return;
        }

        IPlayerWW displayWW = game.autoSelect(getPlayerWW());

        IRole roles = displayWW.getRole();
        NewDisplayRole newDisplayRole = new NewDisplayRole(this.getPlayerWW(), roles.getKey(), roles.getCamp().getKey());
        Bukkit.getPluginManager().callEvent(newDisplayRole);

        if (newDisplayRole.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            setDisplayCamp(Camp.WEREWOLF.getKey());
            setDisplayRole(RolesBase.FALSIFIER_WEREWOLF.getKey());
            displayAura = Aura.DARK;
        } else {
            setDisplayRole(roles.getKey());
            setDisplayCamp(newDisplayRole.getNewDisplayCamp());
            displayAura = roles.getDefaultAura();
        }
        this.getPlayerWW().sendMessageWithKey("werewolf.role.falsifier_werewolf.display_role_message",
                game.translate(getDisplayRole()));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.falsifier_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .addExtraLines(game.translate("werewolf.role.falsifier_werewolf.role",
                        game.translate(this.getDisplayRole())))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return this.displayAura;
    }

    @Override
    public Aura getAura() {
        return this.displayAura;
    }
}
