package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.random_events.NightAssemblyEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RandomEvent(key = EventBase.NIGHT_ASSEMBLY, loreKey = "werewolf.random_events.night_assembly.description")
public class NightAssembly extends ListenerWerewolf {

    private static final int ASSEMBLY_DURATION = 60;
    private static final int MIN_EPISODE = 3;

    private boolean triggered = false;
    private boolean assemblyActive = false;
    @Nullable
    private BukkitTask assemblyTask = null;
    private final Map<UUID, UUID> votes = new HashMap<>();
    private final Set<UUID> voters = new HashSet<>();
    @Nullable
    private IPlayerWW mostTargeted = null;
    private boolean arrowActive = false;
    @Nullable
    private IPlayerWW weaknessTarget = null;

    public NightAssembly(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onNight(NightEvent event) {
        WereWolfAPI game = this.getGame();

        if (weaknessTarget != null) {
            if (weaknessTarget.isState(StatePlayer.ALIVE)) {
                weaknessTarget.addPotionModifier(
                        PotionModifier.remove(UniversalPotionEffectType.WEAKNESS, EventBase.NIGHT_ASSEMBLY, 0));
            }
            weaknessTarget = null;
        }

        if (triggered) return;
        if (event.getNumber() < MIN_EPISODE) return;

        if (game.getRandom().nextBoolean()) {
            startAssembly();
        }
    }

    private void startAssembly() {
        WereWolfAPI game = this.getGame();
        triggered = true;
        assemblyActive = true;
        votes.clear();
        voters.clear();

        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.getRole().isWereWolf()) {
                p.sendMessageWithKey(Prefix.YELLOW, "werewolf.random_events.night_assembly.announce");
            }
        }

        cancelAssemblyTask();
        assemblyTask = Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
            if (game.isState(StateGame.END) || !assemblyActive) return;
            Set<IPlayerWW> voterWWs = new HashSet<>();
            voters.forEach(uuid -> game.getPlayerWW(uuid).ifPresent(voterWWs::add));
            Bukkit.getPluginManager().callEvent(new NightAssemblyEndEvent(voterWWs));
        }, ASSEMBLY_DURATION * 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAssemblyEnd(NightAssemblyEndEvent event) {
        if (!assemblyActive) return;
        assemblyActive = false;
        cancelAssemblyTask();
        if (event.isCancelled()) return;
        processResults();
    }

    private void cancelAssemblyTask() {
        if (assemblyTask != null) {
            assemblyTask.cancel();
            assemblyTask = null;
        }
    }

    private void processResults() {
        WereWolfAPI game = this.getGame();

        Map<UUID, Integer> voteCount = new HashMap<>();
        for (UUID target : votes.values()) {
            voteCount.merge(target, 1, Integer::sum);
        }

        mostTargeted = null;
        int maxVotes = 0;
        for (Map.Entry<UUID, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes) {
                IPlayerWW target = game.getPlayerWW(entry.getKey()).orElse(null);
                if (target != null) {
                    mostTargeted = target;
                    maxVotes = entry.getValue();
                }
            }
        }

        for (IPlayerWW wolf : game.getAlivePlayersWW()) {
            if (!wolf.getRole().isWereWolf()) continue;

            if (voters.contains(wolf.getUUID())) {
                wolf.modifyHonor(-1);
                wolf.sendMessageWithKey(Prefix.YELLOW, "werewolf.random_events.night_assembly.voted");
            } else {
                wolf.modifyHonor(1);
                wolf.sendMessageWithKey(Prefix.GREEN, "werewolf.random_events.night_assembly.not_voted");
            }
        }

        if (mostTargeted != null) {
            arrowActive = true;
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {
        cancelAssemblyTask();
        assemblyActive = false;
        arrowActive = false;

        if (mostTargeted != null && mostTargeted.isState(StatePlayer.ALIVE)) {
            mostTargeted.sendMessageWithKey(Prefix.RED, "werewolf.random_events.night_assembly.targeted");
            mostTargeted.addPotionModifier(
                    PotionModifier.add(UniversalPotionEffectType.WEAKNESS, EventBase.NIGHT_ASSEMBLY));
            weaknessTarget = mostTargeted;
        }
        mostTargeted = null;
    }

    @EventHandler
    public void onActionBar(ActionBarEvent event) {
        if (!arrowActive) return;
        if (mostTargeted == null) return;

        WereWolfAPI game = this.getGame();
        IPlayerWW viewer = game.getPlayerWW(event.getPlayerUUID()).orElse(null);
        if (viewer == null) return;
        if (!viewer.isState(StatePlayer.ALIVE)) return;
        if (!viewer.getRole().isWereWolf()) return;
        if (!voters.contains(viewer.getUUID())) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) return;

        event.getActionBar().append(" ").append(Utils.updateArrow(player, mostTargeted.getLocation()));
    }

    public boolean isAssemblyActive() {
        return assemblyActive;
    }

    public void vote(IPlayerWW voter, IPlayerWW target) {
        votes.put(voter.getUUID(), target.getUUID());
        voters.add(voter.getUUID());
    }
}
