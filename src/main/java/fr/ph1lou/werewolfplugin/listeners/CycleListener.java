package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.TrollEvent;
import fr.ph1lou.werewolfapi.events.TrollLoverEvent;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayWillComeEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.RegisterManager;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.LoversManagement;
import fr.ph1lou.werewolfplugin.roles.lovers.FakeLover;
import fr.ph1lou.werewolfplugin.statistiks.StatistiksUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class CycleListener implements Listener {

    private final GameManager game;

    public CycleListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(23500);

        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE.getKey() , "werewolf.announcement.day",
                Formatter.number(event.getNumber())));
        groupSizeChange();


        long duration = game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey());

        if (2L * game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey())
                - duration > 0) {

            BukkitUtils.scheduleSyncDelayedTask(() -> {

                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new SelectionEndEvent());
                }
            }, duration * 20);

        }

        long duration2 = game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey());

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new NightEvent(event.getNumber()));
                game.getWerewolfChatHandler().enableWereWolfChat();
                BukkitUtils.scheduleSyncDelayedTask(() -> game.getWerewolfChatHandler().disableWereWolfChat(), game.getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION.getKey()) * 20L);
            }

        }, duration2 * 20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey())
                - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        if(event.getNumber()%2==0){
            BukkitUtils.scheduleSyncDelayedTask(() -> {
                if(!game.isState(StateGame.END)){
                    Bukkit.broadcastMessage(StatistiksUtils.getMessage());
                }
            }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey()) * 10L);
        }

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.announcement.night",
                Formatter.number(event.getNumber())));
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

    public void groupSizeChange() {

        if (game.getPlayersCount() <= game.getGroup() * 3 && game.getGroup() > 3) {
            game.setGroup(game.getGroup() - 1);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        player.sendMessage(
                                game.translate(
                                        Prefix.ORANGE.getKey() , "werewolf.commands.admin.group.group_change",
                                        Formatter.number(game.getGroup())));
                        VersionUtils.getVersionUtils().sendTitle(
                                player,
                                game.translate("werewolf.commands.admin.group.top_title"),
                                game.translate("werewolf.commands.admin.group.bot_title",
                                        Formatter.number(game.getGroup())),
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

                                        BukkitUtils.registerEvents(role);

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
                            HandlerList.unregisterAll(playerWW.getRole());
                            Sound.PORTAL_TRIGGER.play(playerWW);
                            playerWW.clearPotionEffects();
                            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.announcement.troll");
                            playerWW.addPlayerMaxHealth(20 - playerWW.getMaxHealth());
                        });
                game.getPlayersWW().forEach(IPlayerWW::clearLover);
                Iterator<? extends ILover> iterator = game.getLoversManager().getLovers().iterator();
                while (iterator.hasNext()){
                    HandlerList.unregisterAll(iterator.next());
                    iterator.remove();
                }

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

        loverAPIS.forEach(BukkitUtils::registerEvents);
        loverAPIS.forEach(iLover -> ((FakeLover)iLover).announceLovers());

        BukkitUtils.scheduleSyncDelayedTask(() -> {

            if (!game.isState(StateGame.END)) {
                loverAPIS.forEach(HandlerList::unregisterAll);
                game.getPlayersWW()
                        .forEach(playerWW -> {
                            playerWW
                                    .sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.announcement.lover_troll");
                            loverAPIS.forEach(((IPlayerWW)playerWW)::removeLover);
                        });
                game.getConfig().setTrollLover(false);
                ((LoversManagement)game.getLoversManager()).repartition();
            }

        }, 1200L);
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
                IRole role = (IRole) roleRegister.getConstructors().newInstance(game,
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
