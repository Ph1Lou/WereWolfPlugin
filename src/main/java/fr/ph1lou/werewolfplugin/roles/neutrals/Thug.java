package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.DeathItemsEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugRecoverGoldenAppleEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugRevealEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Role(key = RoleBase.THUG,
        defaultAura = Aura.NEUTRAL,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        configValues = {@IntValue(key = IntValueBase.THUG_DISTANCE,
                defaultValue = 25, meetUpValue = 25, step = 5, item = UniversalMaterial.GRAY_WOOL)})
public class Thug extends RoleNeutral implements IPower, IAffectedPlayers {

    private int probability = 10;
    private boolean power = false;
    private boolean power2 = false;
    @Nullable
    private IPlayerWW playerWW;
    @Nullable
    private IPlayerWW deathPlayerWithRoleRevealed;


    public Thug(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.roles.thug.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.THUG_DISTANCE))))
                .setEffects(game.translate("werewolf.roles.thug.effect"))
                .setPower(game.translate("werewolf.roles.thug.power", Formatter.number(this.probability)))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {

        this.setPower(true);

        if (this.playerWW != null) {
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.playerWW));
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.thug.command_message");

    }

    @Override
    public void recoverPotionEffect() {
        if (this.hasPower2()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.RESISTANCE, this.getKey()));
        }
    }

    @Override
    public void disableAbilitiesRole() {
        if (this.hasPower2()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.RESISTANCE, this.getKey(), 0));
        }
        if (this.playerWW != null) {
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.playerWW));
        }
    }

    @EventHandler
    public void onPlayerDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        event.getPlayerWW()
                .getLastKiller()
                .ifPresent(playerWW -> {
                    if (!playerWW.equals(this.getPlayerWW())) {
                        return;
                    }

                    if (!this.hasPower2()) {
                        this.setPower2(true);
                        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.thug.resistance");
                        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.RESISTANCE, this.getKey()));
                    }

                    if (!event.getPlayerWW().getPlayersKills().isEmpty()) {
                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,
                                "werewolf.roles.thug.new_heart",
                                Formatter.player(event.getPlayerWW().getName()));
                        this.getPlayerWW().addPlayerMaxHealth(2);
                    }

                    if (game.getRandom().nextInt(100) < probability) {
                        ThugRevealEvent thugRevealEvent = new ThugRevealEvent(playerWW);

                        Bukkit.getPluginManager().callEvent(thugRevealEvent);

                        if (!thugRevealEvent.isCancelled()) {
                            this.deathPlayerWithRoleRevealed = event.getPlayerWW();
                        }
                    }
                });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAnnouncement(AnnouncementDeathEvent event) {

        if (event.getPlayerWW().equals(this.deathPlayerWithRoleRevealed)) {
            event.setFormat("werewolf.roles.thug.death_message");
        }
    }

    @EventHandler
    public void onPlayerDeath(DeathItemsEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (event.getLocation().getWorld() != this.getPlayerWW().getLocation().getWorld()) {
            return;
        }

        if (event.getLocation().distance(this.getPlayerWW().getLocation()) > 25) {
            return;
        }

        int total = event.getItems()
                .stream()
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == UniversalMaterial.GOLDEN_APPLE.getType())
                .mapToInt(ItemStack::getAmount)
                .sum();

        if (total <= 1) {
            return;
        }

        ThugRecoverGoldenAppleEvent thugRecoverGoldenAppleEvent = new ThugRecoverGoldenAppleEvent(this.getPlayerWW(),
                event.getPlayerWW(),
                total / 2);

        Bukkit.getPluginManager().callEvent(thugRecoverGoldenAppleEvent);

        if (thugRecoverGoldenAppleEvent.isCancelled()) {
            return;
        }

        AtomicInteger temp = new AtomicInteger(total / 2);

        event.getItems()
                .stream()
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == UniversalMaterial.GOLDEN_APPLE.getType())
                .forEach(itemStack -> {
                    if (temp.get() > 0) {
                        if (itemStack.getAmount() > temp.get()) {
                            itemStack.setAmount(itemStack.getAmount() - temp.get());
                            temp.set(0);
                        } else {
                            temp.addAndGet(-itemStack.getAmount());
                            itemStack.setType(Material.COBBLESTONE);
                        }
                    }
                });

        probability += 10;

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.thug.get_apple",
                Formatter.format("&number1&", this.probability),
                Formatter.number(thugRecoverGoldenAppleEvent.getGoldenApple()),
                Formatter.player(event.getPlayerWW().getName()));
        this.getPlayerWW().addItem(new ItemStack(Material.GOLDEN_APPLE, thugRecoverGoldenAppleEvent.getGoldenApple()));
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public void setPower2(boolean power) {
        this.power2 = power;
    }

    public boolean hasPower2() {
        return this.power2;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = null;
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if (playerWW == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(this.playerWW);
    }

    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!this.isAbilityEnabled()) {
            return;
        }

        if (this.playerWW == null) {
            return;
        }

        if (!event.getPlayerUUID().equals(this.playerWW.getUUID())) {
            return;
        }

        if (this.hasPower()) {
            return;
        }

        event.setVisibility(false);
    }
}
