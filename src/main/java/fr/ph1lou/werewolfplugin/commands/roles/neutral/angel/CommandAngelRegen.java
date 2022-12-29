package fr.ph1lou.werewolfplugin.commands.roles.neutral.angel;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@RoleCommand(key = "werewolf.roles.guardian_angel.command",
        roleKeys = {RoleBase.ANGEL, RoleBase.GUARDIAN_ANGEL},
        argNumbers = 0)
public class CommandAngelRegen implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        if (playerWW == null) return;

        Angel guardianAngel = (Angel) playerWW.getRole();

        if (!guardianAngel.isChoice(AngelForm.GUARDIAN_ANGEL)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.state_player");
            return;
        }


        if (((ILimitedUse) guardianAngel).getUse() >= 3) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        if (((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.roles.guardian_angel.no_protege");
            return;
        }

        IPlayerWW playerWW1 = ((IAffectedPlayers) guardianAngel).getAffectedPlayers().get(0);

        Player playerProtected = Bukkit.getPlayer(playerWW1.getUUID());

        if (playerProtected == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.roles.guardian_angel.disconnected_protege");
            return;
        }


        ((ILimitedUse) guardianAngel).setUse(((ILimitedUse) guardianAngel).getUse() + 1);

        RegenerationEvent event = new RegenerationEvent(playerWW, ((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().get(0));

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        playerWW1.addPotionModifier(PotionModifier.add( PotionEffectType.REGENERATION,
                400,
                0,
                playerWW.getRole().getKey()));

        playerWW1.sendMessageWithKey(Prefix.GREEN , "werewolf.roles.guardian_angel.get_regeneration");
        playerWW.sendMessageWithKey(
                Prefix.GREEN , "werewolf.roles.guardian_angel.perform",
                Formatter.number(3 - ((ILimitedUse) guardianAngel).getUse()));
    }
}
