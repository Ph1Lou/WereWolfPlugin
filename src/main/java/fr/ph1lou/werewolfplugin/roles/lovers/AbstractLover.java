package fr.ph1lou.werewolfplugin.roles.lovers;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTagEvent;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public abstract class AbstractLover implements ILover {


    protected final List<IPlayerWW> lovers;
    protected final WereWolfAPI game;
    protected boolean death = false;

    public AbstractLover(WereWolfAPI game, List<IPlayerWW> lovers) {
        this.game = game;
        this.lovers = lovers;
        lovers.forEach(playerWW -> playerWW.addLover(this));
    }

    public List<? extends IPlayerWW> getLovers() {
        return lovers;
    }


    public void announceLovers() {
        lovers.forEach(this::announceLovers);
    }

    public void announceLovers(IPlayerWW playerWW) {

        if (this.death) return;

        if (!this.lovers.contains(playerWW)) return;

        StringBuilder couple = new StringBuilder();

        this.lovers.stream()
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .forEach(playerWW1 -> couple.append(playerWW1.getName()).append(" "));

        playerWW.sendMessageWithKey("werewolf.role.lover.description",
                Formatter.player(couple.toString()));
        playerWW.sendSound(Sound.SHEEP_SHEAR);
    }

    @EventHandler
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if (!this.game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (!lovers.contains(playerWW)) return;

        StringBuilder sb = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        buildActionbarLover(player, sb, lovers);

        event.setActionBar(sb.toString());

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
    public void onModeratorScoreBoard(UpdateModeratorNameTagEvent event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        IPlayerWW playerWW = this.game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) return;

        if (!this.lovers.contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(this.getLoverType().getChatColor()).append(" ♥");

        event.setSuffix(sb.toString());
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    public abstract LoverType getLoverType();

    @Override
    public void second() {
    }

    @Override
    public boolean isAlive() {
        return !this.death;
    }

    @Override
    public String getKey() {
        return this.getLoverType().getKey();
    }
}
