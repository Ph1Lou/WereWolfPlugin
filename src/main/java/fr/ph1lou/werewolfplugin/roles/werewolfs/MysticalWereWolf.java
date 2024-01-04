package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.MYSTICAL_WEREWOLF,
        defaultAura = Aura.LIGHT,
        category = Category.WEREWOLF,
        attributes = RoleAttribute.WEREWOLF)
public class MysticalWereWolf extends RoleWereWolf {

    public MysticalWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mystical_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessage(WereWolfCanSpeakInChatEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.mystical_werewolf.no_message");
        event.setCanSpeak(false);
    }

    @EventHandler
    public void onWereWolfDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        if (!isAbilityEnabled()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.mystical_werewolf.ability_disabled");
            return;
        }

        List<IPlayerWW> roles = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isDisplayCamp(Camp.WEREWOLF.getKey()) ||
                                    (playerWW.getRole().getDisplayCamp().equals(playerWW.getRole().getCamp().getKey()) && !playerWW.getRole().isWereWolf()))
                .collect(Collectors.toList());

        if (roles.isEmpty()) return;

        IPlayerWW roleWW = roles.get((int) Math.floor(game.getRandom().nextFloat() * roles.size()));

        Bukkit.getPluginManager().callEvent(new MysticalWerewolfRevelationEvent(this.getPlayerWW(), roleWW));

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.mystical_werewolf.werewolf_death",
                Formatter.player(roleWW.getName()),
                Formatter.role(game.translate(roleWW.getRole().getKey())));
    }
}
