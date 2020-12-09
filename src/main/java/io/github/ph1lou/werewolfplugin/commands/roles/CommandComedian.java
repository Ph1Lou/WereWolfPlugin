package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UseMaskEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.PotionEffects;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        PotionEffectType[] potionsType = {
                PotionEffectType.DAMAGE_RESISTANCE,
                PotionEffectType.SPEED,
                PotionEffectType.INCREASE_DAMAGE
        };

        String[] maskName = {
                game.translate("werewolf.role.comedian.1"),
                game.translate("werewolf.role.comedian.2"),
                game.translate("werewolf.role.comedian.3")
        };

        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                player.sendMessage(game.translate(
                        "werewolf.role.comedian.mask_unknown"));
                return;
            }

            if (((PotionEffects) comedian).getPotionEffects()
                    .contains(potionsType[i])) {

                player.sendMessage(game.translate(
                        "werewolf.role.comedian.used_mask"));
                return;
            }
            ((Power) comedian).setPower(false);
            ((PotionEffects) comedian).addPotionEffect(potionsType[i]);

            UseMaskEvent useMaskEvent = new UseMaskEvent(playerWW, i);
            Bukkit.getPluginManager().callEvent(useMaskEvent);

            if (useMaskEvent.isCancelled()) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }

            player.sendMessage(game.translate(
                    "werewolf.role.comedian.wear_mask_perform",
                    maskName[i]));
            player.removePotionEffect(potionsType[i]);
            player.addPotionEffect(new PotionEffect(
                    potionsType[i],
                    Integer.MAX_VALUE,
                    i == 2 ? -1 : 0,
                    false,
                    false));

        } catch (NumberFormatException ignored) {
        }
    }

}
