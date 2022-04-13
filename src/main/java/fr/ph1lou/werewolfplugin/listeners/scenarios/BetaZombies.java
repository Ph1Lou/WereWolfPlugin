package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BetaZombies extends ListenerManager {

    public BetaZombies(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {


        List<ItemStack> loots = event.getDrops();

        for (int i = loots.size() - 1; i >= 0; --i) {
            ItemStack is = loots.get(i);
            if (is == null) {
                return;
            }

            if(is.getType() == Material.ROTTEN_FLESH){
                loots.remove(i);
                loots.add(new ItemStack(UniversalMaterial.FEATHER.getStack()));
            }
        }
    }
}
