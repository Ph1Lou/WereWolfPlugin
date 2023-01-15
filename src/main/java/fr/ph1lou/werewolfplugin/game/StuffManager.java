package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.game.IStuffManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class StuffManager implements IStuffManager {

    private final Map<String, List<ItemStack>> stuffRoles = new HashMap<>();
    private final Map<UUID, ItemStack[]> tempStuff = new HashMap<>();
    private final List<ItemStack> deathLoot = new ArrayList<>();
    private final List<ItemStack> startLoot = new ArrayList<>();

    @Override
    public List<? extends ItemStack> getDeathLoot() {
        return this.deathLoot;
    }

    @Override
    public List<? extends ItemStack> getStartLoot() {
        return this.startLoot;
    }

    @Override
    public void clearDeathLoot() {
        deathLoot.clear();
    }

    @Override
    public void clearStartLoot() {
        startLoot.clear();
    }

    @Override
    public void clearStuffRoles() {
        this.stuffRoles.clear();
    }

    @Override
    public void clearTempStuff() {
        this.tempStuff.clear();
    }

    @Override
    public void addDeathLoot(ItemStack i) {
        deathLoot.add(i);
    }

    @Override
    public void addStartLoot(ItemStack itemStack) {
        this.startLoot.add(itemStack);
    }

    @Override
    public List<? extends ItemStack> getStuffRole(String key) {
        return this.stuffRoles.getOrDefault(key, Collections.emptyList());
    }

    @Override
    public void setStuffRole(String key, List<ItemStack> list) {
        this.stuffRoles.put(key, list);
    }

    @Override
    public ItemStack[] recoverTempStuff(UUID uuid) {
        ItemStack[] temp = this.tempStuff.remove(uuid);

        if (temp != null) {
            return temp;
        }

        return new ItemStack[40];
    }

    @Override
    public void putTempStuff(UUID player, ItemStack[] list) {
        this.tempStuff.put(player, list);
    }

    @Override
    public boolean isInTempStuff(UUID uuid) {
        return this.tempStuff.containsKey(uuid);
    }

}
