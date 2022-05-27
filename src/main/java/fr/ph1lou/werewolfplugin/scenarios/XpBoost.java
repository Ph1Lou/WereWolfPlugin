package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExpEvent;

@Scenario(key = ScenarioBase.XP_BOOST,
        defaultValue = true,
        loreKey = {"werewolf.menu.advanced_tool.xp_lore",
                "werewolf.menu.shift"},
        configValues = @IntValue(key = XpBoost.KEY,
                defaultValue = 500,
                meetUpValue = 500,
                step = 10, item = UniversalMaterial.EXPERIENCE_BOTTLE))
public class XpBoost extends ListenerManager {

    public static final String KEY = "werewolf.menu.advanced_tool.xp";

    public XpBoost(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXp(BlockExpEvent event) {
        WereWolfAPI game = this.getGame();
        event.setExpToDrop((int) (event.getExpToDrop() *
                game.getConfig().getValue(KEY) / 100f));
    }
}
