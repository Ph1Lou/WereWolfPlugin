package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.random_events.InfectionRandomEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

public class Infection extends ListenerManager {

    public Infection(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    if (game.getPlayersWW().stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).filter(playerWW -> playerWW.getRole().isWereWolf()).count() > game.getPlayersWW().stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).count() / 2f)
                        return;

                    List<IRole> roles1 = game.getPlayersWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .map(IPlayerWW::getRole)
                            .filter(roles -> !roles.isWereWolf())
                            .collect(Collectors.toList());

                    IRole role1 = roles1.get((int) Math.floor(game.getRandom().nextDouble() * roles1.size()));

                    InfectionRandomEvent infectionRandomEvent = new InfectionRandomEvent(role1.getPlayerWW());

                    Bukkit.getPluginManager().callEvent(infectionRandomEvent);

                    if (infectionRandomEvent.isCancelled()) return;

                    infectionRandomEvent.getPlayerWW().getRole().setInfected();
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(infectionRandomEvent.getPlayerWW()));

                    game.checkVictory();

                    register(false);

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.infection.message"));
                }
            }
        }, (long) (20 * 60 * 60 + game.getRandom().nextDouble() * 15 * 60 * 20));
    }


}
