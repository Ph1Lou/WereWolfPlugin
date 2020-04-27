package io.github.ph1lou.pluginlg.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlg.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerListener implements Listener {

    final MainLG main;

    public ServerListener(MainLG main) {
        this.main = main;
    }


    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/rl") || args[0].equalsIgnoreCase("/stop") || args[0].equalsIgnoreCase("/bukkit:rl") || args[0].equalsIgnoreCase("/bukkit:reload")) {
            event.setCancelled(true);
            player.sendMessage(main.defaultLanguage.getText(274));
        }

        if (!event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) return;

        if (args[0].equalsIgnoreCase("/tellRaw") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/minecraft:tell")) {

            event.setCancelled(true);
            if (args.length <= 2) return;
            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(main.defaultLanguage.getText(132));
                return;
            }

            Player recipient = Bukkit.getPlayer(args[1]);

            if (!recipient.hasPermission("adminLG.use") && !player.hasPermission("adminLG.use")) {
                if (!player.getWorld().equals(recipient.getWorld())) {
                    player.sendMessage(main.defaultLanguage.getText(273));
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String w : args) {
                sb.append(w).append(" ");
            }
            sb.delete(0, args[0].length() + args[1].length() + 2);
            recipient.sendMessage(String.format(main.defaultLanguage.getText(133), player.getName(), sb.toString()));
            player.sendMessage(String.format(main.defaultLanguage.getText(134), args[1], sb.toString()));
            recipient.playSound(recipient.getLocation(), Sound.ANVIL_USE, 1, 20);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        FastBoard fastboard = main.boards.remove(player.getUniqueId());
        if (fastboard != null) {
            fastboard.delete();
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(player.getWorld())) {
                player.hidePlayer(p);
                p.hidePlayer(player);
            } else {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }

        GameManager game = null;

        for (GameManager gameManager : main.listGames.values()) {
            if (gameManager.getWorld().equals(player.getWorld())) {
                game = gameManager;
                break;
            }
        }

        if (game != null) {
            return;
        }
        new UpdateChecker(main, 73113).getVersion(version -> {

            if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                player.sendMessage(main.defaultLanguage.getText(2));
            } else {
                player.sendMessage(main.defaultLanguage.getText(185));
            }});

        FastBoard fastboard = new FastBoard(player);
        fastboard.updateTitle(main.defaultLanguage.getText(125));
        main.boards.put(player.getUniqueId(), fastboard);
        Title.sendTabTitle(player, main.defaultLanguage.getText(125), main.defaultLanguage.getText(184));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);

        if (main.listGames.isEmpty()) {
            return;
        }

        for (GameManager gameManager : main.listGames.values()) {
            if (gameManager.isState(StateLG.LOBBY)) {
                if (!gameManager.isWhiteList() || gameManager.getWhiteListedPlayers().contains(player.getName())) {
                    gameManager.sendMessage(player);
                }
            }
        }
    }


    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)) {
            if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(player.getWorld())) {
                player.hidePlayer(p);
                p.hidePlayer(player);
            } else {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {

        Set<Player> recipients = new HashSet<>(event.getRecipients());

        for (Player p2 : recipients) {
            if (!event.getPlayer().getWorld().equals(p2.getWorld())) {
                event.getRecipients().remove(p2);
            }
        }
    }

    @EventHandler
    private void onSousMenu(InventoryClickEvent event) {

        if (!event.getWhoClicked().getWorld().equals(Bukkit.getWorlds().get(0))) return;

        InventoryView view = event.getView();
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();

        if (current != null) {

            if (view.getTitle().equalsIgnoreCase(main.defaultLanguage.getText(308))) {

                event.setCancelled(true);
                List<Integer> slotHost = Arrays.asList(21, 22, 23, 30, 31, 32);
                if (current.getType() == Material.EMERALD_BLOCK || current.getType() == Material.REDSTONE_BLOCK) {
                    player.performCommand("lg join " + ((GameManager) main.listGames.values().toArray()[slotHost.indexOf(event.getSlot())]).getGameUUID());
                }

            }
        }
    }


}