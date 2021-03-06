package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatPrefixEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class AlphaWereWolf extends RoleWereWolf {

    public AlphaWereWolf(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
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
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.ABSORPTION, 6000, 0,"alpha-werewolf"));
        }
    }

    @EventHandler
    public void onNight(NightEvent event) {
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
