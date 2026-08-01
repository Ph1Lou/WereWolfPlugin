package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.events.game.configs.LoneWolfEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.LONE_WOLF))
public class LoneWolf extends ListenerWerewolf {

    private static final double SOLO_HEALTH = 28;

    private int threshold = -1;

    public LoneWolf(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event) {

        WereWolfAPI game = this.getGame();

        int initialCount = game.getPlayersWW().size();

        double ratio = 0.65 + game.getRandom().nextDouble() * 0.25;

        this.threshold = (int) Math.round(initialCount * ratio);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDeath(FinalDeathEvent event) {

        if (this.threshold < 0) return;

        WereWolfAPI game = this.getGame();

        if (game.getAlivePlayersWW().size() < this.threshold) {
            this.designSolitary();
        }
    }

    private void designSolitary() {

        WereWolfAPI game = this.getGame();

        List<IRole> roleWWs = game.getAlivePlayersWW()
                .stream()
                .map(IPlayerWW::getRole)
                .filter(IRole::isWereWolf)
                .filter(iRole -> !iRole.isNeutral())
                .collect(Collectors.toList());

        if (roleWWs.isEmpty()) return;

        IRole role = roleWWs.get((int) Math.floor(game.getRandom().nextDouble() * roleWWs.size()));

        LoneWolfEvent event = new LoneWolfEvent(role.getPlayerWW());

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        IPlayerWW playerWW = role.getPlayerWW();

        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.lone_wolf.message");

        double currentMax = playerWW.getMaxHealth();
        if (currentMax < SOLO_HEALTH) {
            playerWW.addPlayerMaxHealth(SOLO_HEALTH - currentMax);
        } else if (currentMax > SOLO_HEALTH) {
            playerWW.removePlayerMaxHealth(currentMax - SOLO_HEALTH);
        }

        playerWW.addPlayerHealth(SOLO_HEALTH - playerWW.getHealth());

        role.setSolitary(true);
        register(false);
    }
}
