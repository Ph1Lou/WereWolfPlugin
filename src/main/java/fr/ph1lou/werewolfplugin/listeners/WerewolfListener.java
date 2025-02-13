package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfKillEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class WerewolfListener implements Listener {

    private final WereWolfAPI game;

    public WerewolfListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerDeathByWereWolf(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();
        IPlayerWW playerWW = game.getPlayerWW(killer.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        IRole iRole = playerWW.getRole();

        if (!iRole.isWereWolf()) return;

        if (!iRole.isAbilityEnabled()) return;

        Player victim = event.getEntity().getPlayer();

        if (victim == null) return;

        Optional<IPlayerWW> victimWW = game.getPlayerWW(victim.getUniqueId());
        if (victimWW.isPresent()) {
            WereWolfKillEvent killEvent = new WereWolfKillEvent(playerWW, victimWW.get());
            Bukkit.getPluginManager().callEvent(killEvent);
            if (killEvent.isCancelled()) {
                return;
            }
        }
        playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 1200, 0, RoleBase.WEREWOLF));
        playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 1200, 0, RoleBase.WEREWOLF));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRequestWereWolfList(RequestSeeWereWolfListEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.isState(StatePlayer.DEATH)) return;

        if (this.game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) <= 0) {
            event.setAccept(playerWW.getRole().isWereWolf());
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (event.getTargetWW().isState(StatePlayer.DEATH)) return;

        event.setAppear(event.getTargetWW().getRole().isWereWolf());
    }

    @EventHandler
    public void onNewWereWolf(NewWereWolfEvent event) {

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(event.getPlayerWW()));
        IRole iRole = event.getPlayerWW().getRole();

        if (iRole.isWereWolf()) { // Envoie le message seulement si vraiment loup
            event.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.werewolf.go_to_the_werewolf_camp");
            Sound.WOLF_HOWL.play(event.getPlayerWW());
            iRole.recoverPotionEffects();
        }

        this.game.getAlivePlayersWW().stream()
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(player1 -> {
                    player1.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.new_werewolf");
                    Sound.WOLF_HOWL.play(player1);
                });
    }

    @EventHandler
    public void onNightForWereWolf(NightEvent event) {

        game.getAlivePlayersWW().stream()
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(playerWW -> playerWW.getRole().isAbilityEnabled())
                .forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, RoleBase.WEREWOLF)));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onDayForWereWolf(DayEvent event) {
        game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, RoleBase.WEREWOLF, 0)));
    }

}
