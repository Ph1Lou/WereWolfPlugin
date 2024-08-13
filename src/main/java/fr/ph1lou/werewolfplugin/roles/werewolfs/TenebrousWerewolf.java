package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
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
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.TENEBROUS_WEREWOLF,
        auraDescriptionSpecialUseCase = "werewolf.roles.tenebrous_werewolf.aura",
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        timers = {@Timer(key = TimerBase.WEREWOLF_TENEBROUS_DURATION, defaultValue = 30, meetUpValue = 20)},
        configValues = {@IntValue(key = IntValueBase.TENEBROUS_WEREWOLF_DISTANCE,
                defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.BLACK_WOOL)})
public class TenebrousWerewolf extends RoleWereWolf implements IPower, IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power = true;

    public TenebrousWerewolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                .setDescription(game.translate("werewolf.roles.tenebrous_werewolf.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.TENEBROUS_WEREWOLF_DISTANCE)),
                        Formatter.format("&time&", game.getConfig().getTimerValue(TimerBase.WEREWOLF_TENEBROUS_DURATION))))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setCommand(game.translate("werewolf.roles.tenebrous_werewolf.description_command"))
                .setPower(game.translate(power ? "werewolf.roles.tenebrous_werewolf.power_available" : "werewolf.roles.tenebrous_werewolf.power_not_available"))
                .build();
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
            targetWW.addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.BLINDNESS, this.getKey(), 1));
            affectedPlayers.remove(targetWW);
        }
    }
}