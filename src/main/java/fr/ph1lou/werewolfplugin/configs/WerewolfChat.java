package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatPrefixEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.WEREWOLF_CHAT,
        defaultValue = true,
        meetUpValue = true),
        timers = @Timer(key = TimerBase.WEREWOLF_CHAT_DURATION,
                defaultValue = 30,
                meetUpValue = 30),
        configValues = @IntValue(key = WerewolfChat.CONFIG,
                defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.BOOK))
public class WerewolfChat extends ListenerWerewolf {

    public static final String CONFIG = IntValueBase.WEREWOLF_CHAT;

    public WerewolfChat(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onNight(NightEvent event) {

        WereWolfAPI game = this.getGame();

        if (game.isState(StateGame.END)) return;

        if(game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) > 0){
            return;
        }
        
        game.getWerewolfChatHandler().enableWereWolfChat();

        getGame().getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> {
                    WereWolfCanSpeakInChatEvent wereWolfCanSpeakInChatEvent = new WereWolfCanSpeakInChatEvent(playerWW);
                    Bukkit.getPluginManager().callEvent(wereWolfCanSpeakInChatEvent);
                    return wereWolfCanSpeakInChatEvent.canSpeak();
                })
                .forEach(this::openWereWolfChat);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> game.getWerewolfChatHandler().disableWereWolfChat(),
                game.getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION) * 20L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWerewolfChat(WereWolfChatEvent event){
        if(!event.getTargetWW().getRole().isWereWolf()){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWWChat(WereWolfChatEvent event) {

        if (!event.getTargetWW().getRole().isAbilityEnabled()) return;

        WereWolfChatPrefixEvent event1 = new WereWolfChatPrefixEvent(event.getPlayerWW(), event.getTargetWW());

        Bukkit.getPluginManager().callEvent(event1);

        Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(
                event1.getFormatters().toArray(new Formatter[0]),
                new Formatter[]{Formatter.format("&message&", event.getMessage())});

        event.getTargetWW().sendMessageWithKey(event1.getPrefix(), formatters);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatSpeak(WereWolfCanSpeakInChatEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();
        IRole iRole = playerWW.getRole();

        if (!iRole.isAbilityEnabled()) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (!iRole.isWereWolf()) return;

        event.setCanSpeak(true);
    }

    private void openWereWolfChat(IPlayerWW playerWW) {
        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.announce",
                Formatter.format("&timer&", Utils.conversion(getGame().getConfig()
                        .getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION))),
                Formatter.format("&number&", getGame().getConfig().getValue(IntValueBase.WEREWOLF_CHAT)));

        BukkitUtils.scheduleSyncDelayedTask(getGame(),
                () -> playerWW
                        .sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.disable"),
                this.getGame().getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION) * 20L);
    }
}
