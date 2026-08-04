package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.*;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IPowers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.role.utils.Powers;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Role(key = RoleBase.WITCH,
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        configurations = {@Configuration(config = @ConfigurationBasic(key = ConfigBase.WITCH_AUTO_RESURRECTION, defaultValue = true))})
public class Witch extends RoleImpl implements IPowers {

    public static final int OFFER_WINDOW = 5;
    public static final double ABSORPTION_REDUCTION = 2d;

    // Clés des pouvoirs nommés (servent aussi de clés de traduction du nom affiché
    // dans la commande admin role). Toujours garder en minuscules (cf. LanguageLoader).
    public static final String POWER_LIFE = "werewolf.roles.witch.powers.life";
    public static final String POWER_DEATH = "werewolf.roles.witch.powers.death";

    private final Powers powers = new Powers();

    private final Set<IPlayerWW> cursedKillers = new HashSet<>();
    private final Map<IPlayerWW, Integer> pendingOffers = new HashMap<>();

    public Witch(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        powers.add(POWER_LIFE).add(POWER_DEATH);
    }

    @Override
    public Powers getPowers() {
        return powers;
    }

    public void addCursedKiller(IPlayerWW killer) {
        this.cursedKillers.add(killer);
    }

    public boolean isOfferValid(IPlayerWW target) {
        Integer offerTime = this.pendingOffers.get(target);
        return offerTime != null && game.getTimer() - offerTime <= OFFER_WINDOW;
    }

    public void clearOffer(IPlayerWW target) {
        this.pendingOffers.remove(target);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.witch.description"))
                .setPower(game.translate("werewolf.roles.witch.power",
                        Formatter.format("&life&", game.translate(hasPower(POWER_LIFE)
                                ? "werewolf.roles.witch.potion_available"
                                : "werewolf.roles.witch.potion_used")),
                        Formatter.format("&death&", game.translate(hasPower(POWER_DEATH)
                                ? "werewolf.roles.witch.potion_available"
                                : "werewolf.roles.witch.potion_used"))))
                .setItems(game.translate("werewolf.roles.witch.items"))
                .addExtraLines(game.translate(game.getConfig().isConfigActive(ConfigBase.WITCH_AUTO_RESURRECTION)
                        ? "werewolf.roles.witch.himself"
                        : "werewolf.roles.witch.not_himself"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler(ignoreCancelled = true)
    @RequireAbility
    @RequireAlive
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.getRole().isInfected()) return;

        if (playerWW.equals(this.getPlayerWW())) {
            event.setCancelled(autoResurrection());
            return;
        }

        if (!hasPower(POWER_LIFE) && !hasPower(POWER_DEATH)) return;

        this.pendingOffers.put(playerWW, game.getTimer());

        TextComponent message = new TextComponent(game.translate(Prefix.YELLOW,
                "werewolf.roles.witch.death_message",
                Formatter.player(playerWW.getName())));

        if (hasPower(POWER_LIFE)) {
            TextComponent life = VersionUtils.getVersionUtils().createClickableText(
                    game.translate("werewolf.roles.witch.life_potion"),
                    String.format("/ww %s %s",
                            game.translate("werewolf.roles.witch.command_life"),
                            playerWW.getUUID()),
                    ClickEvent.Action.RUN_COMMAND);
            message.addExtra(life);
        }

        if (hasPower(POWER_DEATH) && playerWW.getLastKiller().isPresent()) {
            TextComponent death = VersionUtils.getVersionUtils().createClickableText(
                    game.translate("werewolf.roles.witch.death_potion"),
                    String.format("/ww %s %s",
                            game.translate("werewolf.roles.witch.command_death"),
                            playerWW.getUUID()),
                    ClickEvent.Action.RUN_COMMAND);
            message.addExtra(death);
        }

        this.getPlayerWW().sendMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @RequireAbility
    @RequireAlive
    public void onGoldenAppleEat(PlayerItemConsumeEvent event) {

        if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

        IPlayerWW eater = game.getPlayerWW(event.getPlayer().getUniqueId()).orElse(null);
        if (eater == null) return;
        if (!this.cursedKillers.contains(eater)) return;

        Player killer = Bukkit.getPlayer(eater.getUUID());
        if (killer == null) return;

        BukkitUtils.scheduleSyncDelayedTask(game, () ->
                VersionUtils.getVersionUtils().removePlayerAbsorptionHealth(killer, ABSORPTION_REDUCTION));
    }

    private boolean autoResurrection() {

        if (!game.getConfig().isConfigActive(ConfigBase.WITCH_AUTO_RESURRECTION)) {
            return false;
        }

        if (!hasPower(POWER_LIFE)) {
            return false;
        }

        WitchResurrectionEvent witchResurrectionEvent =
                new WitchResurrectionEvent(this.getPlayerWW(), this.getPlayerWW());
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if (witchResurrectionEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return false;
        }

        usePower(POWER_LIFE);
        game.resurrection(this.getPlayerWW());
        return true;
    }

    public Optional<IPlayerWW> getKillerOf(IPlayerWW target) {
        return target.getLastKiller();
    }
}
