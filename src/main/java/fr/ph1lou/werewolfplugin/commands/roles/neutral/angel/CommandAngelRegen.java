package fr.ph1lou.werewolfplugin.commands.roles.neutral.angel;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandAngelRegen implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Angel guardianAngel = (Angel) playerWW.getRole();

        if (!guardianAngel.isChoice(AngelForm.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.state_player"));
            return;
        }


        if (((ILimitedUse) guardianAngel).getUse() >= 3) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        if (((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.guardian_angel.no_protege");
            return;
        }

        IPlayerWW playerWW1 = ((IAffectedPlayers) guardianAngel).getAffectedPlayers().get(0);

        Player playerProtected = Bukkit.getPlayer(playerWW1.getUUID());

        if (playerProtected == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.guardian_angel.disconnected_protege");
            return;
        }


        ((ILimitedUse) guardianAngel).setUse(((ILimitedUse) guardianAngel).getUse() + 1);

        RegenerationEvent event = new RegenerationEvent(playerWW, ((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().get(0));

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        playerWW1.addPotionModifier(PotionModifier.add( PotionEffectType.REGENERATION,
                400,
                0,
                "angel_regen"));

        playerWW1.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.role.guardian_angel.get_regeneration");
        playerWW.sendMessageWithKey(
                Prefix.GREEN.getKey() , "werewolf.role.guardian_angel.perform",
                Formatter.number(3 - ((ILimitedUse) guardianAngel).getUse()));
    }
}
