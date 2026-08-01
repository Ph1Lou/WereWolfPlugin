package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.BUILDINGS,
        defaultValue = true, meetUpValue = true,
        loreKey = "werewolf.configurations.buildings.description"),
        configurations = {
                @ConfigurationBasic(key = ConfigBase.BUILDING_COUNCIL_CHAMBER,
                        defaultValue = true, meetUpValue = true,
                        loreKey = "werewolf.configurations.buildings.council_chamber.description"),
                @ConfigurationBasic(key = ConfigBase.BUILDING_SANCTUARY,
                        defaultValue = true, meetUpValue = true,
                        loreKey = "werewolf.configurations.buildings.sanctuary.description")
        },
        configValues = {
                @IntValue(key = IntValueBase.BUILDINGS_RADIUS, defaultValue = 400,
                        meetUpValue = 250, step = 25, item = UniversalMaterial.ARROW),
                @IntValue(key = IntValueBase.BUILDINGS_VOTE_OFFICE_COUNT, defaultValue = 6,
                        meetUpValue = 4, step = 1, item = UniversalMaterial.PLAYER_HEAD),
                @IntValue(key = IntValueBase.BUILDINGS_COUNCIL_CHAMBER_COUNT, defaultValue = 3,
                        meetUpValue = 2, step = 1, item = UniversalMaterial.BLACK_WOOL),
                @IntValue(key = IntValueBase.BUILDINGS_DECORATIVE_COUNT, defaultValue = 10,
                        meetUpValue = 6, step = 1, item = UniversalMaterial.CRAFTING_TABLE),
                @IntValue(key = IntValueBase.BUILDINGS_SANCTUARY_COUNT, defaultValue = 4,
                        meetUpValue = 4, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.URN_CAPACITY, defaultValue = 5,
                        meetUpValue = 4, step = 1, item = UniversalMaterial.ANVIL)
        })
public class Buildings extends ListenerWerewolf {

    public Buildings(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRepartition(RepartitionEvent event) {
        ((GameManager) getGame()).getBuildingsManager().generate();
    }
}
