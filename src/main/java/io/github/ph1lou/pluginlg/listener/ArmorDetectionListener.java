package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorDetectionListener implements Listener {

    final MainLG main;

    public ArmorDetectionListener(MainLG main) {
        this.main = main;
    }


    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        String playerName = player.getName();
        Inventory inventory = player.getInventory();
        if (!main.isState(StateLG.LG)) return;
        if (!main.isDay(Day.NIGHT)) return;
        if (!main.playerLG.containsKey(playerName) || (!main.playerLG.get(playerName).isRole(RoleLG.PETITE_FILLE) && !main.playerLG.get(playerName).isRole(RoleLG.LOUP_PERFIDE)))
            return;

        if (inventory.getItem(36) == null && inventory.getItem(37) == null && inventory.getItem(38) == null && inventory.getItem(39) == null) {
            if (main.playerLG.get(playerName).hasPower()) {
                player.sendMessage(main.text.getText(129));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                if (main.playerLG.get(playerName).isCamp(Camp.LG)) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                main.playerLG.get(playerName).setPower(false);
                main.optionlg.updateNameTag();
            }
        } else if (!main.playerLG.get(playerName).hasPower()) {
            player.sendMessage(main.text.getText(18));
            if (main.playerLG.get(playerName).isCamp(Camp.LG)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            main.playerLG.get(playerName).setPower(true);
            main.optionlg.updateNameTag();
        }
    }
}
