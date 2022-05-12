package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatPrefixEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.ALPHA_WEREWOLF,
        category = Category.WEREWOLF,
        attributes = {RoleAttribute.WEREWOLF})
public class AlphaWereWolf extends RoleWereWolf {

    public AlphaWereWolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.alpha_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate("werewolf.role.alpha_werewolf.effect"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) {
            if (!this.isAbilityEnabled()) {
                return;
            }
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.ABSORPTION, 6000, 0,"alpha-werewolf"));
        }
    }

    @EventHandler
    public void onNight(NightEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }
        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.ABSORPTION, 6000, 0,"alpha-werewolf"));
    }

    @EventHandler
    public void onChat(WereWolfChatPrefixEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        if (!event.getRequester().getRole().isWereWolf()) return;

        event.setPrefix("werewolf.role.alpha_werewolf.prefix");

        event.addFormatter(Formatter.format("&alpha&",this.getPlayerWW().getName()));
    }
}
