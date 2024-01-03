package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.random_events.VaccinationEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RandomEvent(key = EventBase.VACCINATION,
        loreKey = "werewolf.random_events.vaccination.description")
public class Vaccination extends ListenerWerewolf {
    private final List<IPlayerWW> vaccinatedPlayers = new ArrayList<>();

    public Vaccination(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInfection(InfectionEvent e) {
        if (e.isCancelled()) return;


        IPlayerWW infectedPlayerWW = e.getTargetWW();
        IRole infectedRole = infectedPlayerWW.getRole();

        // la vaccination ne concerne que un changement de camp
        if (infectedRole.isWereWolf() | infectedRole.isInfected()) return;

        IPlayerWW infectPlayer = e.getPlayerWW();

        VaccinationEvent vaccinationEvent = new VaccinationEvent(infectPlayer, infectedPlayerWW);
        Bukkit.getPluginManager().callEvent(vaccinationEvent);
        if (vaccinationEvent.isCancelled()) {
            return;
        }

        //annule l'évenement mais cache l'annulation à l'ipdl
        e.setInformInfectionCancelledMessage(false);
        e.setCancelled(true);

        infectPlayer.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.infect_father_of_the_wolves.infection_perform",
                Formatter.player(infectedPlayerWW.getName()));

        this.getGame().resurrection(infectedPlayerWW);
        infectedPlayerWW.sendMessageWithKey(Prefix.RED, "werewolf.random_events.vaccination.infection_cancelled");

        NewWereWolfEvent newWereWolfEvent = new NewWereWolfEvent(infectedPlayerWW);
        Bukkit.getPluginManager().callEvent(newWereWolfEvent);

        this.vaccinatedPlayers.add(infectedPlayerWW);

        UpdateNameTagEvent nameTagEvent = new UpdateNameTagEvent(infectedPlayerWW);
        Bukkit.getPluginManager().callEvent(nameTagEvent);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent e) {

        if (this.vaccinatedPlayers.contains(e.getTargetWW()) && !e.getTargetWW().isState(StatePlayer.DEATH)) {
            e.setAppear(true); //affiche les joueurs vaccinés dans la liste
        }
    }
}
