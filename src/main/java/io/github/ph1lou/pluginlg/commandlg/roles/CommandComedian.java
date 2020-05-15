package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Comedian;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandComedian extends Commands {


    public CommandComedian(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();


        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }


        if (!(plg.getRole() instanceof Comedian)) {
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.comedian.display")));
            return;
        }

        Comedian comedian = (Comedian) plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.parameters",1));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (!comedian.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }
        PotionEffectType[] potionsType = {PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE};
        String[] maskName = {game.translate("werewolf.role.comedian.1"), game.translate("werewolf.role.comedian.2"), game.translate("werewolf.role.comedian.3")};
        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                player.sendMessage(game.translate("werewolf.role.comedian.mask_unknown"));
                return;
            }

            if (comedian.getPotionEffects().contains(potionsType[i])) {
                player.sendMessage(game.translate("werewolf.role.comedian.used_mask"));
                return;
            }
            comedian.setPower(false);
            comedian.addPotionEffect(potionsType[i]);
            player.sendMessage(game.translate("werewolf.role.comedian.wear_mask_perform", maskName[i]));
            player.removePotionEffect(potionsType[i]);
            player.addPotionEffect(new PotionEffect(potionsType[i], Integer.MAX_VALUE, i == 2 ? -1 : 0, false, false));

        } catch (NumberFormatException ignored) {
        }
    }

}
