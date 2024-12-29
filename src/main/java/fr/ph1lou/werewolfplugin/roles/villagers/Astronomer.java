package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.ASTRONOMER,
        category = Category.VILLAGER,
        defaultAura = Aura.DARK,
        attribute = RoleAttribute.MINOR_INFORMATION,
        auraDescriptionSpecialUseCase = "werewolf.roles.astronomer.aura")
public class Astronomer extends RoleImpl implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;
    @Nullable
    private Location targetLocation;
    @Nullable
    private Location playerLocation;


    public Astronomer(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
        this.targetLocation = playerWW.getLocation();
        this.playerLocation = this.getPlayerWW().getLocation();
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

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.astronomer.description"))
                .setCommand(game.translate("werewolf.roles.astronomer.command_description"))
                .setEffects(game.translate("werewolf.roles.astronomer.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {
        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.NIGHT_VISION, this.getKey()));
    }

    @Override
    public void second() {

        if (this.hasPower()) {
            return;
        }

        if (this.playerLocation == null) {
            return;
        }

        if (this.targetLocation == null) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        Utils.sendParticleArrow(this.playerLocation, player, Utils.getAngle(this.playerLocation, this.targetLocation), 800);
    }

    @EventHandler
    public void onDay(DayEvent dayEvent) {
        this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));
    }

    @EventHandler
    public void onNight(NightEvent event) {
        this.addAuraModifier(new AuraModifier(this.getKey(), Aura.LIGHT, 1, false));
    }
}
