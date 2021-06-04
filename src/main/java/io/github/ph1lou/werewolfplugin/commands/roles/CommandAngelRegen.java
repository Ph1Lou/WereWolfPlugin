package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
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

        IRole guardianAngel = playerWW.getRole();


        if (((ILimitedUse) guardianAngel).getUse() >= 3) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        if (((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey("werewolf.role.guardian_angel.no_protege");
            return;
        }

        IPlayerWW playerWW1 = ((IAffectedPlayers) guardianAngel).getAffectedPlayers().get(0);

        Player playerProtected = Bukkit.getPlayer(playerWW1.getUUID());

        if (playerProtected == null) {
            playerWW.sendMessageWithKey("werewolf.role.guardian_angel.disconnected_protege");
            return;
        }


        ((ILimitedUse) guardianAngel).setUse(((ILimitedUse) guardianAngel).getUse() + 1);

        RegenerationEvent event = new RegenerationEvent(playerWW, ((IAffectedPlayers) guardianAngel)
                .getAffectedPlayers().get(0));

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        playerWW1.addPotionModifier(PotionModifier.add( PotionEffectType.REGENERATION,
                400,
                0,
                "angel_regen"));

        playerWW1.sendMessageWithKey("werewolf.role.guardian_angel.get_regeneration");
        playerWW.sendMessageWithKey(
                "werewolf.role.guardian_angel.perform",
                3 - ((ILimitedUse) guardianAngel).getUse());
    }
}
