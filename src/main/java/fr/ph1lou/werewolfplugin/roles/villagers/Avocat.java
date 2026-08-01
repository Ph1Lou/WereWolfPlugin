package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.honor.HonorChangeEvent;
import fr.ph1lou.werewolfapi.events.roles.avocat.AvocatDefenseEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.AVOCAT,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.LIGHT,
        configValues = {@IntValue(key = IntValueBase.AVOCAT_COOLDOWN,
                defaultValue = 600, meetUpValue = 600, step = 30, item = UniversalMaterial.CLOCK),
                @IntValue(key = IntValueBase.AVOCAT_DEFENSE_DURATION,
                        defaultValue = 600, meetUpValue = 600, step = 30, item = UniversalMaterial.PAPER)})
public class Avocat extends RoleImpl implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int lastUseTimer = -600;
    private IPlayerWW lastDefendedPlayer = null;
    private IPlayerWW defendedPlayer = null;
    private int preventedDecrease = 0;
    private boolean anyHonorChange = false;

    public Avocat(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    public boolean canDefend() {
        return game.getTimer() - this.lastUseTimer >= game.getConfig().getValue(IntValueBase.AVOCAT_COOLDOWN);
    }

    public int getRemainingCooldown() {
        return game.getConfig().getValue(IntValueBase.AVOCAT_COOLDOWN) - (game.getTimer() - this.lastUseTimer);
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
                int amount;
                if (!this.anyHonorChange) {
                    int honorDelta = defended.getHonor() - this.getPlayerWW().getHonor();
                    if (honorDelta != 0) {
                        this.getPlayerWW().modifyHonor(honorDelta);
                    }
                    amount = honorDelta;
                    this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                            "werewolf.roles.avocat.defense_end_exact",
                            Formatter.player(defended.getName()),
                            Formatter.number(defended.getHonor()));
                } else {
                    amount = this.preventedDecrease;
                    this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                            "werewolf.roles.avocat.defense_end_decrease",
                            Formatter.player(defended.getName()),
                            Formatter.number(this.preventedDecrease));
                }
                Bukkit.getPluginManager().callEvent(new AvocatDefenseEndEvent(this.getPlayerWW(), defended, amount));
            }
        }, game.getConfig().getValue(IntValueBase.AVOCAT_DEFENSE_DURATION) * 20L);
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
                .setDescription(game.translate("werewolf.roles.avocat.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.AVOCAT_COOLDOWN)),
                        Formatter.format("&number2&", game.getConfig().getValue(IntValueBase.AVOCAT_DEFENSE_DURATION))))
                .setCommand(game.translate("werewolf.roles.avocat.command"))
                .build();
    }

    @Override
    public void recoverPower() {
        this.lastUseTimer = -game.getConfig().getValue(IntValueBase.AVOCAT_COOLDOWN);
    }
}
