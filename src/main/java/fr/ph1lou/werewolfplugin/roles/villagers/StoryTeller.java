package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.actionablestory.ActionableStoryEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.storyteller.StoryTellerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.STORY_TELLER,
        category = Category.VILLAGER, attributes = {RoleAttribute.VILLAGER,
        RoleAttribute.INFORMATION})
public class StoryTeller extends RoleVillage {

    private final Set<IPlayerWW> players = new HashSet<>();

    public StoryTeller(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @EventHandler
    public void onActionableStory(ActionableStoryEvent event){
        game.getPlayerWW(event.getPlayer()).ifPresent(players::add);
    }

    @EventHandler
    public void onDay(DayEvent event){

        if(event.getNumber()>=5){

            if(this.getPlayerWW().isState(StatePlayer.ALIVE)){


                if(this.isAbilityEnabled()){
                    if(this.players.size() > 0){

                        StoryTellerEvent storyTellerEvent = new StoryTellerEvent(this.getPlayerWW(), this.players);

                        Bukkit.getPluginManager().callEvent(storyTellerEvent);

                        if(storyTellerEvent.isCancelled()){
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                        }
                        else{
                            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                                    "werewolf.role.story_teller.players",
                                    Formatter.format("&players&", this.players.stream()
                                            .map(IPlayerWW::getName)
                                            .collect(Collectors.joining(", "))));
                        }
                    }
                }
            }
        }

        this.players.clear();
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.story_teller.description"))
                .setItems(game.translate("werewolf.role.story_teller.items"))
                .build();
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    @Override
    public void recoverPower() {

    }
}
