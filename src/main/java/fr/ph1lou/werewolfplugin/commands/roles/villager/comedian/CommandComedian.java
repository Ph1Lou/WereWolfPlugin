package fr.ph1lou.werewolfplugin.commands.roles.villager.comedian;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.ComedianMask;
import fr.ph1lou.werewolfapi.events.roles.comedian.UseMaskEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Comedian;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.comedian.command",
        roleKeys = RoleBase.COMEDIAN,
        requiredPower = true,
        argNumbers = 1)
public class CommandComedian implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole comedian = playerWW.getRole();


        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.comedian.mask_unknown");
                return;
            }

            if (((Comedian) comedian).getMasks()
                    .contains(ComedianMask.values()[i])) {

                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.comedian.used_mask");
                return;
            }
            ((IPower) comedian).setPower(false);
            ((Comedian) comedian).addMask(ComedianMask.values()[i]);

            UseMaskEvent useMaskEvent = new UseMaskEvent(playerWW, i);
            Bukkit.getPluginManager().callEvent(useMaskEvent);

            if (useMaskEvent.isCancelled()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                return;
            }

            playerWW.sendMessageWithKey(
                    Prefix.YELLOW, "werewolf.roles.comedian.wear_mask_perform",
                    Formatter.format("&mask&", game.translate(ComedianMask.values()[i].getKey())));
            playerWW.addPotionModifier(PotionModifier.add(ComedianMask.values()[i].getPotionEffectType(), playerWW.getRole().getKey()));

        } catch (NumberFormatException ignored) {
        }
    }

}
