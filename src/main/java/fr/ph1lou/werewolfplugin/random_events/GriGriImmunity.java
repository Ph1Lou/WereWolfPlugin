package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.buildings.IBuildingsManager;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteResultEvent;
import fr.ph1lou.werewolfapi.events.random_events.GriGriSpawnEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RandomEvent(key = EventBase.GRI_GRI_IMMUNITY,
        loreKey = "werewolf.random_events.gri_gri_immunity.description",
        timers = @Timer(key = TimerBase.GRI_GRI_SPAWN,
                defaultValue = 20 * 60,
                meetUpValue = 5 * 60,
                decrement = true,
                onZero = GriGriSpawnEvent.class))
public class GriGriImmunity extends ListenerWerewolf {

    private boolean placed = false;

    public GriGriImmunity(WereWolfAPI main) {
        super(main);
    }

    private ItemStack createGriGri() {
        WereWolfAPI game = this.getGame();
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(game.translate("werewolf.random_events.gri_gri_immunity.item_name"));
            meta.setLore(Arrays.asList(game.translate("werewolf.random_events.gri_gri_immunity.item_lore")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isGriGri(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        return meta.getDisplayName().equals(getGame().translate("werewolf.random_events.gri_gri_immunity.item_name"));
    }

    private boolean playerHasGriGri(IPlayerWW playerWW) {
        Player player = Bukkit.getPlayer(playerWW.getUUID());
        if (player == null) return false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isGriGri(item)) return true;
        }
        return false;
    }

    private void consumeGriGri(IPlayerWW playerWW) {
        Player player = Bukkit.getPlayer(playerWW.getUUID());
        if (player == null) return;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (isGriGri(player.getInventory().getItem(i))) {
                player.getInventory().setItem(i, null);
                return;
            }
        }
    }

    @EventHandler
    public void onGriGriSpawn(GriGriSpawnEvent event) {
        if (!isRegister()) return;
        if (placed) return;

        WereWolfAPI game = this.getGame();
        if (!(game instanceof GameManager)) return;

        IBuildingsManager bm = game.getBuildingsManager();
        List<? extends IVoteOffice> offices = bm.getVoteOffices();
        if (offices.isEmpty()) return;

        IVoteOffice office = offices.get(game.getRandom().nextInt(offices.size()));
        Location chestLoc = office.getChestLocation();
        if (chestLoc == null) return;

        org.bukkit.block.Block chestBlock = chestLoc.getBlock();
        if (chestBlock.getType() != Material.CHEST) {
            chestBlock.setType(Material.CHEST);
        }
        BlockState state = chestBlock.getState();
        if (!(state instanceof Chest)) return;

        Chest chest = (Chest) state;
        chest.getInventory().addItem(createGriGri());
        placed = true;

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.random_events.gri_gri_immunity.appeared"));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUrnVoteResult(UrnVoteResultEvent event) {
        if (!isRegister()) return;
        if (!placed) return;

        IPlayerWW winner = event.getPlayerWW();
        if (!playerHasGriGri(winner)) return;

        consumeGriGri(winner);

        IPlayerWW nextWinner = null;
        int nextMax = 0;
        for (Map.Entry<IPlayerWW, Integer> e : event.getTotals().entrySet()) {
            if (e.getKey().equals(winner)) continue;
            if (e.getValue() > nextMax) {
                nextMax = e.getValue();
                nextWinner = e.getKey();
            }
        }

        if (nextWinner != null) {
            Bukkit.broadcastMessage(getGame().translate(Prefix.YELLOW,
                    "werewolf.random_events.gri_gri_immunity.consumed",
                    Formatter.player(winner.getName())));
            event.setPlayerWW(nextWinner);
            event.setVotes(nextMax);
            return;
        }

        event.setCancelled(true);
    }
}
