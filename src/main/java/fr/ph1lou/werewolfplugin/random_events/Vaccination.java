package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.events.game.utils.CountRemainingRolesCategoriesEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;

import java.util.*;

@SuppressWarnings("unused")
@Event(key=EventBase.VACCINATION,
        loreKey = "werewolf.random_events.vaccination.description")
public class Vaccination extends ListenerWerewolf {
    private Map<UUID, Camp> infectedPlayers = new HashMap<>();
    private List<UUID> vaccinatedPlayers = new ArrayList<>();

    public Vaccination(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInfection(InfectionEvent e){
        if (e.isCancelled()) return;
        IPlayerWW playerWW = e.getTargetWW();
        IRole playerRole = playerWW.getRole();

        // la vaccination ne concerne que un changement de camp
        if (playerRole.isWereWolf() | playerRole.isInfected()) return;

        this.infectedPlayers.put(playerWW.getUUID(), playerRole.getCamp());
    }

    @EventHandler()
    public void onNewWereWolfPlayer(NewWereWolfEvent e){
        IPlayerWW playerWW = e.getPlayerWW();
        if(!this.infectedPlayers.containsKey(playerWW.getUUID())) return;

        IRole playerRole = playerWW.getRole();

        BukkitUtils.scheduleSyncDelayedTask(new Runnable() {
            @Override
            public void run() {
                //récupére et retire le joueur de la liste des joueurs infectés
                Camp lastPlayerCamp = Vaccination.this.infectedPlayers.remove(playerWW.getUUID());
                Vaccination.this.vaccinatePlayer(playerWW, lastPlayerCamp);
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWinConditionsCheck(WinConditionsCheckEvent e){
        if (e.isCancelled() & !this.allowWin()){
            e.setCancelled(false); //bloque la victoire tant que le joueur n'est pas guéri
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCountRemainingRolesCategories(CountRemainingRolesCategoriesEvent e){
        for (UUID uuid : this.infectedPlayers.keySet()){
            Optional<IPlayerWW> playerOptional = this.getGame().getPlayerWW(uuid);
            Camp playerCamp = this.infectedPlayers.remove(uuid);

            playerOptional.ifPresent(playerWW -> this.vaccinatePlayer(playerWW, playerCamp));
        }
        //compte l'ancien camp dans les camps en vie
        for (Map.Entry<UUID, Camp> entry: this.infectedPlayers.entrySet()){

            switch (entry.getValue()){
                case VILLAGER:
                    e.addVillager();
                    break;
                case NEUTRAL:
                    e.addNeutral();
                    break;
                case WEREWOLF:
                    e.addWerewolf();
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent e){
        if(this.vaccinatedPlayers.contains(e.getPlayerUUID()) & !e.isAppear()){
            e.setAppear(true); //affiche les joueurs vaccinés dans la liste
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRequestSeeWereWolfList(RequestSeeWereWolfListEvent e){
        if(this.vaccinatedPlayers.contains(e.getPlayerUUID()) & e.isAccept()){
            e.setAccept(false); //empeche les joueurs vaccinées d'obtenir la liste
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCanSpeakInChatEvent(WereWolfCanSpeakInChatEvent e){
        if (this.vaccinatedPlayers.contains(e.getPlayerWW().getUUID())){
            e.setCanSpeak(false);
        }
    }

    public boolean allowWin(){
        return this.infectedPlayers.isEmpty();
    }

    public void vaccinatePlayer(IPlayerWW playerWW, Camp playerCamp){
        if (playerWW == null) return;

        if (playerCamp.equals(Camp.NEUTRAL)){
            playerWW.getRole().setTransformedToNeutral(true);
            playerWW.getRole().setInfected(false);

        }else if(playerCamp.equals(Camp.VILLAGER)){
            playerWW.getRole().setTransformedToVillager(true);
            playerWW.getRole().setInfected(false);
        }

        playerWW.sendMessageWithKey("werewolf.random_events.vaccination.infection_cancelled");
        playerWW.getRole().recoverPotionEffects();

        //attend 10 secondes que le joueur soit tombé pour reset ses effets
        BukkitUtils.scheduleSyncDelayedTask(new Runnable() {
            @Override
            public void run() {
                playerWW.clearPotionEffects();
                playerWW.getRole().recoverPotionEffects();
                Bukkit.getLogger().info("[Vaccination]: potions effects cleared and go back to normal");
            }
        }, 20 * 10);

        this.vaccinatedPlayers.add(playerWW.getUUID());
        this.getGame().checkVictory();
    }
}
