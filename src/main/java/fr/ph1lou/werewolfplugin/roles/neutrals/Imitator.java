package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.IMITATOR,
        auraDescriptionSpecialUseCase = "werewolf.roles.imitator.aura",
        defaultAura = Aura.NEUTRAL,
        category = Category.NEUTRAL,
        sharpnessIronModifier = 1,
        sharpnessDiamondModifier = 1,
        attribute = RoleAttribute.NEUTRAL)
public class Imitator extends RoleNeutral implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Imitator(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.imitator.description"))
                .setEffects(game.translate("werewolf.roles.imitator.effect"))
                .build();
    }


    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(
                UniversalPotionEffectType.SPEED,
                1200,
                0,
                this.getKey()));

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFirstDeathEvent(FirstDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getLastKiller().isPresent()) return;

        if (!playerWW.getLastKiller().get().equals(getPlayerWW())) return;

        if (!hasPower()) return;

        event.setCancelled(true);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (this.getPlayerWW().isState(StatePlayer.ALIVE)
                && hasPower()) {
                imitatorRecoverRole(playerWW);
            } else {
                BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(
                        new SecondDeathEvent(playerWW, event.getLastStrikers())), 20L);
            }
        }, 7 * 20);
    }


    public void imitatorRecoverRole(IPlayerWW playerWW) {

        IRole role = playerWW.getRole();

        setPower(false);

        IRole roleClone = role.publicClone();

        if (roleClone == null) {
            return;
        }
        HandlerList.unregisterAll(this);

        this.getPlayerWW().setRole(roleClone);

        BukkitUtils.registerListener(roleClone);
        if (this.isInfected()) {
            roleClone.setInfected();
        } else if (roleClone.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
        }

        roleClone.setTransformedToNeutral(true);
        this.getPlayerWW().addDeathRole(this.getKey());

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.thief.realized_theft",
                Formatter.role(game.translate(role.getKey())));
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.thief.details");

        this.getPlayerWW().clearPotionEffects(this.getKey());

        Bukkit.getPluginManager().callEvent(new StealEvent(this.getPlayerWW(),
                playerWW,
                roleClone.getKey()));

        if (!isAbilityEnabled()) {
            roleClone.disableAbilities();
        }
        roleClone.removeTemporaryAuras();

        roleClone.recoverPower();
        roleClone.recoverPotionEffects();

        if (this.isSolitary()) {
            roleClone.setSolitary(true);
        } else if (roleClone.isSolitary()) {
            if (this.getPlayerWW().getMaxHealth() < 30) {
                this.getPlayerWW().addPlayerMaxHealth(Math.max(0, Math.min(8, 30 - this.getPlayerWW().getMaxHealth())));
            }
        }

        for (int i = 0; i < playerWW.getLovers().size(); i++) {
            ILover lover = playerWW.getLovers().get(i);
            if (lover.swap(playerWW, getPlayerWW())) {
                this.getPlayerWW().addLover(lover);
                playerWW.removeLover(lover);
                i--;
            }
        }
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        game.death(playerWW);
    }


    @Override
    public void recoverPower() {
    }

    @Override
    public void recoverPotionEffect() {

        if (!this.power) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));

    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
    }
}
