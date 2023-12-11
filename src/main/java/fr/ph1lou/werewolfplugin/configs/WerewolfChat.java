package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
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
    public void onDay(DayEvent event) {

        WereWolfAPI game = this.getGame();

        if (game.isState(StateGame.END)) return;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            game.getWerewolfChatHandler().enableWereWolfChat();
            BukkitUtils.scheduleSyncDelayedTask(game, () -> game.getWerewolfChatHandler().disableWereWolfChat(),
                    game.getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION) * 20L);

        }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L);
    }
}
