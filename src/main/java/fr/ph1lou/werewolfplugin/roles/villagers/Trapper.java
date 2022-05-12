package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.TRAPPER,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Trapper extends RoleVillage implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Trapper(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        setPower(false);
    }

    private boolean power = true;

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW , "werewolf.role.trapper.tracking_message");
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.trapper.description"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (hasPower()) return;

        getAffectedPlayers()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .peek(playerWW -> stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" "))
                .forEach(playerWW -> stringBuilder.append(Utils.updateArrow(player, playerWW.getLocation())));

        event.setActionBar(stringBuilder.toString());
    }
}
