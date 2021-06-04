package io.github.ph1lou.werewolfplugin.roles.lovers;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import io.github.ph1lou.werewolfapi.events.lovers.RevealAmnesiacLoversEvent;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AmnesiacLover implements ILover, Listener {

    private final WereWolfAPI game;
    private IPlayerWW amnesiacLover1;
    private IPlayerWW amnesiacLover2;
    private boolean find = false;
    private boolean death = false;

    public AmnesiacLover(WereWolfAPI game, IPlayerWW amnesiacLover1, IPlayerWW amnesiacLover2) {
        this.game = game;
        this.amnesiacLover1 = amnesiacLover1;
        this.amnesiacLover2 = amnesiacLover2;
        getLovers().forEach(playerWW -> playerWW.addLover(this));
    }

    public List<? extends IPlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(this.amnesiacLover1, this.amnesiacLover2));
    }

    public IPlayerWW getOtherLover(IPlayerWW playerWW) {
        return playerWW.equals(this.amnesiacLover1) ? this.amnesiacLover2 : this.amnesiacLover1;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        death = true;

        HandlerList.unregisterAll(this);

        if (!find) return;

        IPlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death",
                playerWW1.getName()));
        Bukkit.getPluginManager().callEvent(
                new AmnesiacLoverDeathEvent(event.getPlayerWW(), playerWW1));
        game.death(playerWW1);
        game.getConfig().removeOneLover(LoverType.AMNESIAC_LOVER.getKey());
    }


    @Override
    public void second() {

        if (this.find) return;

        if (this.death) return;

        Player player1 = Bukkit.getPlayer(this.amnesiacLover1.getUUID());
        Player player2 = Bukkit.getPlayer(this.amnesiacLover2.getUUID());

        if (player1 == null || player2 == null) return;

        if (!player1.getWorld().equals(player2.getWorld())) {
            return;
        }

        if (player1.getLocation().distance(player2.getLocation()) <
                this.game.getConfig().getDistanceAmnesiacLovers()) {

            Bukkit.getPluginManager().callEvent(new RevealAmnesiacLoversEvent(
                    Sets.newHashSet(this.amnesiacLover1, this.amnesiacLover2)));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.amnesiacLover1));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.amnesiacLover2));

            this.find = true;
            announceAmnesiacLoversOnJoin(this.amnesiacLover1);
            announceAmnesiacLoversOnJoin(this.amnesiacLover2);
            game.getConfig().addOneLover(LoverType.AMNESIAC_LOVER.getKey());
            game.checkVictory();

        }
    }

    public void announceAmnesiacLoversOnJoin(IPlayerWW playerWW) {

        if (!this.find) return;

        if (this.amnesiacLover1.equals(playerWW)) {
            playerWW.sendMessageWithKey("werewolf.role.lover.description", Sound.PORTAL_TRAVEL,
                    this.amnesiacLover2.getName());
        } else if (this.amnesiacLover2.equals(playerWW)) {
            playerWW.sendMessageWithKey("werewolf.role.lover.description", Sound.PORTAL_TRAVEL,
                    this.amnesiacLover1.getName());
        }
    }


    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTagEvent event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        IPlayerWW playerWW = this.game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) return;

        if (!getLovers().contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(ChatColor.DARK_PURPLE).append(" ♥");

        event.setSuffix(sb.toString());
    }

    private void buildActionbarLover(Player player, StringBuilder sb, List<IPlayerWW> list) {

        list
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getUUID().equals(player.getUniqueId()))
                .peek(playerWW -> sb.append(" §d♥ ")
                        .append(playerWW.getName())
                        .append(" "))
                .forEach(playerWW -> sb
                        .append(Utils.updateArrow(player,
                                playerWW.getLocation())));
    }

    @EventHandler
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if (!this.find) return;

        if (this.death) return;

        if (!this.game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();
        IPlayerWW playerWW = this.game.getPlayerWW(uuid).orElse(null);

        if (!getLovers().contains(playerWW)) return;

        StringBuilder sb = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (!this.find) {
            return;
        }

        buildActionbarLover(player,
                sb,
                new ArrayList<>(Arrays.asList(this.amnesiacLover1, this.amnesiacLover2)));

        event.setActionBar(sb.toString());

    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!getLovers().contains(playerWW)) return;

        IPlayerWW playerWW1 = getOtherLover(playerWW);

        StringBuilder sb = event.getEndMessage();

        sb.append(this.game.translate("werewolf.end.lover",
                playerWW1.getName() + " "));
    }

    @Override
    public String getKey() {
        return LoverType.AMNESIAC_LOVER.getKey();
    }

    @Override
    public boolean isAlive() {
        return !death;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    public boolean isRevealed() {
        return find;
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (this.getLovers().contains(playerWW1)) return false;

        if (this.death) return false;

        if (this.amnesiacLover1.equals(playerWW)) {
            this.amnesiacLover1 = playerWW1;
        } else {
            this.amnesiacLover2 = playerWW1;
        }


        for (IPlayerWW playerWW2 : getLovers()) {
            announceAmnesiacLoversOnJoin(playerWW2);
        }
        return true;
    }

    @EventHandler
    public void onAroundLoverEvent(AroundLoverEvent event) {

        if (this.death) return;

        for (IPlayerWW playerWW : event.getPlayerWWS()) {
            if (getLovers().contains(playerWW)) {
                event.addPlayer(getOtherLover(playerWW));
                break;
            }
        }
    }
}
