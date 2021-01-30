package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class LoneWolf extends ListenerManager {

    public LoneWolf(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void designAloneWolf(WereWolfListEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (!game.isState(StateGame.END) && isRegister()) {
                designSolitary();
            }
        }, (long) (game.getRandom().nextFloat() * 3600 * 20));
    }

    private void designSolitary() {

        WereWolfAPI game = main.getWereWolfAPI();

        List<Roles> roleWWs = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(Roles::isWereWolf)
                .collect(Collectors.toList());

        if (roleWWs.isEmpty()) return;

        Roles role = roleWWs.get((int) Math.floor(game.getRandom().nextDouble() * roleWWs.size()));

        role.getPlayerWW().sendMessageWithKey("werewolf.lone_wolf.message");

        if (role.getPlayerWW().getMaxHealth() < 30) {
            role.getPlayerWW().addPlayerMaxHealth(Math.min(8, 30 - role.getPlayerWW().getMaxHealth()));
        }

        role.setSolitary(true);
        register(false);
    }

    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) > 0) return;

        designSolitary();
    }
}
