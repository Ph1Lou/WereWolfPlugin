package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandComedian extends Commands {

    final MainLG main;

    public CommandComedian(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;
        String playername = player.getName();

        if (!main.playerLG.containsKey(playername)) {
            player.sendMessage(main.text.getText(67));
            return;
        }

        PlayerLG plg = main.playerLG.get(playername);

        if (!main.isState(StateLG.LG)) {
            player.sendMessage(main.text.getText(68));
            return;
        }

        if (!plg.isRole(RoleLG.COMEDIEN)) {
            player.sendMessage(String.format(main.text.getText(189), main.text.translateRole.get(RoleLG.COMEDIEN)));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(String.format(main.text.getText(190), 1));
            return;
        }

        if (!plg.isState(State.LIVING)) {
            player.sendMessage(main.text.getText(97));
            return;
        }

        if (!plg.hasPower()) {
            player.sendMessage(main.text.getText(103));
            return;
        }
        PotionEffectType[] potionsType = {PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE};
        String[] maskName = {main.text.getText(47), main.text.getText(48), main.text.getText(49)};
        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                player.sendMessage(main.text.getText(37));
                return;
            }

            if (plg.getPotionEffects().contains(potionsType[i])) {
                player.sendMessage(main.text.getText(35));
                return;
            }
            plg.setPower(false);
            plg.addPotionEffect(potionsType[i]);
            player.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.COMEDIEN), maskName[i]));
            player.removePotionEffect(potionsType[i]);
            player.addPotionEffect(new PotionEffect(potionsType[i], Integer.MAX_VALUE, i == 2 ? -1 : 0, false, false));

        } catch (NumberFormatException ignored) {
        }
    }

}
