package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.classesroles.InvisibleState;
import io.github.ph1lou.pluginlg.classesroles.villageroles.LittleGirl;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.MischievousWereWolf;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.Day;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class ArmorDetectionListener implements Listener {

    final GameManager game;

    public ArmorDetectionListener(GameManager game) {
        this.game = game;
    }


    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inventory = player.getInventory();
        if (!game.isState(StateLG.GAME)) return;
        if (!game.isDay(Day.NIGHT)) return;
        if(!game.playerLG.containsKey(uuid)){
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);

        if (!(plg.getRole() instanceof LittleGirl) && !(plg.getRole() instanceof MischievousWereWolf)){
            return;
        }

        InvisibleState powerRole= (InvisibleState) plg.getRole();

        if (inventory.getItem(36) == null && inventory.getItem(37) == null && inventory.getItem(38) == null && inventory.getItem(39) == null) {
            if (!powerRole.isInvisible()) {
                player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor_perform"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                if (plg.getRole().isCamp(Camp.WEREWOLF)) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                powerRole.setInvisible(true);
                game.optionlg.updateNameTag();
            }
        } else if (powerRole.isInvisible()) {
            player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
            if (plg.getRole().isCamp(Camp.WEREWOLF)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            powerRole.setInvisible(false);
            game.optionlg.updateNameTag();
        }
    }
}
