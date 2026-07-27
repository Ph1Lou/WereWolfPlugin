package fr.ph1lou.werewolfplugin.commands.roles.werewolf.bloodthirsty;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bloodthirsty_werewolf.BloodthirstyWerewolfHuntDownEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.werewolfs.BloodthirstyWereWolf;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.bloodthirsty_werewolf.command",
        roleKeys = RoleBase.BLOODTHIRSTY_WEREWOLF,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true))
)
public class CommandBloodthirsty implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        BloodthirstyWereWolf sanguinaryWolf = (BloodthirstyWereWolf) playerWW.getRole();

        IPlayerWW selectedPlayerWW = ctx.player(0);

        BloodthirstyWerewolfHuntDownEvent event = new BloodthirstyWerewolfHuntDownEvent(playerWW, selectedPlayerWW);

        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bloodthirsty_werewolf.player_cant_be_traqued", Formatter.player(selectedPlayerWW.getName()));
            return;
        }

        sanguinaryWolf.addAffectedPlayer(selectedPlayerWW);
    }
}
