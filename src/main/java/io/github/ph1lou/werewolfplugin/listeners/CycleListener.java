package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.ScoreAPI;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
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
import java.util.UUID;


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

        long duration = game.getConfig().getTimerValues().get(TimersBase.VOTE_DURATION.getKey());
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        groupSizeChange();

        if (game.getConfig().getConfigValues().get(ConfigsBase.VOTE.getKey()) &&
                game.getScore().getPlayerSize() < game.getConfig().getPlayerRequiredVoteEnd()) {

            game.getConfig().getConfigValues().put(ConfigsBase.VOTE.getKey(), false);
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
            game.getVote().setStatus(VoteStatus.ENDED);
        }

        if (2 * game.getConfig().getTimerValues().get(TimersBase.DAY_DURATION.getKey())
                - duration
                - game.getConfig().getTimerValues().get(TimersBase.CITIZEN_DURATION.getKey()) > 0) {

            if (game.getConfig().getConfigValues().get(ConfigsBase.VOTE.getKey())
                    && game.getConfig().getTimerValues().get(TimersBase.VOTE_BEGIN.getKey()) < 0) {
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
        long duration2 = game.getConfig().getTimerValues().get(TimersBase.POWER_DURATION.getKey());

        if (2 * game.getConfig().getTimerValues().get(TimersBase.DAY_DURATION.getKey()) - duration2 > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new SelectionEndEvent());
                }
            }, duration2 * 20);

        }

        long duration3 = game.getConfig().getTimerValues().get(TimersBase.DAY_DURATION.getKey());

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateGame.END)){
                Bukkit.getPluginManager().callEvent(new NightEvent( event.getNumber()));
            }

        },duration3*20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValues().get(TimersBase.DAY_DURATION.getKey()) - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.night", event.getNumber()));
        groupSizeChange();

        if (duration > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if (!game.isState(StateGame.END)) {
                    Bukkit.getPluginManager().callEvent(new DayWillComeEvent());
                }

            },duration*20);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateGame.END)){
                Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber()+1));
            }

        },(duration+30)*20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event) {

        long duration = game.getConfig().getTimerValues().get(TimersBase.CITIZEN_DURATION.getKey());
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
        game.getLoversManage().autoLovers();

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

        if (game.getConfig().getTimerValues().get(TimersBase.DIGGING.getKey()) >= 0) {
            return;
        }

        Block block = event.getBlock();
        List<Material> m = Arrays.asList(Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE);
        Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);

        if (m.contains(block.getType())) {
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onTroll(TrollEvent event) {

        game.setState(StateGame.GAME);
        game.getConfig().getConfigValues().put(ConfigsBase.CHAT.getKey(), false);

        main.getRegisterManager().getRolesRegister()
                .forEach(roleRegister -> {

                    if (roleRegister.getKey().equals(game.getConfig().getTrollKey())) {

                        game.getPlayersWW().values()
                                .forEach(playerWW -> {
                                    try {
                                        Roles role = (Roles) roleRegister.getConstructors()
                                                .newInstance(main,
                                                        game,
                                                        playerWW.getRole().getPlayerUUID(),
                                                        roleRegister.getKey());

                                        Bukkit.getPluginManager().registerEvents((Listener) role, main);
                                        playerWW.setRole(role);
                                    } catch (InstantiationException |
                                            InvocationTargetException |
                                            IllegalAccessException exception) {
                                        exception.printStackTrace();
                                    }
                                });
                    }
                });


        game.getPlayersWW().values()
                .forEach(playerWW -> playerWW.getRole().recoverPower());

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (!game.isState(StateGame.END)) {
                Bukkit.getOnlinePlayers()
                        .forEach(player -> {
                            if (game.getConfig().isTrollSV() &&
                                    game.getPlayersWW().containsKey(player.getUniqueId())) {
                                Sounds.PORTAL_TRIGGER.play(player);
                                for (PotionEffect po : player.getActivePotionEffects()) {
                                    player.removePotionEffect(po.getType());
                                }
                                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 20);
                                player.sendMessage(game.translate("werewolf.announcement.troll"));
                                PlayerWW playerWW = game.getPlayersWW().get(player.getUniqueId());
                                HandlerList.unregisterAll((Listener) playerWW.getRole());
                                playerWW.setKit(false);
                            }
                        });
                if (game.getConfig().getConfigValues().get(ConfigsBase.DOUBLE_TROLL.getKey())) {
                    Bukkit.getPluginManager().callEvent(new TrollEvent());
                    game.getConfig().getConfigValues().put(ConfigsBase.DOUBLE_TROLL.getKey(), false);
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

        List<UUID> playersUUID = new ArrayList<>(game.getPlayersWW().keySet());
        List<RoleRegister> config = new ArrayList<>();
        game.getConfig().getConfigValues().put(ConfigsBase.CHAT.getKey(), false);
        game.getConfig().getRoleCount().put(RolesBase.VILLAGER.getKey(),
                game.getConfig()
                        .getRoleCount()
                        .get(RolesBase.VILLAGER.getKey()) +
                        playersUUID.size() -
                        game.getScore().getRole());
        game.getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount().get(roleRegister.getKey()); i++) {
                        config.add(roleRegister);
                    }
                });


        while (!playersUUID.isEmpty()) {

            int n = (int) Math.floor(game.getRandom().nextFloat() * playersUUID.size());
            UUID playerUUID = playersUUID.get(n);
            PlayerWW plg = game.getPlayersWW().get(playerUUID);

            try {
                Roles role = (Roles) config.get(0).getConstructors().newInstance(game.getMain(),
                        game,
                        playerUUID,
                        config.get(0).getKey());

                Bukkit.getPluginManager().registerEvents((Listener) role, game.getMain());
                plg.setRole(role);

            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            config.remove(0);
            playersUUID.remove(n);
        }
        game.getPlayersWW().values()
                .forEach(playerWW -> playerWW.getRole().recoverPower());

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
}
