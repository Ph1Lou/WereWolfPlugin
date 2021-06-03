package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTag;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.lovers.CursedLoverDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CursedLover implements ILover, Listener {

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
        return playerWW.equals(cursedLover1) ? cursedLover2 : cursedLover1;
    }

    public List<? extends IPlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(cursedLover1, cursedLover2));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        death = true;
        IPlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(
                new CursedLoverDeathEvent(event.getPlayerWW(), playerWW1));

        playerWW1.sendMessageWithKey("werewolf.role.cursed_lover.death_cursed_lover");

        playerWW1.removePlayerMaxHealth(2);

        game.getConfig().removeOneLover(LoverType.CURSED_LOVER.getKey());

        HandlerList.unregisterAll(this);
    }

    public void announceCursedLoversOnJoin(IPlayerWW playerWW) {

        if (cursedLover1.equals(playerWW)) {
            if (!power1) {
                playerWW.addPlayerMaxHealth(2);
            }
            power1 = true;
            playerWW.sendMessageWithKey("werewolf.role.cursed_lover.description", Sound.SHEEP_SHEAR,
                    cursedLover2.getName());
        } else if (cursedLover2.equals(playerWW)) {
            if (!power2) {
                playerWW.addPlayerMaxHealth(2);
            }
            power2 = true;
            playerWW.sendMessageWithKey("werewolf.role.cursed_lover.description", Sound.SHEEP_SHEAR,
                    cursedLover1.getName());
        }
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (death) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (getLovers().contains(playerWW)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTag event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) return;

        if (!getLovers().contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(ChatColor.BLACK).append(" ♥");

        event.setSuffix(sb.toString());
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!getLovers().contains(playerWW)) return;

        IPlayerWW playerWW1 = getOtherLover(playerWW);

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.end.cursed_lover",
                playerWW1.getName() + " "));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDetectVictoryCancel(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (death) return;

        event.setCancelled(true);
    }

    @Override
    public String getKey() {
        return LoverType.CURSED_LOVER.getKey();
    }

    @Override
    public boolean isAlive() {
        return !death;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (this.getLovers().contains(playerWW1)) return false;

        if (death) return false;

        if (cursedLover1.equals(playerWW)) {
            cursedLover1 = playerWW1;
            power1 = false;
        } else {
            cursedLover2 = playerWW1;
            power2 = false;
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
