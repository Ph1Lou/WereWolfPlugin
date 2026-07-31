package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Role(key = RoleBase.MESQUIN_WEREWOLF,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        defaultAura = Aura.DARK)
public class MesquinWereWolf extends RoleWereWolf implements ILimitedUse {

    public static final int MAX_USES = 3;

    private int use = 0;
    private final Set<Location> riggedUrns = new HashSet<>();

    public MesquinWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
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

    public boolean rigUrn(Location urnLocation) {
        if (urnLocation == null) {
            return false;
        }
        if (use >= MAX_USES) {
            return false;
        }
        if (riggedUrns.contains(urnLocation)) {
            return false;
        }
        riggedUrns.add(urnLocation);
        use++;
        return true;
    }

    public boolean isUrnRigged(Location urnLocation) {
        return urnLocation != null && riggedUrns.contains(urnLocation);
    }

    public static boolean isAnyUrnRiggedAt(WereWolfAPI game, Location urnLocation) {
        if (urnLocation == null) {
            return false;
        }
        return game.getPlayersWW().stream()
                .map(IPlayerWW::getRole)
                .filter(MesquinWereWolf.class::isInstance)
                .map(MesquinWereWolf.class::cast)
                .anyMatch(mesquin -> mesquin.isUrnRigged(urnLocation));
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mesquin_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {
    }
}
