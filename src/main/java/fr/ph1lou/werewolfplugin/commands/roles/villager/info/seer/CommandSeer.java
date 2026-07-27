package fr.ph1lou.werewolfplugin.commands.roles.villager.info.seer;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Seer;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.seer.command", roleKeys = RoleBase.SEER,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandSeer implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole seer = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (playerWW.getHealth() < 7) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.seer.not_enough_life");
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
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) seer).addAffectedPlayer(playerWW1);

            if (seerEvent.getCamp().equals(Camp.VILLAGER.getKey())) {
                ((Seer) seer).setDisablePower();
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.seer.see_villager");
                if (game.getConfig().isConfigActive(ConfigBase.CHATTY_SEER)) {
                    Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.chatty_seer.see_perform",
                            Formatter.format("&camp&", game.translate(Camp.VILLAGER.getKey()))));
                }
                playerWW.removePlayerHealth(6);
            } else if (seerEvent.getCamp().equals(Camp.WEREWOLF.getKey())) {
                playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.seer.see_perform",
                        Formatter.format("&camp&", game.translate(Camp.WEREWOLF.getKey())));
                if (game.getConfig().isConfigActive(ConfigBase.CHATTY_SEER)) {
                    Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.chatty_seer.see_perform",
                            Formatter.format("&camp&", game.translate(Camp.WEREWOLF.getKey()))));
                }
            } else {
                playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.seer.see_perform",
                        Formatter.format("&camp&", game.translate(Camp.NEUTRAL.getKey())));
                if (game.getConfig().isConfigActive(ConfigBase.CHATTY_SEER)) {
                    Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.chatty_seer.see_perform",
                            Formatter.format("&camp&", game.translate(Camp.NEUTRAL.getKey()))));
                }
            }
        }
    }
}
