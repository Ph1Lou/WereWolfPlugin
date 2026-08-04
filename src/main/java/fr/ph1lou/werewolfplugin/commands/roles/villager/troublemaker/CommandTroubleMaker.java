package fr.ph1lou.werewolfplugin.commands.roles.villager.troublemaker;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerSwitchEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Troublemaker;
import org.bukkit.Bukkit;

/**
 * {@code /lg switch [Pseudo1] [Pseudo2]} : échange les rôles des deux joueurs
 * ciblés, une fois par partie et avant la révélation de la liste des loups.
 *
 * <p>Les rôles sont échangés dans l'état exact où ils se trouvent (condition de
 * victoire incluse). Les amoureux et le protégé de l'Ange Gardien restent liés
 * au joueur (stockés sur le joueur, pas sur le rôle). En mode solitaire, le
 * Trublion apprend les deux rôles échangés.</p>
 */
@RoleCommand(key = "werewolf.roles.troublemaker.command",
        roleKeys = RoleBase.TROUBLEMAKER,
        requiredPowerKey = Troublemaker.POWER_SWITCH,
        argNumbers = 2,
        args = @Params(players = {
                @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true),
                @PlayerArg(index = 1, states = { StatePlayer.ALIVE }, excludeSelf = true)
        }))
public class CommandTroubleMaker implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Troublemaker troublemaker = (Troublemaker) playerWW.getRole();

        if (troublemaker.isWwListRevealed()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.troublemaker.too_late");
            return;
        }

        IPlayerWW playerWW1 = ctx.player(0);
        IPlayerWW playerWW2 = ctx.player(1);

        if (playerWW1.getUUID().equals(playerWW2.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.two_distinct_player");
            return;
        }

        // Rôles relevés AVANT l'échange (pour la révélation solitaire).
        String roleKey1 = playerWW1.getRole().getKey();
        String roleKey2 = playerWW2.getRole().getKey();

        TroubleMakerSwitchEvent switchEvent =
                new TroubleMakerSwitchEvent(playerWW, playerWW1, playerWW2);
        Bukkit.getPluginManager().callEvent(switchEvent);

        if (switchEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        // Échange croisé des rôles (modèle random_events/Swap.java, sans le bloc amoureux).
        IRole roles1 = playerWW1.getRole();
        IRole roles2 = playerWW2.getRole();
        playerWW1.setRole(roles2);
        playerWW2.setRole(roles1);

        // Réinitialisation PV / effets liés à l'ancien rôle.
        playerWW1.addPlayerMaxHealth(20 - playerWW1.getMaxHealth());
        playerWW2.addPlayerMaxHealth(20 - playerWW2.getMaxHealth());
        playerWW1.clearPotionEffects(roles1.getKey());
        playerWW2.clearPotionEffects(roles2.getKey());
        playerWW1.clearPotionEffects(RoleBase.WEREWOLF);
        playerWW2.clearPotionEffects(RoleBase.WEREWOLF);

        roles1.recoverPower();
        roles2.recoverPower();
        roles1.recoverPotionEffects();
        roles2.recoverPotionEffects();

        troublemaker.usePower(Troublemaker.POWER_SWITCH);

        // Joueurs concernés informés (sans révéler l'autre rôle ni le Trublion).
        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.switch_concerned");
        playerWW2.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.switch_concerned");

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.switch_perform",
                Formatter.format("&player1&", playerWW1.getName()),
                Formatter.format("&player2&", playerWW2.getName()));

        // Mode solitaire : le Trublion connaît les deux rôles échangés.
        if (troublemaker.isSolitary()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.switch_reveal",
                    Formatter.format("&role1&", game.translate(roleKey1)),
                    Formatter.format("&role2&", game.translate(roleKey2)));
        }

        game.checkVictory();
    }
}
