package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Witch extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Witch(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.witch.description");
    }


    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW plg = game.getPlayersWW().get(event.getUuid());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;


        if (event.getUuid().equals(getPlayerUUID())) {
            event.setCancelled(autoResurrection(player));
            return;
        }

        if (!game.getPlayersWW()
                .get(getPlayerUUID())
                .isState(StatePlayer.ALIVE)) return;

        TextComponent textComponent =
                new TextComponent(
                        game.translate(
                                "werewolf.role.witch.resuscitation_message",
                                plg.getName()));
        textComponent.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s %s",
                        game.translate("werewolf.role.witch.command"),
                        event.getUuid())));
        player.spigot().sendMessage(textComponent);
    }

    private boolean autoResurrection(Player player) {

        if (!game.getConfig().getConfigValues()
                .get(ConfigsBase.AUTO_REZ_WITCH.getKey())) {
            return false;
        }

        WitchResurrectionEvent witchResurrectionEvent =
                new WitchResurrectionEvent(getPlayerUUID(),
                        getPlayerUUID());
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);
        setPower(false);

        if (!witchResurrectionEvent.isCancelled()) {
            game.resurrection(getPlayerUUID());
            return true;
        }

        player.sendMessage(game.translate("werewolf.check.cancel"));

        return false;
    }

}
