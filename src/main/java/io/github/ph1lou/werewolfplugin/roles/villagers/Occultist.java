package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ThugMonkeyMC
 *
 */
public class Occultist extends Villager{

    private List<IPlayerWW> deaths = new ArrayList<IPlayerWW>();

    public Occultist(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent e){
        if(e.getPlayerWW().getUUID().equals(this.getPlayerUUID())){
            Bukkit.broadcastMessage("L'occultiste est mort sa dernière volonté était: " + this.getPlayerWW().getWish());
        }
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }
        this.addDeathPlayer(e.getPlayerWW());
        if(this.getDeaths().size() == 4){
            Collections.shuffle(deaths);
            for(IPlayerWW playerWW : this.getDeaths()){
                this.getPlayerWW().sendMessage(new TextComponent("Voici les volontés des 4 derniers morts:"));
                this.getPlayerWW().sendMessage(new TextComponent(" -> " + playerWW.getWish()));;
            }
            this.getDeaths().clear();
        }
    }

    @EventHandler
    public void onWishChangeEvent(WishChangeEvent e){
        if(e.getPlayerWW().getRole().getKey().equalsIgnoreCase(RolesBase.TROUBLEMAKER.getKey())){
            this.getPlayerWW().sendMessage(new TextComponent("La dernière volonté du trublion est maintenant: " + e.getWish()));
        }
    }

    public List<IPlayerWW> getDeaths() {
        return deaths;
    }

    public void addDeathPlayer(IPlayerWW playerWW){
        this.deaths.add(playerWW);
    }

}
