package fr.ph1lou.werewolfplugin.commands.roles.hybrid.wolfdog;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.roles.wolf_dog.WolfDogChooseWereWolfForm;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.wolf_dog.command",
        roleKeys = RoleBase.WOLF_DOG,
        argNumbers = 0,
        requiredPower = true)
public class CommandWolfDog implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole wolfDog = playerWW.getRole();
        ((IPower) wolfDog).setPower(false);
        WolfDogChooseWereWolfForm event = new WolfDogChooseWereWolfForm(playerWW);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((ITransformed) wolfDog).setTransformed(true);

        playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.wolf_dog.perform");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        game.checkVictory();
    }
}
