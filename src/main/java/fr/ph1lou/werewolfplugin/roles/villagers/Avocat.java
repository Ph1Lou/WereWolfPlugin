package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.honor.HonorChangeEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.AVOCAT,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.LIGHT)
public class Avocat extends RoleImpl implements IAffectedPlayers {

    private static final int COOLDOWN = 10 * 60;
    private static final int DEFENSE_DURATION = 10 * 60;

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int lastUseTimer = -COOLDOWN;
    private IPlayerWW lastDefendedPlayer = null;
    private IPlayerWW defendedPlayer = null;
    private int preventedDecrease = 0;
    private boolean anyHonorChange = false;

    public Avocat(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    public boolean canDefend() {
        return game.getTimer() - this.lastUseTimer >= COOLDOWN;
    }

    public int getRemainingCooldown() {
        return COOLDOWN - (game.getTimer() - this.lastUseTimer);
    }

    public boolean isLastDefended(IPlayerWW target) {
        return target.equals(this.lastDefendedPlayer);
    }

    public void defend(IPlayerWW target) {
        clearAffectedPlayer();
        this.defendedPlayer = target;
        this.lastDefendedPlayer = target;
        this.lastUseTimer = game.getTimer();
        this.preventedDecrease = 0;
        this.anyHonorChange = false;
        addAffectedPlayer(target);

        final IPlayerWW defended = target;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
          
            this.defendedPlayer = null;
            removeAffectedPlayer(defended);

            if (!this.getPlayerWW().isState(StatePlayer.DEATH)) {
                if (!this.anyHonorChange) {
                    int honorDelta = defended.getHonor() - this.getPlayerWW().getHonor();
                    if (honorDelta != 0) {
                        this.getPlayerWW().modifyHonor(honorDelta);
                    }
                    this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                            "werewolf.roles.avocat.defense_end_exact",
                            Formatter.player(defended.getName()),
                            Formatter.number(defended.getHonor()));
                } else {
                    this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                            "werewolf.roles.avocat.defense_end_decrease",
                            Formatter.player(defended.getName()),
                            Formatter.number(this.preventedDecrease));
                }
            }
        }, DEFENSE_DURATION * 20L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @RequireAbility
    @RequireAlive
    public void onHonorChange(HonorChangeEvent event) {
        if (this.defendedPlayer == null) return;

        IPlayerWW playerWW = event.getPlayerWW();
        if (!playerWW.equals(this.defendedPlayer)) return;

        int oldHonor = event.getOldHonor();
        int newHonor = playerWW.getHonor();
        this.anyHonorChange = true;

        if (newHonor < oldHonor) {
            this.preventedDecrease += (oldHonor - newHonor);
            event.setCancelled(true);
        }
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
        return this.affectedPlayer;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.avocat.description"))
                .setCommand(game.translate("werewolf.roles.avocat.command"))
                .build();
    }

    @Override
    public void recoverPower() {
    }
}
