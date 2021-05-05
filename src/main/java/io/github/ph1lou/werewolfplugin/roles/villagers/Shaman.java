package io.github.ph1lou.werewolfplugin.roles.villagers;

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
        return null;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (!event.getTargetPlayer().equals(getPlayerUUID())) {
            return;
        }
        event.setCancelled(true);

        String deathMessage = game.translate("death_message_with_role");
        deathMessage = deathMessage.replace("&player&",
                event.getPlayerWW().getName());
        deathMessage = deathMessage.replace("&role&",
                game.translate(event.getPlayerWW().getRole().getDeathRole()));

        getPlayerWW().sendMessage(deathMessage);
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
