package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandAngelRegen extends Commands {

    final MainLG main;

    public CommandAngelRegen(MainLG main) {
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

        if (!plg.isRole(RoleLG.ANGE_GARDIEN)) {
            player.sendMessage(String.format(main.text.getText(189), main.text.translateRole.get(RoleLG.ANGE_GARDIEN)));
            return;
        }

        if (!plg.isState(State.LIVING)) {
            player.sendMessage(main.text.getText(97));
            return;
        }

        if (plg.getUse() > 0) {
            player.sendMessage(main.text.getText(103));
            return;
        }

        if (plg.getAffectedPlayer().isEmpty()) {
            player.sendMessage(main.text.getText(59));
            return;
        }

        if (Bukkit.getPlayer(plg.getAffectedPlayer().get(0)) == null) {
            player.sendMessage(main.text.getText(55));
            return;
        }

        plg.setUse(1);

        Player playerProtected = Bukkit.getPlayer(plg.getAffectedPlayer().get(0));
        playerProtected.removePotionEffect(PotionEffectType.REGENERATION);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0, false, false));
        playerProtected.sendMessage(main.text.getText(60));

    }
}
