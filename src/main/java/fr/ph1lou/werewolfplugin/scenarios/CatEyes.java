package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalJoinEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;


@Scenario(key = ScenarioBase.CAT_EYES, meetUpValue = true)
public class CatEyes extends ListenerWerewolf {

    public CatEyes(WereWolfAPI api) {
        super(api);
    }

    @EventHandler
    private void onStartEvent(StartEvent event) {
        this.getGame()
                .getPlayersWW()
                .forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES)));
    }

    @EventHandler
    private void onFinalJoin(FinalJoinEvent event) {
        event.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES));
    }

    @Override
    public void register(boolean isActive) {


        if (isActive) {
            if (!isRegister()) {
                this.getGame().getPlayersWW().forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES)));
                BukkitUtils.registerListener(this);
                register = true;
            }
        } else if (isRegister()) {
            register = false;
            HandlerList.unregisterAll(this);

            this.getGame().getPlayersWW()
                    .forEach(playerWW -> playerWW
                            .addPotionModifier(
                                    PotionModifier.remove(UniversalPotionEffectType.NIGHT_VISION,
                                            ScenarioBase.CAT_EYES,
                                            0)));
        }
    }
}
