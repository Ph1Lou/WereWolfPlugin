package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Witch extends RolesVillage implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Witch(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public @NotNull String getDescription() {

        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.witch.description")) +
                game.translate("werewolf.description.power", (game.translate(game.getConfig().isConfigActive(ConfigsBase.AUTO_REZ_WITCH.getKey()) ? "werewolf.role.witch.himself" : "werewolf.role.witch.not_himself"))) +
                game.translate("werewolf.description.power", game.translate(power ? "werewolf.role.witch.power_available" : "werewolf.role.witch.power_not_available")) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.witch.items")) +
                game.translate("werewolf.description._");
    }


    @Override
    public void recoverPower() {

    }


    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW playerWW = event.getPlayerWW();


        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(autoResurrection(playerWW));
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        TextComponent textComponent =
                new TextComponent(
                        game.translate(
                                "werewolf.role.witch.resuscitation_message",
                                playerWW.getName()));
        textComponent.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s %s",
                        game.translate("werewolf.role.witch.command"),
                        playerWW.getUUID())));
        getPlayerWW().sendMessage(textComponent);
    }

    private boolean autoResurrection(PlayerWW player) {

        if (!game.getConfig().isConfigActive(ConfigsBase.AUTO_REZ_WITCH.getKey())) {
            return false;
        }

        WitchResurrectionEvent witchResurrectionEvent =
                new WitchResurrectionEvent(getPlayerWW(),
                        getPlayerWW());
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);
        setPower(false);

        if (!witchResurrectionEvent.isCancelled()) {
            game.resurrection(getPlayerWW());
            return true;
        }

        player.sendMessage(game.translate("werewolf.check.cancel"));

        return false;
    }

}
