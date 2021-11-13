package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.roles.villagers.Seer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSeer implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole seer = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (player.getHealth() < 7) {
            playerWW.sendMessageWithKey("werewolf.role.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            String camp = Camp.VILLAGER.getKey();

            if (role1.isDisplayCamp(Camp.NEUTRAL.getKey())) {
                camp = Camp.NEUTRAL.getKey();
            } else if (role1.isDisplayCamp(Camp.WEREWOLF.getKey())) {
                camp = Camp.WEREWOLF.getKey();
            }

            SeerEvent seerEvent = new SeerEvent(playerWW, playerWW1, camp);
            ((IPower) seer).setPower(false);
            Bukkit.getPluginManager().callEvent(seerEvent);

            if (seerEvent.isCancelled()) {
                playerWW.sendMessageWithKey("werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) seer).addAffectedPlayer(playerWW1);

            if (seerEvent.getCamp().equals(Camp.VILLAGER.getKey())) {
                ((Seer) seer).setDisablePower();
                playerWW.sendMessageWithKey("werewolf.role.seer.see_villager");
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform",
                            Formatter.format("&camp&",game.translate(Camp.VILLAGER.getKey()))));
                }
                playerWW.removePlayerHealth(6);
            } else if (seerEvent.getCamp().equals(Camp.WEREWOLF.getKey())) {
                playerWW.sendMessageWithKey("werewolf.role.seer.see_perform",
                        Formatter.format("&camp&",game.translate(Camp.WEREWOLF.getKey())));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform",
                            Formatter.format("&camp&",game.translate(Camp.WEREWOLF.getKey()))));
                }
            } else {
                playerWW.sendMessageWithKey("werewolf.role.seer.see_perform",
                        Formatter.format("&camp&",game.translate(Camp.NEUTRAL.getKey())));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform",
                            Formatter.format("&camp&",game.translate(Camp.NEUTRAL.getKey()))));
                }
            }
        }
    }
}
