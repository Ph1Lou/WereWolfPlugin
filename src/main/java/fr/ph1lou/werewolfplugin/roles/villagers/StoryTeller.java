package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.actionablestory.ActionableStoryEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.storyteller.StoryTellerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.STORY_TELLER,
        category = Category.VILLAGER, attribute = RoleAttribute.INFORMATION,
        configValues = @IntValue(key = IntValueBase.STORY_TELLER_DAY, defaultValue = 5, meetUpValue = 3, step = 1, item = UniversalMaterial.BED))
public class StoryTeller extends RoleImpl {

    private final Set<IPlayerWW> players = new HashSet<>();


    public StoryTeller(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @EventHandler
    public void onActionableStory(ActionableStoryEvent event) {
        players.add(event.getPlayer());
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (event.getNumber() >= game.getConfig().getValue(IntValueBase.STORY_TELLER_DAY)) {

            if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {

                if (this.isAbilityEnabled()) {

                    if (!this.players.isEmpty()) {

                        StoryTellerEvent storyTellerEvent = new StoryTellerEvent(this.getPlayerWW(), this.players);

                        Bukkit.getPluginManager().callEvent(storyTellerEvent);

                        if (storyTellerEvent.isCancelled()) {
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                        } else {
                            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                                    "werewolf.roles.story_teller.players",
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
                .setDescription(game.translate("werewolf.roles.story_teller.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.STORY_TELLER_DAY))))
                .setItems(game.translate("werewolf.roles.story_teller.items"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}
