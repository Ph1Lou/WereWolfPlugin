package fr.ph1lou.werewolfplugin.commands.roles.villager.protector;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.protector.ProtectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.protector.command",
        roleKeys = RoleBase.PROTECTOR,
        requiredPower = true,
        argNumbers = 1)
public class CommandProtector implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole protector = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) protector).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.already_get_power");
            return;
        }


        ((IPower) protector).setPower(false);

        ProtectionEvent protectionEvent = new ProtectionEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(protectionEvent);

        if (protectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) protector).clearAffectedPlayer();
        ((IAffectedPlayers) protector).addAffectedPlayer(playerWW1);

        playerWW1.addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,playerWW.getRole().getKey()));
        playerWW1.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(), Aura.LIGHT, 40, true));
        playerWW1.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.protector.get_protection");
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.protector.protection_perform",
                Formatter.player(playerArg.getName()));
    }
}
