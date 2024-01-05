package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    @Role(key = RoleBase.WITNESS,
            category = Category.VILLAGER,
            attribute = RoleAttribute.MINOR_INFORMATION)
public class Witness extends RoleImpl implements IAffectedPlayers, IPower {

    public Witness(WereWolfAPI main, IPlayerWW playerWW) {
        super(main,playerWW);
    }

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    @Override
    public @NotNull String getDescription() {

        DescriptionBuilder descBuilder = new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.roles.witness.description"));
        if (affectedPlayer.isEmpty()) {
            if (power) {
                descBuilder.addExtraLines(game.translate("werewolf.roles.witness.culprit_unknown", Formatter.format("&time&",
                                Utils.conversion(game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST)))));
            } else {
                descBuilder.addExtraLines(game.translate("werewolf.roles.witness.culprit_dead"));
            }
        } else {
            descBuilder.addExtraLines(game.translate("werewolf.roles.witness.culprit_name", Formatter.format("&player&", affectedPlayer.get(0).getName())));
        }
        return descBuilder.build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.remove(iPlayerWW);
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
    public void setPower(boolean b) {
        this.power = b;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @EventHandler
    public void onWerewolfListEvent(WereWolfListEvent event){

        List<IPlayerWW> wolves = new ArrayList<>();
        for (IPlayerWW p : game.getPlayersWW()) {

            if(p.isState(StatePlayer.ALIVE) && p.getRole().isWereWolf()) {
                wolves.add(p);
            }
        }

        if (wolves.isEmpty()){
            return;
        }
        IPlayerWW culprit = wolves.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*wolves.size()));
        addAffectedPlayer(culprit);

        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witness.reveal_culprit", Formatter.format("&player&", culprit.getName()));

    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {
        IPlayerWW playerWW = event.getPlayerWW();
        if (!getAffectedPlayers().contains(playerWW)) return;
        if (getPlayerWW().isState(StatePlayer.DEATH)){
            if (!power) {
                return;
            }
        }

        removeAffectedPlayer(playerWW);
        this.power = false;

        getPlayerWW().removePlayerMaxHealth(8);
        getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.witness.culprit_death");
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event){
        IPlayerWW player = event.getTargetWW();
        IPlayerWW thief = event.getPlayerWW();

        if(!getAffectedPlayers().contains(player)) return;

        removeAffectedPlayer(player);
        addAffectedPlayer(thief);

        if(getPlayerWW().isState(StatePlayer.DEATH)) return;

        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witness.change", Formatter.format("&player&", thief.getName()));
    }

    @EventHandler
    public void onStealEvent(StealEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if(power) {
            getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.witness.reveal_culprit", Formatter.format("&player&", getAffectedPlayers().get(0).getName()));
        }
        else {
            getPlayerWW().removePlayerMaxHealth(8);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwap(SwapEvent event) {
        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (event.isCancelled()) return;

        if (!power) return;

        if (affectedPlayer.isEmpty()) return;

        if (affectedPlayer.contains(event.getPlayerWW1())) {
            removeAffectedPlayer(event.getPlayerWW1());
            addAffectedPlayer(event.getPlayerWW2());

            if (isAbilityEnabled()) {
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witness.change", Formatter.format("&player&", event.getPlayerWW2().getName()));
            }
        } else if (affectedPlayer.contains(event.getPlayerWW2())) {
            removeAffectedPlayer(event.getPlayerWW2());
            addAffectedPlayer(event.getPlayerWW1());

            if (isAbilityEnabled()) {
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witness.change", Formatter.format("&player&", event.getPlayerWW1().getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!hasPower()) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);

        if (damagerWW == null || !damagerWW.equals(getPlayerWW())) return;

        Player target = (Player) event.getEntity();
        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
        if (targetWW == null) return;

        if (!affectedPlayer.contains(targetWW)) return;

        event.setDamage(event.getDamage() * 0.7);
    }
}
