package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.occultist.OccultistRevealWishes;
import fr.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
    public void onFinalDeath(FinalDeathEvent event){
        if(!this.isAbilityEnabled()){
            return;
        }
        if(event.getPlayerWW().getUUID().equals(this.getPlayerUUID())){
            this.getPlayerWW().getWish().ifPresent(wish ->
                    Bukkit.broadcastMessage(game.translate(Prefix.ORANGE.getKey(),"werewolf.role.occultist.self_death",
                            Formatter.format("&wish&", wish))));
        }
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }
        this.addDeathPlayer(event.getPlayerWW());
        if(this.deaths.size() == 4){
            OccultistRevealWishes occultistRevealWishes = new OccultistRevealWishes(this.getPlayerWW());
            Bukkit.getPluginManager().callEvent(occultistRevealWishes);
            if(occultistRevealWishes.isCancelled()){
                getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.check.cancel");
                this.deaths.clear();
                this.troublemakers.clear();
                return;
            }
            this.deaths.addAll(this.troublemakers);
            Collections.shuffle(this.deaths, game.getRandom());
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(),"werewolf.role.occultist.last_wishes");
            for(IPlayerWW playerWW : this.deaths){
                playerWW.getWish().ifPresent(wish -> this.getPlayerWW().sendMessage(new TextComponent(" -> " + wish )));

            }
            this.deaths.clear();
            this.troublemakers.clear();
        }
    }

    @EventHandler
    public void onDay(DayEvent event){
        if(event.getNumber()==6){
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE.getKey(),"werewolf.role.occultist.command"));
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
    public void onWishChangeEvent(WishChangeEvent event){
        if(!this.isAbilityEnabled()){
            return;
        }
        if(event.getPlayerWW().getRole().getKey().equals(RolesBase.TROUBLEMAKER.getKey())){
            if(!this.troublemakers.contains(event.getPlayerWW())){
                this.troublemakers.add(event.getPlayerWW());
            }
        }
    }

    public void addDeathPlayer(IPlayerWW playerWW){
        this.deaths.add(playerWW);
    }

}
