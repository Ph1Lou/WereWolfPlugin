package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanHowlingEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfHowlingEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.HOWLING_WEREWOLF, defaultAura = Aura.DARK, category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class HowlingWerewolf extends RoleWereWolf {

    private int power = 0;

    public HowlingWerewolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.howling_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onRequestHowling(WereWolfCanHowlingEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        if (this.power >= 3) {
            return;
        }

        event.setCanHowling(true);
        this.power++;
    }

    @EventHandler(ignoreCancelled = true)
    public void onHowling(WereWolfHowlingEvent event) {

        if (!event.getTargetWW().equals(this.getPlayerWW())) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.REGENERATION,
                7 * 20, 1, this.getKey()));

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.howling_werewolf.pseudo",
                Formatter.player(event.getPlayerWW().getName()));
    }
}
