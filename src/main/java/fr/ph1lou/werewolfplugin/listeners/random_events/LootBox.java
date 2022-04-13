package fr.ph1lou.werewolfplugin.listeners.random_events;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.random_events.FindAllLootBoxEvent;
import fr.ph1lou.werewolfapi.events.random_events.LootBoxEvent;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LootBox extends ListenerManager {


    private final Map<Location, Boolean> chestHasBeenOpen = new HashMap<>();
    private final List<Location> chestLocation = new ArrayList<>();
    private boolean eventActive = false;

    public LootBox(Main main) {
        super(main);
    }

    @EventHandler
    public void onVillagerDeath(FinalDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (eventActive) return;

        if (!event.getPlayerWW().getRole().isCamp(Camp.VILLAGER)) return;

        if (game.getPlayersCount() > 16) return;

        if (game.getRandom().nextFloat() * 5 > 3) return;

        launchEvent(event.getPlayerWW().getName());

    }

    private void createTarget(Location location, Boolean active, String name) {

        WereWolfAPI game = this.getGame();
        Location location2 = location.clone();
        location2.setY(location2.getY() + 1);

        Block block1 = location.getBlock();
        Block block2 = location2.getBlock();

        block1.setType(UniversalMaterial.CHEST.getType());
        block2.setType(UniversalMaterial.WALL_SIGN.getType());

        Chest chest = (Chest) block1.getState();
        Sign sign = (Sign) block2.getState();

        if (active) {
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
            sign.setLine(1, name);
        } else {
            chest.getInventory().addItem(new ItemStack(Material.BONE, 8));
            sign.setLine(1, game.translate("werewolf.random_events.loot_box.on_sign"));
        }
        sign.update();
        location.getBlock().setType(chest.getType());
        location2.getBlock().setType(sign.getType());
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        chestHasBeenOpen.clear();
        chestLocation.clear();
        eventActive = false;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        chestHasBeenOpen.clear();
        chestLocation.clear();
        eventActive = false;
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        if (chestHasBeenOpen.isEmpty()) return;

        if (Bukkit.getPlayer(event.getPlayerUUID()) == null) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        for (Location location : chestLocation) {
            if (!chestHasBeenOpen.get(location)) {
                stringBuilder.append("§a");
            } else {
                stringBuilder.append("§6");
            }
            stringBuilder.append(" ").append(Utils.updateArrow(player,
                    location));
        }
        event.setActionBar(stringBuilder.toString());
    }


    public void launchEvent(String deathName) {

        WereWolfAPI game = this.getGame();
        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW1.getRole().isCamp(Camp.VILLAGER))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        IPlayerWW playerWW = playerWWS.get((int) Math.floor(
                game.getRandom().nextFloat() * playerWWS.size()));

        int nbTarget = game.getPlayersCount() / 3;
        if (nbTarget < 2) {
            nbTarget = 2;
        }

        LootBoxEvent lootBoxEvent = new LootBoxEvent(playerWW, nbTarget);

        Bukkit.getPluginManager().callEvent(lootBoxEvent);

        if (lootBoxEvent.isCancelled()) return;

        eventActive = true;

        for (int i = 0; i < nbTarget; i++) {

            double a = Math.random() * 2 * Math.PI;
            int x = (int) (Math.round(wb.getSize() / 3 *
                    Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 *
                    Math.sin(a) + world.getSpawnLocation().getBlockZ()));
            Location location = new Location(world, x, world.getHighestBlockYAt(x, z), z);

            createTarget(location, i == 0, playerWW.getName());

            chestLocation.add(location);
            chestHasBeenOpen.put(location, false);
        }

        Bukkit.broadcastMessage(game.translate("werewolf.random_events.loot_box.villager_death",
                Formatter.player(deathName),
                Formatter.number(nbTarget)));
    }

    @EventHandler
    private void catchChestOpen(InventoryOpenEvent event) {

        WereWolfAPI game = this.getGame();

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (!event.getInventory().getType().equals(InventoryType.CHEST)) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof Chest)) {
            return;
        }

        Location location = ((Chest) event.getInventory().getHolder()).getLocation();

        if (!chestLocation.contains(location)) {
            return;
        }

        chestHasBeenOpen.put(location, true);

        if (chestHasBeenOpen.containsValue(false)) {
            return;
        }

        chestLocation.clear();
        chestHasBeenOpen.clear();
        register(false);
        Bukkit.broadcastMessage(game.translate(Prefix.GREEN.getKey() , "werewolf.random_events.loot_box.all_chest_find"));

        Bukkit.getPluginManager().callEvent(new FindAllLootBoxEvent());
    }
}
