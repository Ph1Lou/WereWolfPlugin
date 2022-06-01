package fr.ph1lou.werewolfplugin.random_events;

import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.DiscordEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.role.interfaces.ICamp;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@Event(key = EventBase.DISCORD, loreKey = "werewolf.random_events.discord.description",
        timers = {@Timer(key = Discord.TIMER_START, defaultValue = 55*60, meetUpValue = 20*60),
                @Timer(key = Discord.PERIOD, defaultValue = 30*60, meetUpValue = 15*60)})
public class Discord extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.discord.timer_start";
    public static final String PERIOD = "werewolf.random_events.discord.period";

    public Discord(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    List<IPlayerWW> playerWWsWerewolf = game.getPlayersWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .map(IPlayerWW::getRole)
                            .filter(ICamp::isWereWolf)
                            .map(IRole::getPlayerWW)
                            .collect(Collectors.toList());

                    if(playerWWsWerewolf.isEmpty()) return;

                    IPlayerWW werewolf = playerWWsWerewolf
                            .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsWerewolf.size()));

                    List<IPlayerWW> playerWWsNeutral = game.getPlayersWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> !playerWW.equals(werewolf))
                            .map(IPlayerWW::getRole)
                            .filter(ICamp::isNeutral)
                            .filter(iRole -> !iRole.isWereWolf())
                            .map(IRole::getPlayerWW)
                            .collect(Collectors.toList());

                    if(playerWWsNeutral.isEmpty()) return;

                    IPlayerWW neutral = playerWWsNeutral
                            .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsNeutral.size()));


                    List<IPlayerWW> playerWWsVillager = game.getPlayersWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> !playerWW.equals(werewolf))
                            .filter(playerWW -> !playerWW.equals(neutral))
                            .map(IPlayerWW::getRole)
                            .filter(iRole -> iRole.isCamp(Camp.VILLAGER))
                            .map(IRole::getPlayerWW)
                            .collect(Collectors.toList());

                    if(playerWWsVillager.isEmpty()) return;

                    IPlayerWW villager = playerWWsVillager
                            .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsVillager.size()));

                    DiscordEvent discordEvent = new DiscordEvent(Sets.newHashSet(werewolf, neutral, villager));
                    Bukkit.getPluginManager().callEvent(discordEvent);

                    if (discordEvent.isCancelled()) return;

                    werewolf.getRole().setSolitary(true);
                    werewolf.sendMessageWithKey(Prefix.RED , "werewolf.configurations.lone_wolf.message");

                    if (werewolf.getMaxHealth() < 30) {
                        werewolf.addPlayerMaxHealth(Math.min(8, 30 - werewolf.getMaxHealth()));
                    }
                    neutral.getRole().setTransformedToVillager(true);
                    if(neutral.getRole().isCamp(Camp.VILLAGER)){
                        neutral.sendMessageWithKey(Prefix.RED,"werewolf.random_events.discord.to_villager");
                    }

                    villager.getRole().setInfected();
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(villager));

                    register(false);

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.discord.message"));
                }
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) + game.getRandom().nextDouble() * 20 * game.getConfig().getTimerValue(PERIOD)));
    }
}
