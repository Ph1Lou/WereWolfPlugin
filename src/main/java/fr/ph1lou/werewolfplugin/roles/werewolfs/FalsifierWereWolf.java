package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import fr.ph1lou.werewolfapi.events.roles.falsifier_werewolf.NewDisplayRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.FALSIFIER_WEREWOLF,
        auraDescriptionSpecialUseCase = "werewolf.roles.falsifier_werewolf.aura",
        defaultAura = Aura.LIGHT,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class FalsifierWereWolf extends RoleWereWolf {


    public FalsifierWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        this.setDisplayCamp(Camp.VILLAGER.getKey());
        this.setDisplayRole(RoleBase.VILLAGER);
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isAbilityEnabled()) {
            this.setDisplayCamp(Camp.WEREWOLF.getKey());
            this.setDisplayRole(this.getKey());
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));
            return;
        }

        IPlayerWW displayWW = Utils.autoSelect(game, getPlayerWW());

        IRole roles = displayWW.getRole();
        NewDisplayRole newDisplayRole = new NewDisplayRole(this.getPlayerWW(), roles.getKey(), roles.getCamp().getKey());
        Bukkit.getPluginManager().callEvent(newDisplayRole);

        if (newDisplayRole.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            setDisplayCamp(Camp.WEREWOLF.getKey());
            setDisplayRole(RoleBase.FALSIFIER_WEREWOLF);
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));
        } else {
            setDisplayRole(roles.getKey());
            setDisplayCamp(newDisplayRole.getNewDisplayCamp());
            this.addAuraModifier(new AuraModifier(this.getKey(), roles.getDefaultAura(), 1, false));
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.falsifier_werewolf.display_role_message",
                Formatter.role(game.translate(getDisplayRole())));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.falsifier_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .addExtraLines(game.translate("werewolf.roles.falsifier_werewolf.role",
                        Formatter.role(game.translate(this.getDisplayRole()))))
                .build();
    }


    @Override
    public void recoverPower() {

    }
}
