package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Shaman extends RoleVillage implements IAffectedPlayers {

    public Shaman(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.shaman.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        IPlayerWW playerWW = event.getPlayerWW();

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }

        TextComponent textComponent = new TextComponent(
                 game.translate("werewolf.role.shaman.choice_message", playerWW.getName()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s %s",
                game.translate("werewolf.role.shaman.command"), playerWW.getUUID())));

        this.getPlayerWW().sendMessage(textComponent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (!event.getTargetPlayer().equals(getPlayerWW())) {
            return;
        }

        event.setFormat("werewolf.announcement.death_message_with_role");
        event.setRole(event.getPlayerWW().getRole().getKey());
        event.setPlayerName(event.getPlayerWW().getName());
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }
}
