package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfplugin.roles.lovers.Lover;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.stud.StudLoverEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Role(key = RoleBase.STUD,
          category = Category.VILLAGER,
          attributes = RoleAttribute.HYBRID)
public class Stud extends RoleVillage implements IPower {
    private boolean power = true;

    public Stud(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.stud.description"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (power) return;

        game.getConfig().addOneLover(LoverType.LOVER.getKey()); //pour rajouter dans la compo le couple à la mort du tombeur (puisque le couple du tombeur est pas affiché dans la compo)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        Optional<IPlayerWW> killerWW = getPlayerWW().getLastKiller();

        if (!killerWW.isPresent()) return;

        if (!killerWW.get().isState(StatePlayer.ALIVE)) return;

        for (ILover lover : getPlayerWW().getLovers()) {
            if (lover.getLovers().contains(killerWW.get())) return;
        }

        Bukkit.getPluginManager().callEvent(new StudLoverEvent(getPlayerWW(), killerWW.get()));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(killerWW.get()));

        setPower(false);

        Lover lover = new Lover(game, new ArrayList<>(Arrays.asList(getPlayerWW(), killerWW.get())));

        game.getLoversManager().addLover(lover);
        BukkitUtils.registerListener(lover);
        lover.announceLovers();

        event.setCancelled(true);

        game.resurrection(getPlayerWW());
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
