package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.assassin.AssassinEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Role(key = RoleBase.ASSASSIN,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        sharpnessDiamondModifier = 1,
        sharpnessIronModifier = 1,
        protectionDiamondModifier = 1,
        protectionIronModifier = 1,
        powerModifier = 1)
public class Assassin extends RoleNeutral implements ILimitedUse {

    private int use = 0;
    private final Set<UUID> assassinated = new HashSet<>();

    public Assassin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public int getUse() {
        return this.use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));

    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!isAbilityEnabled()) return;

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (this.use >= 2) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW victim = event.getPlayerWW();

        Optional<IPlayerWW> killerWW = victim.getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().equals(this.getPlayerWW())) {
            return;
        }

        if (victim.equals(this.getPlayerWW())) {
            return;
        }

        if (!this.getPlayerWW().isState(fr.ph1lou.werewolfapi.enums.StatePlayer.ALIVE)) return;

        TextComponent assassinateMessage = VersionUtils.getVersionUtils().createClickableText(
                game.translate(Prefix.YELLOW, "werewolf.roles.assassin.assassinate_message",
                        Formatter.player(victim.getName())),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.assassin.command"),
                        victim.getUUID()),
                ClickEvent.Action.RUN_COMMAND);

        this.getPlayerWW().sendMessage(assassinateMessage);
    }

    public void assassinate(IPlayerWW victim) {

        AssassinEvent assassinEvent = new AssassinEvent(this.getPlayerWW(), victim);
        Bukkit.getPluginManager().callEvent(assassinEvent);

        if (assassinEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.use++;
        this.assassinated.add(victim.getUUID());

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.assassin.assassinate_perform",
                Formatter.player(victim.getName()),
                Formatter.role(game.translate(victim.getRole().getKey())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnouncementDeath(AnnouncementDeathEvent event) {

        if (this.assassinated.contains(event.getPlayerWW().getUUID())) {
            event.setCancelled(true);
        }
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.assassin.description"))
                .setItems(game.translate("werewolf.roles.assassin.items"))
                .setEffects(game.translate("werewolf.roles.assassin.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
    }
}
