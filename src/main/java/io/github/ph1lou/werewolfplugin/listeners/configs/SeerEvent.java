package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.ChestEvent;
import io.github.ph1lou.werewolfapi.events.StopEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SeerEvent extends ListenerManager {


    private final Map<Location, Boolean> chestHasBeenOpen = new HashMap<>();
    private final List<Location> chestLocation = new ArrayList<>();

    public SeerEvent(Main main) {
        super(main);
    }

    private void createTarget(Location location, Boolean active) {

        WereWolfAPI game = main.getWereWolfAPI();
        Location location2 = location.clone();
        location2.setY(location2.getY() + 1);
        List<PlayerWW> danger = new ArrayList<>();
        Block block1 = location.getBlock();
        Block block2 = location2.getBlock();

        block1.setType(UniversalMaterial.CHEST.getType());
        block2.setType(UniversalMaterial.WALL_SIGN.getType());

        Chest chest = (Chest) block1.getState();
        Sign sign = (Sign) block2.getState();

        for (PlayerWW plg : game.getPlayersWW().values()) {
            Roles role = plg.getRole();
            if (plg.isState(StatePlayer.ALIVE)) {
                if (role.isWereWolf() || role.isWereWolf()) {
                    danger.add(plg);
                }
            }
        }

        if (active && !danger.isEmpty()) {
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
            PlayerWW plg = danger.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat() * danger.size()));
            sign.setLine(1, plg.getName());
        } else {
            chest.getInventory().addItem(new ItemStack(Material.BONE, 8));
            sign.setLine(1, game.translate("werewolf.event.on_sign"));
        }
        sign.update();
        location.getBlock().setType(chest.getType());
        location2.getBlock().setType(sign.getType());
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        chestHasBeenOpen.clear();
        chestLocation.clear();
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();
        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        if (chestHasBeenOpen.isEmpty()) return;

        if (Bukkit.getPlayer(event.getPlayerUUID()) == null) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        for (Location location : chestLocation) {
            if (!chestHasBeenOpen.get(location)) {
                stringBuilder.append("§a");
            } else {
                stringBuilder.append("§6");
            }
            stringBuilder.append(" ").append(game.getScore().updateArrow(player,
                    location));
        }
        event.setActionBar(stringBuilder.toString());
    }

    @EventHandler
    public void onSeerEvent(ChestEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();
        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();
        int nb_target = game.getScore().getPlayerSize() / 3;
        if (nb_target < 2) {
            nb_target = 2;
        }
        for (int i = 0; i < nb_target; i++) {

            double a = Math.random() * 2 * Math.PI;
            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getBlockZ()));
            Location location = new Location(world, x, world.getHighestBlockYAt(x, z), z);

            createTarget(location, i == 0);

            chestLocation.add(location);
            chestHasBeenOpen.put(location, false);
        }

        Bukkit.broadcastMessage(game.translate("werewolf.event.seer_death", nb_target));
    }

    @EventHandler
    private void catchChestOpen(InventoryOpenEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        PlayerWW playerWW = game.getPlayerWW(player.getUniqueId());

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
        Bukkit.broadcastMessage(game.translate("werewolf.event.all_chest_find"));
    }
}
