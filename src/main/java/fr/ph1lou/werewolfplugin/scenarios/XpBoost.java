package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExpEvent;

@Scenario(key = ScenarioBase.XP_BOOST,
        defaultValue = true,
        configValues = @IntValue(key = IntValueBase.XP_BOOST,
                defaultValue = 500,
                meetUpValue = 500,
                step = 10, item = UniversalMaterial.EXPERIENCE_BOTTLE))
public class XpBoost extends ListenerWerewolf {


    public XpBoost(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXp(BlockExpEvent event) {
        WereWolfAPI game = this.getGame();
        event.setExpToDrop((int) (event.getExpToDrop() *
                game.getConfig().getValue(IntValueBase.XP_BOOST) / 100f));
    }
}
