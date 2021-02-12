package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ComedianMask;
import io.github.ph1lou.werewolfapi.events.UseMaskEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Comedian;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandComedian implements Commands {


    private final Main main;

    public CommandComedian(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles comedian = playerWW.getRole();


        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                playerWW.sendMessageWithKey("werewolf.role.comedian.mask_unknown");
                return;
            }

            if (((Comedian) comedian).getMasks()
                    .contains(ComedianMask.values()[i])) {

                playerWW.sendMessageWithKey("werewolf.role.comedian.used_mask");
                return;
            }
            ((Power) comedian).setPower(false);
            ((Comedian) comedian).addMask(ComedianMask.values()[i]);

            UseMaskEvent useMaskEvent = new UseMaskEvent(playerWW, i);
            Bukkit.getPluginManager().callEvent(useMaskEvent);

            if (useMaskEvent.isCancelled()) {
                playerWW.sendMessageWithKey("werewolf.check.cancel");
                return;
            }

            playerWW.sendMessageWithKey(
                    "werewolf.role.comedian.wear_mask_perform",
                    game.translate(ComedianMask.values()[i].getKey()));
            playerWW.addPotionEffect(ComedianMask.values()[i].getPotionEffectType());

        } catch (NumberFormatException ignored) {
        }
    }

}
