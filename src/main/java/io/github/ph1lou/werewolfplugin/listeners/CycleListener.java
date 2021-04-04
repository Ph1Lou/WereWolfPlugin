package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IScoreBoard;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.DayWillComeEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.PVPEvent;
import io.github.ph1lou.werewolfapi.events.TrollEvent;
import io.github.ph1lou.werewolfapi.events.TrollLoverEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.DiggingEndEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.InvulnerabilityEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import io.github.ph1lou.werewolfapi.events.lovers.LoversRepartitionEvent;
import io.github.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
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
import java.util.List;
import java.util.stream.Collectors;


public class CycleListener implements Listener {

    private final Main main;
    private final GameManager game;

    public CycleListener(Main main) {
        this.main = main;
        this.game = (GameManager) main.getWereWolfAPI();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(23500);

        long duration = game.getConfig().getTimerValue(TimersBase.VOTE_DURATION.getKey());
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        groupSizeChange();

        if (game.getConfig().isConfigActive(ConfigsBase.VOTE.getKey()) &&
                game.getScore().getPlayerSize() < game.getConfig().getPlayerRequiredVoteEnd()) {

            game.getConfig().switchConfigValue(ConfigsBase.VOTE.getKey());
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
            game.getVote().setStatus(VoteStatus.ENDED);
        }

        if (2L * game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey())
                - duration
                - game.getConfig().getTimerValue(TimersBase.CITIZEN_DURATION.getKey()) > 0) {

            if (game.getConfig().isConfigActive(ConfigsBase.VOTE.getKey())
                    && !game.getVote().isStatus(VoteStatus.NOT_BEGIN)) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time",
                        Utils.conversion((int) duration)));

                game.getVote().setStatus(VoteStatus.IN_PROGRESS);

                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    if (!game.isState(StateGame.END)) {
                        Bukkit.getPluginManager().callEvent(new VoteEndEvent());
                    }

                }, duration * 20);
            }
        }
        long duration2 = game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey());

        if (2L * game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey())
                - duration2 > 0) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new SelectionEndEvent());
                }
            }, duration2 * 20);

        }

        long duration3 = game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey());

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new NightEvent(event.getNumber()));
                CommandWereWolfChat.enable();
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, CommandWereWolfChat::disable, game.getConfig().getTimerValue(TimersBase.WEREWOLF_CHAT_DURATION.getKey()) * 20L);
            }

        },duration3*20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey())
                - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.night",
                event.getNumber()));
        groupSizeChange();

        if (duration > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new DayWillComeEvent());
                }

            }, duration * 20);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateGame.END)){
                Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber()+1));
            }

        },(duration+30)*20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event) {

        long duration = game.getConfig().getTimerValue(TimersBase.CITIZEN_DURATION.getKey());
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getPluginManager().callEvent(new VoteResultEvent());
            }

        }, duration * 20);
    }

    public void groupSizeChange() {

        IScoreBoard score = game.getScore();

        if (score.getPlayerSize() <= score.getGroup() * 3 && score.getGroup() > 3) {
            score.setGroup(score.getGroup() - 1);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        player.sendMessage(
                                game.translate(
                                        "werewolf.commands.admin.group.group_change",
                                        score.getGroup()));
                        VersionUtils.getVersionUtils().sendTitle(
                                player,
                                game.translate("werewolf.commands.admin.group.top_title"),
                                game.translate("werewolf.commands.admin.group.bot_title",
                                        score.getGroup()),
                                20,
                                60,
                                20);
                    });
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLoverRepartition(LoversRepartitionEvent event) {
        game.getLoversManager().repartition(main);

    }

    @EventHandler
    public void onPVP(PVPEvent event) {

        game.getMapManager().getWorld().setPVP(true);
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.pvp"));
                    Sound.DONKEY_ANGRY.play(player);
                });
    }

    @EventHandler
    public void onDiggingEnd(DiggingEndEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.mining"));
                    Sound.ANVIL_BREAK.play(player);
                });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        if (game.getConfig().getTimerValue(TimersBase.DIGGING.getKey()) >= 0) {
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
        game.getConfig().setConfig(ConfigsBase.CHAT.getKey(), false);

        main.getRegisterManager().getRolesRegister()
                .forEach(roleRegister -> {

                    if (roleRegister.getKey().equals(game.getConfig().getTrollKey())) {

                        game.getPlayerWW()
                                .forEach(playerWW -> {
                                    try {
                                        IRole role = (IRole) roleRegister.getConstructors()
                                                .newInstance(main,
                                                        playerWW,
                                                        roleRegister.getKey());

                                        Bukkit.getPluginManager().registerEvents((Listener) role,
                                                main);

                                        playerWW.setRole(role);
                                    } catch (InstantiationException |
                                            InvocationTargetException |
                                            IllegalAccessException exception) {
                                        exception.printStackTrace();
                                    }
                                });
                    }
                });


        game.getPlayerWW()
                .forEach(playerWW -> playerWW.getRole().roleAnnouncement());

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            
            if (!game.isState(StateGame.END)) {
                game.getPlayerWW()
                        .forEach(playerWW -> {

                            Player player = Bukkit.getPlayer(playerWW.getUUID());

                            if (player != null) {
                                Sound.PORTAL_TRIGGER.play(player);
                                for (PotionEffect po : player.getActivePotionEffects()) {
                                    player.removePotionEffect(po.getType());
                                }
                            }
                            playerWW.sendMessageWithKey("werewolf.announcement.troll");
                            playerWW.addPlayerMaxHealth(20 - playerWW.getMaxHealth());
                            HandlerList.unregisterAll((Listener) playerWW.getRole());
                        });
                if (game.getConfig().isConfigActive(ConfigsBase.DOUBLE_TROLL.getKey())) {
                    Bukkit.getPluginManager().callEvent(new TrollEvent());
                    game.getConfig().switchConfigValue(ConfigsBase.DOUBLE_TROLL.getKey());
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

        List<IPlayerWW> IPlayerWWs = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .collect(Collectors.toList());

        if (IPlayerWWs.isEmpty()) return;

        while (IPlayerWWs.size() > 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(IPlayerWWs.remove(0),
                            IPlayerWWs.remove(0))));
        }
        if (IPlayerWWs.size() == 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(IPlayerWWs.remove(0),
                            IPlayerWWs.remove(0),
                            IPlayerWWs.remove(0))));
        } else if (IPlayerWWs.size() == 2) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(IPlayerWWs.remove(0),
                            IPlayerWWs.remove(0))));
        }

        loverAPIS.forEach(ILover -> Bukkit.getPluginManager()
                .registerEvents((Listener) ILover, main));

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

            if (!game.isState(StateGame.END)) {
                game.getPlayerWW()
                        .forEach(playerWW -> playerWW
                                .sendMessageWithKey("werewolf.announcement.lover_troll"));
                game.getConfig().setTrollLover(false);
                Bukkit.getPluginManager().callEvent(new LoversRepartitionEvent());
            }
            loverAPIS.forEach(ILover -> HandlerList.unregisterAll((Listener) ILover));

        }, 1800L);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {

        game.setState(StateGame.GAME);

        List<IPlayerWW> iPlayerWWS = new ArrayList<>(game.getPlayerWW());
        List<RoleRegister> config = new ArrayList<>();

        game.getConfig().setConfig(ConfigsBase.CHAT.getKey(), false);

        game.getConfig().setRole(RolesBase.VILLAGER.getKey(),
                Math.max(0,
                        game.getConfig()
                                .getRoleCount(RolesBase.VILLAGER.getKey()) +
                                iPlayerWWS.size() -
                                game.getScore().getRole()));

        main.getRegisterManager().getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount(roleRegister.getKey()); i++) {
                        config.add(roleRegister);
                    }
                });


        while (!iPlayerWWS.isEmpty()) {

            int n = (int) Math.floor(game.getRandom().nextFloat() * iPlayerWWS.size());
            IPlayerWW playerWW = iPlayerWWS.get(n);

            try {
                IRole role = (IRole) config.get(0).getConstructors().newInstance(game.getMain(),
                        playerWW,
                        config.get(0).getKey());
                Bukkit.getPluginManager().registerEvents((Listener) role, game.getMain());
                playerWW.setRole(role);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException exception) {
                exception.printStackTrace();
            }

            config.remove(0);
            iPlayerWWS.remove(n);
        }
        game.getPlayerWW()
                .forEach(playerWW -> playerWW.getRole().roleAnnouncement());

        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::checkVictory);
    }

    @EventHandler
    public void onBorderStart(BorderStartEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.border"));
                    Sound.FIREWORK_LAUNCH.play(player);
                });
    }

    @EventHandler
    public void onInvulnerabilityEnd(InvulnerabilityEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.invulnerability"));
                    Sound.GLASS.play(player);
                });
    }

    @EventHandler
    public void onStart(StartEvent event) {
        main.getRegisterManager().getRandomEventsRegister()
                .forEach(randomEventRegister -> randomEventRegister.getRandomEvent()
                        .register(game.getRandom().nextDouble() * 100 < game.getConfig().getProbability(randomEventRegister.getKey())));
    }
}
