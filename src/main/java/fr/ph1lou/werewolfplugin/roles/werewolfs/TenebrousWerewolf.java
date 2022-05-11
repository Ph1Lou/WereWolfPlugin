package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.TENEBROUS_WEREWOLF, 
        category = Category.WEREWOLF, 
        attributes = {RoleAttribute.WEREWOLF},
        timers = {@Timer(key = TimerBase.WEREWOLF_TENEBROUS_DURATION, defaultValue = 30, meetUpValue = 20)},
        intValues = {@IntValue(key = "werewolf.role.tenebrous_werewolf.darkness_distance",
                defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.BLACK_WOOL)})
public class TenebrousWerewolf extends RoleWereWolf implements IPower, IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power = true;

    public TenebrousWerewolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.tenebrous_werewolf.description",
                        Formatter.format("&range&", game.getConfig().getValue(RoleBase.TENEBROUS_WEREWOLF, "werewolf.role.tenebrous_werewolf.darkness_distance")),
                        Formatter.format("&time&", game.getConfig().getTimerValue(TimerBase.WEREWOLF_TENEBROUS_DURATION))))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setCommand(game.translate("werewolf.role.tenebrous_werewolf.description_command"))
                .setPower(game.translate(power ? "werewolf.role.tenebrous_werewolf.power_available" : "werewolf.role.tenebrous_werewolf.power_not_available"))
                .build();
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player target = (Player) event.getEntity();
        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
        if (targetWW == null) return;

        if (!affectedPlayers.contains(targetWW)) return;

        Player damager = (Player) event.getDamager();
        IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);
        if (damagerWW == null) return;

        if (damagerWW.getRole().isWereWolf()) {
            targetWW.addPotionModifier(PotionModifier.remove(PotionEffectType.BLINDNESS, "tenebrous", 1));
            affectedPlayers.remove(targetWW);
        }
    }
}