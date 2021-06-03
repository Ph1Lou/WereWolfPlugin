package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class MysticalWereWolf extends RoleWereWolf {

    public MysticalWereWolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.mystical_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessage(WereWolfChatEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.getPlayerWW().sendMessageWithKey("werewolf.role.mystical_werewolf.no_message");
        event.setCancelled(true);
    }

    @Override
    protected void openWereWolfChat() {
    }

    @EventHandler
    public void onWereWolfDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        List<IPlayerWW> roles = game.getPlayersWW()
                .stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (roles.isEmpty()) return;

        IPlayerWW roleWW = roles.get((int) Math.floor(game.getRandom().nextFloat() * roles.size()));

        Bukkit.getPluginManager().callEvent(new MysticalWerewolfRevelationEvent(this.getPlayerWW(), roleWW));

        this.getPlayerWW().sendMessageWithKey("werewolf.role.mystical_werewolf.werewolf_death", roleWW.getName(), game.translate(roleWW.getRole().getKey()));
    }


}
