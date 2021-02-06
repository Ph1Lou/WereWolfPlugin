package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class FakeLover implements LoverAPI, Listener {

    private final List<PlayerWW> lovers;
    private final WereWolfAPI game;

    public FakeLover(WereWolfAPI game, List<PlayerWW> lovers) {
        this.game = game;
        this.lovers = lovers;
        announceLovers();
    }

    public List<? extends PlayerWW> getLovers() {
        return lovers;
    }


    public void announceLovers() {
        lovers.forEach(this::announceLovers);
    }

    public void announceLovers(PlayerWW playerWW) {

        if (!lovers.contains(playerWW)) return;

        StringBuilder couple = new StringBuilder();

        for (PlayerWW playerWW1 : lovers) {
            if (!playerWW.equals(playerWW1)) {
                couple.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessageWithKey("werewolf.role.lover.description", Sound.SHEEP_SHEAR, couple.toString());
    }


    @EventHandler
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if (!game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (!lovers.contains(playerWW)) return;

        StringBuilder sb = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        buildActionbarLover(player, sb, lovers);

        event.setActionBar(sb.toString());

    }

    private void buildActionbarLover(Player player, StringBuilder sb, List<PlayerWW> list) {

        list
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getUUID().equals(player.getUniqueId()))
                .peek(playerWW -> sb.append(" §d♥ ")
                        .append(playerWW.getName())
                        .append(" "))
                .forEach(playerWW -> sb
                        .append(game.getScore()
                                .updateArrow(player,
                                        playerWW.getLocation())));
    }

    @Override
    public String getKey() {
        return LoverType.CURSED_LOVER.getKey();
    } //pour desac le /don

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public boolean swap(PlayerWW playerWW, PlayerWW playerWW1) {
        return false;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

}
