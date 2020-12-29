package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.SeerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Seer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSeer implements Commands {

    private final Main main;

    public CommandSeer(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles seer = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (player.getHealth() < 7) {
            player.sendMessage(game.translate("werewolf.role.seer.not_enough_life"));
        } else {
            Roles role1 = playerWW1.getRole();

            String camp = "werewolf.categories.villager";

            if ((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.NEUTRAL)) || role1.isNeutral()) {
                camp = "werewolf.categories.neutral";
            } else if ((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.WEREWOLF)) || (!(role1 instanceof Display) && role1.isWereWolf())) {
                camp = "werewolf.categories.werewolf";
            }

            SeerEvent seerEvent = new SeerEvent(playerWW, playerWW1, camp);
            ((Power) seer).setPower(false);
            Bukkit.getPluginManager().callEvent(seerEvent);

            if (seerEvent.isCancelled()) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }

            ((AffectedPlayers) seer).addAffectedPlayer(playerWW1);

            if (seerEvent.getCamp().equals("werewolf.categories.villager")) {
                playerWW.removePlayerMaxHealth(6);
                ((Seer) seer).setDisablePower();
                player.sendMessage(game.translate("werewolf.role.seer.see_villager"));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.villager")));
                }
                playerWW.removePlayerHealth(6);
            } else if (seerEvent.getCamp().equals("werewolf.categories.werewolf")) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform", game.translate("werewolf.categories.werewolf")));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.werewolf")));
                }
            } else {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform", game.translate("werewolf.categories.neutral")));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.neutral")));
                }
            }
        }
    }
}
