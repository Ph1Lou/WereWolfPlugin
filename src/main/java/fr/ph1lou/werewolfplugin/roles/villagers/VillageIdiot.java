package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.RequirePower;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Role(key = RoleBase.VILLAGE_IDIOT,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        configValues = @IntValue(key = IntValueBase.VILLAGE_IDIOT_HEALTH_PENALTY,
                defaultValue = 4, meetUpValue = 4, step = 2, item = UniversalMaterial.RED_DYE))
public class VillageIdiot extends RoleImpl implements IPower {

    private boolean power = true;


    public VillageIdiot(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    public @NotNull String getDescription() {

        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.roles.village_idiot.description"))
                .setPower(this.game.translate(this.power ?
                        "werewolf.roles.village_idiot.power_on" :
                        "werewolf.roles.village_idiot.power_off"))
                .build();
    }

    public void recoverPower() {
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @EventHandler
    @RequireAlive
    public void onVote(VoteEvent event) {
        if (this.hasPower()) {
            return;
        }

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        event.setCancelled(true);

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.village_idiot.vote");
    }

    @EventHandler(priority = EventPriority.HIGH)
    @RequireAlive
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (this.power) return;

        event.setSuffix(event.getSuffix() + " " + this.game.translate("werewolf.roles.village_idiot.suffix"));
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @RequireAbility
    @RequirePower
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        Optional<IPlayerWW> killerWW = this.getPlayerWW().getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (killerWW.get().getRole().isWereWolf()) {
            return;
        }

        this.setPower(false);

        VillageIdiotEvent villageIdiotEvent =
                new VillageIdiotEvent(this.getPlayerWW(), killerWW.get());
        Bukkit.getPluginManager().callEvent(villageIdiotEvent);

        if (villageIdiotEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        this.game.resurrection(getPlayerWW());
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player != null) {
            Bukkit.getPluginManager().callEvent(
                    new UpdateNameTagEvent(player));
        }
        this.getPlayerWW().removePlayerMaxHealth(this.game.getConfig().getValue(IntValueBase.VILLAGE_IDIOT_HEALTH_PENALTY));
        event.setCancelled(true);
        Bukkit.broadcastMessage(this.game.translate(Prefix.YELLOW, "werewolf.roles.village_idiot.announce",
                Formatter.player(this.getPlayerWW().getName())));
    }
}

