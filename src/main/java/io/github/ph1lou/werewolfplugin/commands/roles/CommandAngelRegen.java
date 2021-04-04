package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandAngelRegen implements ICommands {

    private final Main main;

    public CommandAngelRegen(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

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

        playerProtected.removePotionEffect(PotionEffectType.REGENERATION);
        playerProtected.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                400,
                0,
                false,
                false));

        playerWW1.sendMessageWithKey("werewolf.role.guardian_angel.get_regeneration");
        playerWW.sendMessageWithKey(
                "werewolf.role.guardian_angel.perform",
                3 - ((ILimitedUse) guardianAngel).getUse());
    }
}
