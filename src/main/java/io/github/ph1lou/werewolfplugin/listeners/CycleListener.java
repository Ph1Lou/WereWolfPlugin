package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IScoreBoard;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfapi.events.TrollEvent;
import io.github.ph1lou.werewolfapi.events.TrollLoverEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayWillComeEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import io.github.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.Role;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWereWolfChat;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.roles.lovers.FakeLover;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class CycleListener implements Listener {

    private final GameManager game;

    public CycleListener(io.github.ph1lou.werewolfapi.WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(23500);

        long duration = game.getConfig().getTimerValue(TimerBase.VOTE_DURATION.getKey());
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        groupSizeChange();

        if (game.getConfig().isConfigActive(ConfigBase.VOTE.getKey()) &&
                game.getPlayerSize() < game.getConfig().getPlayerRequiredVoteEnd()) {

            game.getConfig().switchConfigValue(ConfigBase.VOTE.getKey());
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
            game.getVote().setStatus(VoteStatus.ENDED);
        }

        if (2L * game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey())
                - duration
                - game.getConfig().getTimerValue(TimerBase.CITIZEN_DURATION.getKey()) > 0) {

            if (game.getConfig().isConfigActive(ConfigBase.VOTE.getKey())
                    && !game.getVote().isStatus(VoteStatus.NOT_BEGIN)) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time",
                        Utils.conversion((int) duration)));

                game.getVote().setStatus(VoteStatus.IN_PROGRESS);

                BukkitUtils.scheduleSyncDelayedTask(() -> {
                    if (!game.isState(StateGame.END)) {
                        Bukkit.getPluginManager().callEvent(new VoteEndEvent());
                    }

                }, duration * 20);
            }
        }
        long duration2 = game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey());

        if (2L * game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey())
                - duration2 > 0) {

            BukkitUtils.scheduleSyncDelayedTask(() -> {

                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new SelectionEndEvent());
                }
            }, duration2 * 20);

        }

        long duration3 = game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey());

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new NightEvent(event.getNumber()));
                CommandWereWolfChat.enable();
                BukkitUtils.scheduleSyncDelayedTask(CommandWereWolfChat::disable, game.getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION.getKey()) * 20L);
            }

        }, duration3 * 20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey())
                - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.night",
                event.getNumber()));
        groupSizeChange();

        if (duration > 0) {
            BukkitUtils.scheduleSyncDelayedTask(() -> {
                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new DayWillComeEvent());
                }

            }, duration * 20);
        }

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber() + 1));
            }

        }, (duration + 30) * 20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event) {

        long duration = game.getConfig().getTimerValue(TimerBase.CITIZEN_DURATION.getKey());
        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new VoteResultEvent());
            }

        }, duration * 20);
    }

    public void groupSizeChange() {

        IScoreBoard score = game.getScore();

        if (game.getPlayerSize() <= game.getGroup() * 3 && game.getGroup() > 3) {
            game.setGroup(game.getGroup() - 1);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        player.sendMessage(
                                game.translate(
                                        "werewolf.commands.admin.group.group_change",
                                        game.getGroup()));
                        VersionUtils.getVersionUtils().sendTitle(
                                player,
                                game.translate("werewolf.commands.admin.group.top_title"),
                                game.translate("werewolf.commands.admin.group.bot_title",
                                        game.getGroup()),
                                20,
                                60,
                                20);
                    });
        }
    }




    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        if (game.getConfig().getTimerValue(TimerBase.DIGGING.getKey()) >= 0) {
            return;
        }

        Block block = event.getBlock();
        List<Material> m = Arrays.asList(Material.REDSTONE_ORE,
                Material.EMERALD_ORE,
                Material.LAPIS_ORE,
                Material.COAL_ORE,
                Material.IRON_ORE,
                Material.GOLD_ORE,
                Material.DIAMOND_ORE);

        Location loc = new Location(block.getWorld(),
                block.getLocation().getBlockX() + 0.5,
                block.getLocation().getBlockY() + 0.5,
                block.getLocation().getBlockZ() + 0.5);

        if (m.contains(block.getType())) {
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onTroll(TrollEvent event) {

        game.setState(StateGame.GAME);
        game.getConfig().setConfig(ConfigBase.CHAT.getKey(), false);

        RegisterManager.get().getRolesRegister()
                .forEach(roleRegister -> {

                    if (roleRegister.getKey().equals(game.getConfig().getTrollKey())) {

                        game.getPlayersWW()
                                .forEach(playerWW -> {
                                    try {
                                        IRole role = (IRole) roleRegister.getConstructors()
                                                .newInstance(game,
                                                        playerWW,
                                                        roleRegister.getKey());

                                        BukkitUtils.registerEvents((Listener) role);

                                        playerWW.setRole(role);
                                    } catch (InstantiationException |
                                            InvocationTargetException |
                                            IllegalAccessException exception) {
                                        exception.printStackTrace();
                                    }
                                });
                    }
                });


        game.getPlayersWW()
                .forEach(playerWW -> playerWW.getRole().roleAnnouncement());

        BukkitUtils.scheduleSyncDelayedTask(() -> {

            if (!game.isState(StateGame.END)) {
                game.getPlayersWW()
                        .forEach(playerWW -> {
                            HandlerList.unregisterAll((Listener) playerWW.getRole());
                            Sound.PORTAL_TRIGGER.play(playerWW);
                            playerWW.clearPotionEffects();
                            playerWW.sendMessageWithKey("werewolf.announcement.troll");
                            playerWW.addPlayerMaxHealth(20 - playerWW.getMaxHealth());
                        });
                if (game.getConfig().isConfigActive(ConfigBase.DOUBLE_TROLL.getKey())) {
                    Bukkit.getPluginManager().callEvent(new TrollEvent());
                    game.getConfig().switchConfigValue(ConfigBase.DOUBLE_TROLL.getKey());
                    game.setDebug(false);
                } else {
                    game.getConfig().setTrollSV(false);
                    Bukkit.getPluginManager().callEvent(new RepartitionEvent());
                }
            }

        }, 1800L);
    }

    @EventHandler
    public void onTrollLover(TrollLoverEvent event) {

        List<ILover> loverAPIS = new ArrayList<>();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        while (playerWWS.size() > 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0))));
        }
        if (playerWWS.size() == 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0),
                            playerWWS.remove(0))));
        } else if (playerWWS.size() == 2) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0))));
        }

        loverAPIS.forEach(ILover -> BukkitUtils
                .registerEvents((Listener) ILover));

        BukkitUtils.scheduleSyncDelayedTask(() -> {

            if (!game.isState(StateGame.END)) {
                game.getPlayersWW()
                        .forEach(playerWW -> playerWW
                                .sendMessageWithKey("werewolf.announcement.lover_troll"));
                game.getConfig().setTrollLover(false);
                game.getLoversManager().repartition();
            }
            loverAPIS.forEach(ILover -> HandlerList.unregisterAll((Listener) ILover));

        }, 1800L);
    }
    @EventHandler
    public void onRepartition(RepartitionEvent event) {

        game.setState(StateGame.GAME);

        List<IPlayerWW> playerWWS = new ArrayList<>(game.getPlayersWW());
        List<RoleRegister> config = new ArrayList<>();

        game.getConfig().setConfig(ConfigBase.CHAT.getKey(), false);

        game.getConfig().setRole(RolesBase.VILLAGER.getKey(),
                Math.max(0,
                        game.getConfig()
                                .getRoleCount(RolesBase.VILLAGER.getKey()) +
                                playerWWS.size() -
                                game.getRoleInitialSize()));

        RegisterManager.get().getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount(roleRegister.getKey()); i++) {
                        config.add(roleRegister);
                    }
                });

        Collections.shuffle(playerWWS, game.getRandom());

        while (!playerWWS.isEmpty()) {
            IPlayerWW playerWW = playerWWS.remove(0);
            RoleRegister roleRegister = config.remove(0);
            try {
                Role role = (Role) roleRegister.getConstructors().newInstance(game,
                        playerWW,
                        roleRegister.getKey());
                BukkitUtils.registerEvents(role);
                playerWW.setRole(role);
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }
        game.getPlayersWW()
                .forEach(playerWW -> playerWW.getRole().roleAnnouncement());

        BukkitUtils.scheduleSyncDelayedTask(game::checkVictory);
    }


    @EventHandler
    public void onStart(StartEvent event) {
        RegisterManager.get().getRandomEventsRegister()
                .forEach(randomEventRegister -> randomEventRegister.getRandomEvent()
                        .register(game.getRandom().nextDouble() * 100 < game.getConfig().getProbability(randomEventRegister.getKey())));
    }
}
