package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandComedian extends Commands {


    public CommandComedian(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game=null;
        Player player =(Player) sender;

        for(GameManager gameManager:main.listGames.values()){
            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        TextLG text = game.text;
        String playername = player.getName();

        if(!game.playerLG.containsKey(playername)) {
            player.sendMessage(text.getText(67));
            return;
        }

        PlayerLG plg = game.playerLG.get(playername);


        if (!game.isState(StateLG.LG)) {
            player.sendMessage(text.getText(68));
            return;
        }

        if (!plg.isRole(RoleLG.COMEDIEN)) {
            player.sendMessage(String.format(text.getText(189), text.translateRole.get(RoleLG.COMEDIEN)));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(String.format(text.getText(190), 1));
            return;
        }

        if (!plg.isState(State.LIVING)) {
            player.sendMessage(text.getText(97));
            return;
        }

        if (!plg.hasPower()) {
            player.sendMessage(text.getText(103));
            return;
        }
        PotionEffectType[] potionsType = {PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE};
        String[] maskName = {text.getText(47), text.getText(48), text.getText(49)};
        try {
            int i = Integer.parseInt(args[0]) - 1;
            if (i < 0 || i > 2) {
                player.sendMessage(text.getText(37));
                return;
            }

            if (plg.getPotionEffects().contains(potionsType[i])) {
                player.sendMessage(text.getText(35));
                return;
            }
            plg.setPower(false);
            plg.addPotionEffect(potionsType[i]);
            player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.COMEDIEN), maskName[i]));
            player.removePotionEffect(potionsType[i]);
            player.addPotionEffect(new PotionEffect(potionsType[i], Integer.MAX_VALUE, i == 2 ? -1 : 0, false, false));

        } catch (NumberFormatException ignored) {
        }
    }

}
