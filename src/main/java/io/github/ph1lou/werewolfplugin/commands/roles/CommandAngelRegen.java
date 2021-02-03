package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.RegenerationEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandAngelRegen implements Commands {

    private final Main main;

    public CommandAngelRegen(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles guardianAngel = playerWW.getRole();


        if (((LimitedUse) guardianAngel).getUse() >= 3) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        if (((AffectedPlayers) guardianAngel)
                .getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey("werewolf.role.guardian_angel.no_protege");
            return;
        }

        PlayerWW playerWW1 = ((AffectedPlayers) guardianAngel).getAffectedPlayers().get(0);

        Player playerProtected = Bukkit.getPlayer(playerWW1.getUUID());

        if (playerProtected == null) {
            playerWW.sendMessageWithKey("werewolf.role.guardian_angel.disconnected_protege");
            return;
        }


        ((LimitedUse) guardianAngel).setUse(((LimitedUse) guardianAngel).getUse() + 1);

        RegenerationEvent event = new RegenerationEvent(playerWW, ((AffectedPlayers) guardianAngel)
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
                3 - ((LimitedUse) guardianAngel).getUse());
    }
}
