package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.DeathItemsEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.PlayerWWKillEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.PlayerWW;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeathListener implements Listener {

    public static final String KILLER = "killer";
    private static final String RESURRECTION = "resurrection";
    private final WereWolfAPI game;

    public DeathListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> player.spigot().respawn(), 10L);
        event.setKeepInventory(true);

        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        if (game.isState(StateGame.GAME)) {

            event.setDeathMessage(null);
            event.setKeepLevel(true);

            IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

            if (playerWW == null) return;

            if (!playerWW.isState(StatePlayer.ALIVE)) return;

            playerWW.setDeathLocation(player.getLocation());
            playerWW.clearItemDeath();
            playerWW.setState(StatePlayer.JUDGEMENT);
            ((PlayerWW) playerWW).setDeathTime(game.getTimer());

            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null) {
                    items.add(item);
                }
            }
            playerWW.setItemDeath(items);

            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.announcement.potential_revive"));

            if (player.getKiller() != null) {

                Player killer = player.getKiller();
                UUID killerUUID = killer.getUniqueId();
                IPlayerWW killerWW = game.getPlayerWW(killerUUID).orElse(null);
                playerWW.addKiller(killerWW);

                Bukkit.getPluginManager().callEvent(new PlayerWWKillEvent(killerWW, playerWW));

                if (killerWW != null) {
                    killerWW.addOneKill(playerWW);
                }
            } else {
                playerWW.addKiller(null);
                Bukkit.getPluginManager().callEvent(new PlayerWWKillEvent(null, playerWW));
            }

            BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                FirstDeathEvent firstDeathEvent = new FirstDeathEvent(playerWW,
                        new HashSet<>(playerWW.getLastMinutesDamagedPlayer()));
                Bukkit.getPluginManager().callEvent(firstDeathEvent);
            }, 20L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFirstDeath(FirstDeathEvent event) {

        if (event.isCancelled()) return;

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.isState(StatePlayer.JUDGEMENT)) return;

        SecondDeathEvent secondDeathEvent = new SecondDeathEvent(playerWW, event.getLastStrikers());
        Bukkit.getPluginManager().callEvent(secondDeathEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (event.getPlayerWW().isState(StatePlayer.JUDGEMENT)) {

                ThirdDeathEvent thirdDeathEvent = new ThirdDeathEvent(event.getPlayerWW(), event.getLastStrikers());
                Bukkit.getPluginManager().callEvent(thirdDeathEvent);
            }
        }, 7 * 20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThirdDeath(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (event.getPlayerWW().isState(StatePlayer.JUDGEMENT)) {
                Bukkit.getPluginManager().callEvent(new FinalDeathEvent(event.getPlayerWW(), event.getLastStrikers()));
            }
        }, 7 * 20);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        PlayerWW playerWW = (PlayerWW) event.getPlayerWW();
        Player player = Bukkit.getPlayer(playerWW.getUUID());
        World world = game.getMapManager().getWorld();

        if (playerWW.isState(StatePlayer.ALIVE)) {

            playerWW.clearItemDeath();

            if (player != null) {
                playerWW.setDeathLocation(player.getLocation());

                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 40; i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item != null) {
                        items.add(item);
                    }
                }
                playerWW.setItemDeath(items);
            }
            playerWW.setDeathTime(game.getTimer());
        }

        if (playerWW.isState(StatePlayer.DEATH)) return;

        playerWW.setState(StatePlayer.DEATH);
        ((GameManager) game).setPlayerSize(game.getPlayersCount() - 1);

        game.getPlayersWW().forEach(playerWW1 -> {
            AnnouncementDeathEvent announcementDeathEvent = new AnnouncementDeathEvent(playerWW, playerWW1, "werewolf.announcement.death_message");
            Bukkit.getPluginManager().callEvent(announcementDeathEvent);

            if (!announcementDeathEvent.isCancelled()) {
                Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
                        new Formatter[]{Formatter.player(announcementDeathEvent.getPlayerName()),
                                Formatter.role(game.translate(announcementDeathEvent.getRole()))});
                announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
                announcementDeathEvent.getTargetPlayer().sendMessageWithKey(Prefix.RED, announcementDeathEvent.getFormat(), formatters);
                announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
                announcementDeathEvent.getTargetPlayer().sendSound(Sound.AMBIENCE_THUNDER);
            }
        });

        event
                .getPlayerWW()
                .getLastKiller()
                .ifPresent(playerWW1 -> playerWW1.getRole()
                        .addAuraModifier(new AuraModifier(KILLER, Aura.DARK, 1, false)));

        game.getModerationManager().getModerators().stream()
                .filter(uuid -> !game.getPlayerWW(uuid).isPresent())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player1 -> {
                    player1.sendMessage(this.sendOriginalDeathMessage(playerWW));
                    Sound.AMBIENCE_THUNDER.play(player1);
                });

        Bukkit.getConsoleSender().sendMessage(this.sendOriginalDeathMessage(playerWW));

        UpdateCompositionEvent updateCompositionReason = new UpdateCompositionEvent(playerWW, playerWW.getDeathRole(), UpdateCompositionReason.DEATH, -1);
        Bukkit.getPluginManager().callEvent(updateCompositionReason);

        if (!updateCompositionReason.isCancelled()) {
            game.getConfig().removeOneRole(playerWW.getDeathRole());
        }

        DeathItemsEvent deathItemsEvent = new DeathItemsEvent(playerWW,
                Stream.concat(playerWW.getItemDeath()
                                .stream(),
                        game.getStuffs().getDeathLoot()
                                .stream()).collect(Collectors.toList()),
                playerWW.getDeathLocation()
        );

        Bukkit.getPluginManager().callEvent(deathItemsEvent);

        if (!deathItemsEvent.isCancelled()) {
            deathItemsEvent.getItems().forEach(itemStack -> world.dropItem(deathItemsEvent.getLocation(), itemStack));
        }

        if (player != null) {

            player.setGameMode(GameMode.SPECTATOR);
            TextComponent msg = Utils.getDiscord(game);
            player.spigot().sendMessage(msg);
            if (game.getConfig().getSpectatorMode() == 0 &&
                    !player.isOp() &&
                    !game.getModerationManager().isStaff(player.getUniqueId())) {
                player.kickPlayer(game.translate(Prefix.RED, "werewolf.check.spectator_disabled"));
            }
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
        }
        BukkitUtils.scheduleSyncDelayedTask(game, game::checkVictory);
    }

    private String sendOriginalDeathMessage(IPlayerWW playerWW) {
        return game.translate("werewolf.utils.bar") + game.translate(Prefix.RED, "werewolf.announcement.death_message_with_role",
                Formatter.player(playerWW.getName()),
                Formatter.role(game.translate(playerWW.getRole().getKey())))
                + game.translate("werewolf.utils.bar");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onResurrection(ResurrectionEvent event) {

        if (event.isCancelled()) return;

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.isState(StatePlayer.ALIVE)) return;

        playerWW.setState(StatePlayer.ALIVE);
        playerWW.getRole().addAuraModifier(new AuraModifier(RESURRECTION, Aura.NEUTRAL, 10, false));

        Player player = Bukkit.getPlayer(playerWW.getUUID());

        if (player != null) {
            ((PlayerWW) playerWW).updatePotionEffects(player);
        }

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.announcement.resurrection");
        game.getMapManager().transportation(playerWW, Math.random() * Math.PI * 2);

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
        BukkitUtils.scheduleSyncDelayedTask(game, game::checkVictory);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW != null) {

            ((PlayerWW) playerWW).updatePotionEffects(player);

            if (game.isState(StateGame.LOBBY)) {
                BukkitUtils.scheduleSyncDelayedTask(game, () ->
                                event.getPlayer().addPotionEffect(new PotionEffect(
                                        UniversalPotionEffectType.SATURATION.getPotionEffectType(),
                                        Integer.MAX_VALUE,
                                        0,
                                        false,
                                        false)),
                        20L);
            } else if (game.isState(StateGame.START) ||
                    game.isState(StateGame.TRANSPORTATION) ||
                    (game.isState(StateGame.GAME) &&
                            game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE))) {

                event.setRespawnLocation(
                        playerWW.getSpawn());
                BukkitUtils.scheduleSyncDelayedTask(game, () -> playerWW.addPotionModifier(PotionModifier.add(
                                UniversalPotionEffectType.WITHER,
                                400,
                                0,
                                "respawn"
                        )
                ));
            } else {
                event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
            }
        } else {
            event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
        }


    }
}
