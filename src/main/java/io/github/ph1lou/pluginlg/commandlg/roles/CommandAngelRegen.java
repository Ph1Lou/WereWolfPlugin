package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandAngelRegen extends Commands {


    public CommandAngelRegen(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;
        Player player = (Player) sender;
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

        if (!plg.isRole(RoleLG.ANGE_GARDIEN)) {
            player.sendMessage(String.format(text.getText(189), text.translateRole.get(RoleLG.ANGE_GARDIEN)));
            return;
        }

        if (!plg.isState(State.LIVING)) {
            player.sendMessage(text.getText(97));
            return;
        }

        if (plg.getUse() > 0) {
            player.sendMessage(text.getText(103));
            return;
        }

        if (plg.getAffectedPlayer().isEmpty()) {
            player.sendMessage(text.getText(59));
            return;
        }

        if (Bukkit.getPlayer(plg.getAffectedPlayer().get(0)) == null) {
            player.sendMessage(text.getText(55));
            return;
        }

        plg.setUse(1);

        Player playerProtected = Bukkit.getPlayer(plg.getAffectedPlayer().get(0));
        playerProtected.removePotionEffect(PotionEffectType.REGENERATION);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 0, false, false));
        playerProtected.sendMessage(text.getText(60));

    }
}
