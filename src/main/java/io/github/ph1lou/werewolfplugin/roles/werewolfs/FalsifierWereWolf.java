package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.NewDisplayRole;
import io.github.ph1lou.werewolfapi.events.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FalsifierWereWolf extends RolesWereWolf implements Display {

    private String displayCamp = Camp.VILLAGER.getKey();
    private String displayRole = RolesBase.VILLAGER.getKey();

    public FalsifierWereWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public boolean isDisplayCamp(String camp) {
        return (this.displayCamp.equals(camp));
    }

    @Override
    public String getDisplayCamp() {
        return (this.displayCamp);
    }

    @Override
    public void setDisplayCamp(String camp) {
        this.displayCamp = camp;
    }

    @Override
    public String getDisplayRole() {
        return (this.displayRole);
    }

    @Override
    public void setDisplayRole(String role) {
        this.displayRole = role;
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        List<UUID> players = new ArrayList<>();
        for (PlayerWW playerWW1 : game.getPlayerWW()) {
            if (playerWW1.isState(StatePlayer.ALIVE) && !playerWW1.equals(getPlayerWW())) {
                players.add(playerWW1.getUUID());
            }
        }
        if (players.size() <= 0) {
            return;
        }

        PlayerWW displayWW = game.autoSelect(getPlayerWW());

        Roles roles = displayWW.getRole();
        NewDisplayRole newDisplayRole = new NewDisplayRole(getPlayerWW(), roles.getKey(), roles.getCamp().getKey());
        Bukkit.getPluginManager().callEvent(newDisplayRole);

        if (newDisplayRole.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            setDisplayCamp(Camp.WEREWOLF.getKey());
            setDisplayRole(RolesBase.FALSIFIER_WEREWOLF.getKey());
        } else {
            setDisplayRole(roles.getKey());
            setDisplayCamp(newDisplayRole.getNewDisplayCamp());
        }
        getPlayerWW().sendMessageWithKey("werewolf.role.falsifier_werewolf.display_role_message",
                game.translate(getDisplayRole()));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.falsifier_werewolf.description"))
                .setEffects(() -> game.translate("werewolf.description.werewolf"))
                .addExtraLines(() -> game.translate("werewolf.role.falsifier_werewolf.role",
                        game.translate(this.displayRole)))
                .build();
    }


    @Override
    public void recoverPower() {

    }
}
