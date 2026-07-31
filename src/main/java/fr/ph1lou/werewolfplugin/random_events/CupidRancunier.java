package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RandomEvent(key = EventBase.CUPID_RANCUNIER,
        loreKey = "werewolf.random_events.cupid_rancunier.description")
public class CupidRancunier extends ListenerWerewolf {

    private static final int MIN_DAY = 5;

    private int day = 0;

    public CupidRancunier(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onDay(DayEvent event) {
        this.day = event.getNumber();
    }

    @EventHandler
    public void onLoverDeath(LoverDeathEvent event) {
        onCoupleDeath(new ArrayList<>(event.getLover().getLovers()));
    }

    @EventHandler
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {
        onCoupleDeath(Arrays.asList(event.getPlayerWW1(), event.getPlayerWW2()));
    }

    private void onCoupleDeath(List<IPlayerWW> deadCouple) {

        WereWolfAPI game = this.getGame();

        if (this.day < MIN_DAY) {
            return;
        }

        if (game.getPlayersWW().stream()
                .map(IPlayerWW::getRole)
                .anyMatch(role -> role.isKey(RoleBase.RIVAL))) {
            return;
        }

        IPlayerWW cupidWW = null;

        for (IPlayerWW playerWW : game.getPlayersWW()) {
            IRole role = playerWW.getRole();
            if (!role.isKey(RoleBase.CUPID)) continue;
            if (!playerWW.isState(StatePlayer.ALIVE)) continue;
            if (role.isSolitary()) continue;
            if (isOwnCouple(role, deadCouple)) {
                cupidWW = playerWW;
                break;
            }
        }

        if (cupidWW == null) {
            return;
        }

        IRole cupidRole = cupidWW.getRole();

        cupidWW.sendSound(Sound.AMBIENCE_THUNDER);

        cupidRole.setTransformedToVillager(false);
        cupidRole.setSolitary(true);
        cupidRole.addAuraModifier(new AuraModifier(cupidRole.getKey(), Aura.DARK, 1, false));

        cupidWW.sendMessageWithKey(Prefix.RED,
                "werewolf.random_events.cupid_rancunier.transform");

        register(false);
        game.checkVictory();
    }

    private boolean isOwnCouple(IRole role, List<IPlayerWW> deadCouple) {

        if (!(role instanceof IAffectedPlayers)) {
            return false;
        }

        List<? extends IPlayerWW> couple =
                ((IAffectedPlayers) role).getAffectedPlayers();

        return !couple.isEmpty() &&
                deadCouple.stream().anyMatch(dead -> couple.contains(dead));
    }
}
