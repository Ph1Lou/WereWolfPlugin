package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
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
        return Aura.LIGHT;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessage(WereWolfChatEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.mystical_werewolf.no_message");
        event.setCancelled(true);
    }

    @Override
    protected void openWereWolfChat() {
    }

    @EventHandler
    public void onWereWolfDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        if (!isAbilityEnabled()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.mystical_werewolf.ability_disabled");
            return;
        }

        List<IPlayerWW> roles = game.getPlayersWW()
                .stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (roles.isEmpty()) return;

        IPlayerWW roleWW = roles.get((int) Math.floor(game.getRandom().nextFloat() * roles.size()));

        Bukkit.getPluginManager().callEvent(new MysticalWerewolfRevelationEvent(this.getPlayerWW(), roleWW));

        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.mystical_werewolf.werewolf_death",
                Formatter.player(roleWW.getName()),
                Formatter.role(game.translate(roleWW.getRole().getKey())));
    }
}
