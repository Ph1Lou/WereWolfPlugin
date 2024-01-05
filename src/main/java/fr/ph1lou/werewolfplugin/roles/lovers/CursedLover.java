package fr.ph1lou.werewolfplugin.roles.lovers;

import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.CursedLoverDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.lovers.impl.LoverBaseImpl;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Lover(key = LoverBase.CURSED_LOVER, color = CursedLover.COLOR)
public class CursedLover extends LoverBaseImpl implements ILover, Listener {
    public static final String COLOR = "werewolf.lovers.cursed_lover.color";

    private final WereWolfAPI game;
    private IPlayerWW cursedLover1;
    private IPlayerWW cursedLover2;
    private boolean power1 = false;
    private boolean power2 = false;
    private boolean death = false;

    public CursedLover(WereWolfAPI game, IPlayerWW cursedLover1, IPlayerWW cursedLover2) {
        this.game = game;
        this.cursedLover1 = cursedLover1;
        this.cursedLover2 = cursedLover2;
        getLovers().forEach(playerWW -> playerWW.addLover(this));
    }

    public IPlayerWW getOtherLover(IPlayerWW playerWW) {
        return playerWW.equals(this.cursedLover1) ? this.cursedLover2 : this.cursedLover1;
    }

    public List<? extends IPlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(cursedLover1, cursedLover2));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (this.death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        this.death = true;
        IPlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(
                new CursedLoverDeathEvent(event.getPlayerWW(), playerWW1));

        playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.lovers.cursed_lover.death_cursed_lover");

        playerWW1.removePlayerMaxHealth(2);

        this.game.getConfig().removeOneLover(LoverBase.CURSED_LOVER);
    }

    public void announceCursedLoversOnJoin(IPlayerWW playerWW) {

        if (this.cursedLover1.equals(playerWW)) {
            if (!this.power1) {
                playerWW.addPlayerMaxHealth(2);
            }
            this.power1 = true;
            playerWW.sendMessageWithKey("werewolf.lovers.cursed_lover.description",
                    Formatter.player(cursedLover2.getName()));
            playerWW.sendSound(Sound.SHEEP_SHEAR);
        } else if (this.cursedLover2.equals(playerWW)) {
            if (!this.power2) {
                playerWW.addPlayerMaxHealth(2);
            }
            this.power2 = true;
            playerWW.sendMessageWithKey("werewolf.lovers.cursed_lover.description",
                    Formatter.player(cursedLover1.getName()));
            playerWW.sendSound(Sound.SHEEP_SHEAR);
        }
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (this.death) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (getLovers().contains(playerWW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!getLovers().contains(playerWW)) return;

        IPlayerWW playerWW1 = getOtherLover(playerWW);

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.end.cursed_lover",
                Formatter.player(playerWW1.getName() + " ")));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDetectVictoryCancel(WinConditionsCheckEvent event) {

        if (event.isWin()) return;

        if (this.death) return;

        event.setWin();
    }

    @Override
    public boolean isAlive() {
        return !this.death;
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (this.getLovers().contains(playerWW1)) return false;

        if (this.death) return false;

        if (this.cursedLover1.equals(playerWW)) {
            this.cursedLover1 = playerWW1;
            this.power1 = false;
        } else {
            this.cursedLover2 = playerWW1;
            this.power2 = false;
        }

        for (IPlayerWW playerWW2 : getLovers()) {
            announceCursedLoversOnJoin(playerWW2);
        }

        return true;
    }

    @Override
    public void second() {
    }


}
