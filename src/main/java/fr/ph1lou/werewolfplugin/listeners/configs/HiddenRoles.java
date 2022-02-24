package fr.ph1lou.werewolfplugin.listeners.configs;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HiddenRoles extends ListenerManager {

    @Nullable
    private IPlayerWW playerWW;

    public HiddenRoles(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRole(RepartitionEvent event) {

        List<IPlayerWW> playerWWs = this.getGame().getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> playerWW1.getRole().isCamp(Camp.VILLAGER))
                .collect(Collectors.toList());

        if(playerWWs.isEmpty()){
            return;
        }

        Collections.shuffle(playerWWs, getGame().getRandom());

        this.playerWW = playerWWs.get(0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void sendDeathMessage(AnnouncementDeathEvent event) {

        if(this.playerWW == null){
            return;
        }

        if (event.getTargetPlayer().equals(this.playerWW)) {
            return; //la prêtresse voit les vrais rôles
        }

        if (event.getTargetPlayer().equals(event.getPlayerWW())) {
            return; //le mort voit son vrai rôle
        }

        IPlayerWW playerWW = event.getTargetPlayer();

        if (playerWW.getRole().isNeutral()) {
            if (this.playerWW.isState(StatePlayer.ALIVE) && getGame().getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.hidden_roles.magic");
            }
        } else if (getGame().getRandom().nextFloat() < 0.8) {

            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if(playerWW.getRole().isWereWolf()){
                    event.setRole("werewolf.hidden_roles.magic");
                }

            }
            else{
                if(!playerWW.getRole().isWereWolf()){
                    event.setRole("werewolf.hidden_roles.magic");
                }
            }
        } else {
            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if(!playerWW.getRole().isWereWolf()){
                    event.setRole("werewolf.hidden_roles.magic");
                }
            }
            else{
                if(playerWW.getRole().isWereWolf()){
                    event.setRole("werewolf.hidden_roles.magic");
                }
            }
        }
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        this.playerWW = null;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        this.playerWW = null;
    }
}
