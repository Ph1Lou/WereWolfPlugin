package fr.ph1lou.werewolfplugin.commands.roles.villager.benefactor;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.benefactor.BenefactorGiveHeartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.villagers.Benefactor;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.benefactor.command",
        roleKeys = RoleBase.BENEFACTOR,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                rejectIfAlreadyAffected = true,
                alreadyAffectedMessageKey = "werewolf.roles.benefactor.already_use_on_player")))
public class CommandBenefactor implements IParameterizedCommandRole {

    private static final long DELIVERY_DELAY = 3 * 60 * 20L;

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Benefactor benefactor = (Benefactor) playerWW.getRole();

        if (!benefactor.canGive()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.benefactor.cooldown");
            return;
        }

        if (benefactor.getAffectedPlayers().size() >= 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.benefactor.too_many_players");
            return;
        }

        IPlayerWW targetWW = ctx.player(0);

        BenefactorGiveHeartEvent event = new BenefactorGiveHeartEvent(playerWW, targetWW);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        benefactor.addAffectedPlayer(targetWW);
        benefactor.onGiven();

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.benefactor.perform", Formatter.player(targetWW.getName()));

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (!targetWW.isState(StatePlayer.ALIVE)) return;
            targetWW.addPlayerMaxHealth(2);
            targetWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.benefactor.target_message");
        }, DELIVERY_DELAY);
    }
}
