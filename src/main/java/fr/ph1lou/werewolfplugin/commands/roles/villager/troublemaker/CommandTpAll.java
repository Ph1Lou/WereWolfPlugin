package fr.ph1lou.werewolfplugin.commands.roles.villager.troublemaker;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.villagers.Troublemaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code /lg tpall} : disperse tous les autres joueurs vivants sur la carte,
 * une fois par partie. Si non utilisé, la dispersion se produit automatiquement
 * à la mort du Trublion (cf. {@link Troublemaker#onFinalDeath}).
 */
@RoleCommand(key = "werewolf.roles.troublemaker.command_tpall",
        roleKeys = RoleBase.TROUBLEMAKER,
        requiredPowerKey = Troublemaker.POWER_TPALL,
        argNumbers = 0)
public class CommandTpAll implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Troublemaker troublemaker = (Troublemaker) playerWW.getRole();

        troublemaker.usePower(Troublemaker.POWER_TPALL);

        AtomicInteger i = new AtomicInteger();
        game.getAlivePlayersWW().stream()
                .filter(playerWW1 -> !playerWW1.equals(playerWW))
                .forEach(playerWW1 -> {
                    game.getMapManager().transportation(playerWW1,
                            i.get() * 2 * Math.PI / game.getPlayersCount());
                    i.getAndIncrement();
                });

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.tpall_perform");

        game.checkVictory();
    }
}
