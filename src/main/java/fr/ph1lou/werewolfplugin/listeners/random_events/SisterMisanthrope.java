package fr.ph1lou.werewolfplugin.listeners.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SisterMisanthrope extends ListenerManager {

    @Nullable()
    private IPlayerWW sisterWW;

    public SisterMisanthrope(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        WereWolfAPI game = this.getGame();
        List<IPlayerWW> sisters = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isKey(RolesBase.SISTER.getKey()))
                .collect(Collectors.toList());

        if(sisters.size() < 2){
            return;
        }
        Collections.shuffle(sisters, game.getRandom());

        this.sisterWW = sisters.get(0);

        if (this.sisterWW.getRole().isWereWolf()) {
            return;
        }

        this.sisterWW.sendMessageWithKey(Prefix.BLUE.getKey(),"werewolf.random_events.sister_misanthrope.message");
        this.sisterWW.getRole().setInfected();
        Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(this.sisterWW));
    }

    @EventHandler
    public void onWerewolfChat(WereWolfCanSpeakInChatEvent event){
        if(event.getPlayerWW().equals(this.sisterWW)){
            event.setCanSpeak(false);
        }
    }

    @EventHandler
    public void onRequestWerewolfList(AppearInWereWolfListEvent event){
        if(this.sisterWW != null && event.getPlayerUUID().equals(this.sisterWW.getUUID())){
            event.setAppear(false);
            this.sisterWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.commands.admin.ww_chat.not_access");
        }
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        this.sisterWW = null;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        this.sisterWW = null;
    }
}
