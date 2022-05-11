package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.InfectionRandomEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@Event(key = EventBase.INFECTION, loreKey = "werewolf.random_events.infection.description")
public class Infection extends ListenerManager {

    public Infection(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
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

                    if(roles1.isEmpty()) return;

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
