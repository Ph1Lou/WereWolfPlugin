package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.occultist.OccultistRevealWishes;
import io.github.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author ThugMonkeyMC
 *
 */
public class Occultist extends Villager{

    private final List<IPlayerWW> deaths = new ArrayList<>();
    private final List<IPlayerWW> troublemakers = new ArrayList<>();

    public Occultist(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent e){
        if(!this.isAbilityEnabled()){
            return;
        }
        if(e.getPlayerWW().getUUID().equals(this.getPlayerUUID())){
            this.getPlayerWW().getWish().ifPresent(wish ->
                    Bukkit.broadcastMessage(game.translate("werewolf.role.occultist.selfdeath",
                            Formatter.format("&wish&", wish))));
        }
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }
        this.addDeathPlayer(e.getPlayerWW());
        if(this.deaths.size() == 4){
            OccultistRevealWishes event = new OccultistRevealWishes();
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()){
               getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.check.cancel");
               this.deaths.clear();
               this.troublemakers.clear();
               return;
            }
            this.deaths.addAll(this.troublemakers);
            Collections.shuffle(this.deaths);
            this.getPlayerWW().sendMessageWithKey("werewolf.role.occultist.lastwishes");
            for(IPlayerWW playerWW : this.deaths){
                this.getPlayerWW().sendMessage(new TextComponent(" -> " + playerWW.getWish()));
            }
            this.deaths.clear();
            this.troublemakers.clear();
        }
    }

    @Override
    public @NotNull String getDescription() {
            return new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.role.occultist.description"))
                    .build();
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @EventHandler
    public void onWishChangeEvent(WishChangeEvent e){
        if(!this.isAbilityEnabled()){
            return;
        }
        if(e.getPlayerWW().getRole().getKey().equalsIgnoreCase(RolesBase.TROUBLEMAKER.getKey())){
            if(!this.troublemakers.contains(e.getPlayerWW())){
                this.troublemakers.add(e.getPlayerWW());
            }
        }
    }

    public void addDeathPlayer(IPlayerWW playerWW){
        this.deaths.add(playerWW);
    }

}
