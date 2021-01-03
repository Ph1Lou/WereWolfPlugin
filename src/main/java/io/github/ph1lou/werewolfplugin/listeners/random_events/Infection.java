package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.InfectionRandomEvent;
import io.github.ph1lou.werewolfapi.events.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.events.RepartitionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class Infection extends ListenerManager {

    public Infection(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    if (game.getPlayerWW().stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).filter(playerWW -> playerWW.getRole().isWereWolf()).count() > game.getPlayerWW().stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).count() / 2f)
                        return;

                    List<Roles> roles1 = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .map(PlayerWW::getRole)
                            .filter(roles -> !roles.isWereWolf())
                            .collect(Collectors.toList());

                    Roles role1 = roles1.get((int) Math.floor(game.getRandom().nextDouble() * roles1.size()));

                    InfectionRandomEvent infectionRandomEvent = new InfectionRandomEvent(role1.getPlayerWW());

                    Bukkit.getPluginManager().callEvent(infectionRandomEvent);

                    if (infectionRandomEvent.isCancelled()) return;

                    infectionRandomEvent.getPlayerWW().getRole().setInfected();
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(infectionRandomEvent.getPlayerWW()));

                    game.checkVictory();

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.infection.message"));
                }
            }
        }, (long) (20 * 60 * 60 + game.getRandom().nextDouble() * 15 * 60 * 20));
    }


}
