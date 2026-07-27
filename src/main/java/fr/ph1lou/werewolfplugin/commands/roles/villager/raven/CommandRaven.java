package fr.ph1lou.werewolfplugin.commands.roles.villager.raven;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.roles.raven.CurseEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.raven.command",
        roleKeys = RoleBase.RAVEN,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE },
                rejectIfAlreadyAffected = true)))
public class CommandRaven implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole raven = playerWW.getRole();
        IPlayerWW playerWW1 = ctx.player(0);

        CurseEvent curseEvent = new CurseEvent(playerWW, playerWW1);
        ((IPower) raven).setPower(false);
        Bukkit.getPluginManager().callEvent(curseEvent);

        if (curseEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) raven).clearAffectedPlayer();
        ((IAffectedPlayers) raven).addAffectedPlayer(playerWW1);
        playerWW1.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.JUMP_BOOST, playerWW.getRole().getKey()));
        playerWW1.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(), Aura.DARK, 20, true));
        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.get_curse");
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.curse_perform",
                Formatter.player(playerWW1.getName()));
    }
}
