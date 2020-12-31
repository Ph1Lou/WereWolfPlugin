package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.ScoreAPI;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWereWolfChat;
import io.github.ph1lou.werewolfplugin.game.GameManager;
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

        if (2 * game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey())
                - duration
                - game.getConfig().getTimerValue(TimersBase.CITIZEN_DURATION.getKey()) > 0) {

            if (game.getConfig().isConfigActive(ConfigsBase.VOTE.getKey())
                    && game.getConfig().getTimerValue(TimersBase.VOTE_BEGIN.getKey()) < 0) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time",
                        game.getScore().conversion((int) duration)));

                game.getVote().setStatus(VoteStatus.IN_PROGRESS);

                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    if (!game.isState(StateGame.END)) {
                        Bukkit.getPluginManager().callEvent(new VoteEndEvent());
                    }

                }, duration * 20);
            }
        }
        long duration2 = game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey());

        if (2 * game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey())
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, CommandWereWolfChat::disable, game.getConfig().getTimerValue(TimersBase.WEREWOLF_CHAT_DURATION.getKey()) * 20);
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

        ScoreAPI score = game.getScore();

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
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.announcement.pvp"));
            Sounds.DONKEY_ANGRY.play(p);
        }
    }

    @EventHandler
    public void onDiggingEnd(DiggingEndEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.mining"));
                    Sounds.ANVIL_BREAK.play(player);
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
                                        Roles role = (Roles) roleRegister.getConstructors()
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
                                Sounds.PORTAL_TRIGGER.play(player);
                                for (PotionEffect po : player.getActivePotionEffects()) {
                                    player.removePotionEffect(po.getType());
                                }
                            }
                            playerWW.sendMessage(game.translate("werewolf.announcement.troll"));
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
    public void onRepartition(RepartitionEvent event) {

        game.setState(StateGame.GAME);

        List<PlayerWW> playerWWS = new ArrayList<>(game.getPlayerWW());
        List<RoleRegister> config = new ArrayList<>();

        game.getConfig().setConfig(ConfigsBase.CHAT.getKey(), false);

        game.getConfig().setRole(RolesBase.VILLAGER.getKey(),
                Math.max(0,
                        game.getConfig()
                                .getRoleCount(RolesBase.VILLAGER.getKey()) +
                                playerWWS.size() -
                                game.getScore().getRole()));

        main.getRegisterManager().getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount(roleRegister.getKey()); i++) {
                        config.add(roleRegister);
                    }
                });


        while (!playerWWS.isEmpty()) {

            int n = (int) Math.floor(game.getRandom().nextFloat() * playerWWS.size());
            PlayerWW playerWW = playerWWS.get(n);

            try {
                Roles role = (Roles) config.get(0).getConstructors().newInstance(game.getMain(),
                        playerWW,
                        config.get(0).getKey());
                Bukkit.getPluginManager().registerEvents((Listener) role, game.getMain());
                playerWW.setRole(role);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException exception) {
                exception.printStackTrace();
            }

            config.remove(0);
            playerWWS.remove(n);
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
                    Sounds.FIREWORK_LAUNCH.play(player);
                });
    }

    @EventHandler
    public void onInvulnerabilityEnd(InvulnerabilityEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(game.translate("werewolf.announcement.invulnerability"));
                    Sounds.GLASS.play(player);
                });
    }

    @EventHandler
    public void onStart(StartEvent event) {
        main.getRegisterManager().getRandomEventsRegister()
                .forEach(randomEventRegister -> randomEventRegister.getRandomEvent()
                        .register(game.getRandom().nextDouble() * 100 < randomEventRegister.getDefaultValue()));
    }
}
