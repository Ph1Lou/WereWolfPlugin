package io.github.ph1lou.pluginlg.listener.gamelisteners;

import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorDetectionListener implements Listener {

    final GameManager game;

    public ArmorDetectionListener(GameManager game) {
        this.game = game;
    }


    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        if(!event.getPlayer().getWorld().equals(game.getWorld())) return;
        Player player = (Player) event.getPlayer();
        String playerName = player.getName();
        Inventory inventory = player.getInventory();
        if (!game.isState(StateLG.LG)) return;
        if (!game.isDay(Day.NIGHT)) return;
        if (!game.playerLG.containsKey(playerName) || (!game.playerLG.get(playerName).isRole(RoleLG.PETITE_FILLE) && !game.playerLG.get(playerName).isRole(RoleLG.LOUP_PERFIDE)))
            return;

        if (inventory.getItem(36) == null && inventory.getItem(37) == null && inventory.getItem(38) == null && inventory.getItem(39) == null) {
            if (game.playerLG.get(playerName).hasPower()) {
                player.sendMessage(game.text.getText(129));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                if (game.playerLG.get(playerName).isCamp(Camp.LG)) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                game.playerLG.get(playerName).setPower(false);
                game.optionlg.updateNameTag();
            }
        } else if (!game.playerLG.get(playerName).hasPower()) {
            player.sendMessage(game.text.getText(18));
            if (game.playerLG.get(playerName).isCamp(Camp.LG)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            game.playerLG.get(playerName).setPower(true);
            game.optionlg.updateNameTag();
        }
    }
}
