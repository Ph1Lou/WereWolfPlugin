package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

 @Role(key = RoleBase.PROTECTOR,
         category = Category.VILLAGER,
         attributes = {RoleAttribute.VILLAGER})
public class Protector extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;


    public Protector(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {


        if (this.last != null) {


            this.last.addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"protector",0));
            this.last.getRole().removeAuraModifier("protection");
            this.last.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.protector.no_longer_protected");
            this.last = null;
        }

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW , "werewolf.role.protector.protection_message",
                Formatter.timer(Utils.conversion(
                        game.getConfig().getTimerValue(TimerBase.POWER_DURATION))));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStealProtection(StealEvent event) {

        if (this.last == null) return;

        if (!event.getThiefWW().equals(this.last)) return;

        this.last.addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,"protector"));
        this.last.getRole().addAuraModifier(new AuraModifier("protection", Aura.LIGHT, 40, true));
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.protector.description"))
                .setItems(game.translate("werewolf.role.protector.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (!playerWW.equals(last)) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);

    }
}