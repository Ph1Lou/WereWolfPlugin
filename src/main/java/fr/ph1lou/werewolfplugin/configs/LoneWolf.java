package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.configs.LoneWolfEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.LONE_WOLF),
        timers = @Timer(key = TimerBase.LONE_WOLF_DURATION, defaultValue = 60 * 60, meetUpValue = 20 * 60))
public class LoneWolf extends ListenerWerewolf {

    public LoneWolf(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void designAloneWolf(WereWolfListEvent event) {

        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game,
                this::designSolitary,
                (long) (game.getRandom().nextFloat() * game.getConfig().getTimerValue(TimerBase.LONE_WOLF_DURATION) * 20));
    }

    private void designSolitary() {

        WereWolfAPI game = this.getGame();

        List<IRole> roleWWs = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(IRole::isWereWolf)
                .filter(iRole -> !iRole.isNeutral())
                .collect(Collectors.toList());

        if (roleWWs.isEmpty()) return;

        IRole role = roleWWs.get((int) Math.floor(game.getRandom().nextDouble() * roleWWs.size()));

        LoneWolfEvent event = new LoneWolfEvent((role.getPlayerWW()));

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        role.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.configurations.lone_wolf.message");

        if (role.getPlayerWW().getMaxHealth() < 30) {
            role.getPlayerWW().addPlayerMaxHealth(Math.max(0, Math.min(8, 30 - role.getPlayerWW().getMaxHealth())));
        }
        role.setSolitary(true);
        register(false);
    }
}
