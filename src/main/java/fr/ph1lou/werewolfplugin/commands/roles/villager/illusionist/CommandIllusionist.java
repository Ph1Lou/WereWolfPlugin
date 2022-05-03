package fr.ph1lou.werewolfplugin.commands.roles.villager.illusionist;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistActivatePowerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.villagers.Illusionist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandIllusionist implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        Illusionist illusionist = (Illusionist) playerWW.getRole();

        illusionist.setPower(false);

        IllusionistActivatePowerEvent event = new IllusionistActivatePowerEvent(playerWW);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        illusionist.setWait(true);
        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey(), "werewolf.role.illusionist.perform");
    }
}
