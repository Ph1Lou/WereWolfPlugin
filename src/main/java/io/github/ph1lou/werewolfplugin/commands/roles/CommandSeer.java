package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Seer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSeer implements ICommands {

    private final Main main;

    public CommandSeer(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole seer = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (player.getHealth() < 7) {
            playerWW.sendMessageWithKey("werewolf.role.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            String camp = "werewolf.categories.villager";

            if (role1.isDisplayCamp(Camp.NEUTRAL.getKey())) {
                camp = "werewolf.categories.neutral";
            } else if (role1.isDisplayCamp(Camp.WEREWOLF.getKey())) {
                camp = "werewolf.categories.werewolf";
            }

            SeerEvent seerEvent = new SeerEvent(playerWW, playerWW1, camp);
            ((IPower) seer).setPower(false);
            Bukkit.getPluginManager().callEvent(seerEvent);

            if (seerEvent.isCancelled()) {
                playerWW.sendMessageWithKey("werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) seer).addAffectedPlayer(playerWW1);

            if (seerEvent.getCamp().equals("werewolf.categories.villager")) {
                ((Seer) seer).setDisablePower();
                playerWW.sendMessageWithKey("werewolf.role.seer.see_villager");
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.villager")));
                }
                playerWW.removePlayerHealth(6);
            } else if (seerEvent.getCamp().equals("werewolf.categories.werewolf")) {
                playerWW.sendMessageWithKey("werewolf.role.seer.see_perform", game.translate("werewolf.categories.werewolf"));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.werewolf")));
                }
            } else {
                playerWW.sendMessageWithKey("werewolf.role.seer.see_perform", game.translate("werewolf.categories.neutral"));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.neutral")));
                }
            }
        }
    }
}
