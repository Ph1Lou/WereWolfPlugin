package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Thief extends RoleNeutral implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Thief(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
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
                .setDescription(game.translate("werewolf.role.thief.description"))
                .setEffects(game.translate("werewolf.role.thief.effect"))
                .build();

    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void recoverPotionEffect() {

        if(!this.power) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,"thief"));

    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(
                PotionEffectType.SPEED,
                1200,
                0,
                "thief"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstDeathEvent(FirstDeathEvent event){

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getLastKiller().isPresent()) return;

        if (!playerWW.getLastKiller().get().equals(getPlayerWW())) return;

        if(!hasPower())return;

        event.setCancelled(true);

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                if (this.getPlayerWW().isState(StatePlayer.ALIVE)
                        && hasPower()) {
                    thiefRecoverRole(playerWW);
                } else {
                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if (!game.isState(StateGame.END)) {
                            Bukkit.getPluginManager().callEvent(
                                    new SecondDeathEvent(playerWW));
                        }

                    }, 20L);
                }
            }

        },7*20);
    }


    public void thiefRecoverRole(IPlayerWW playerWW) {

        IRole role = playerWW.getRole();

        setPower(false);
        HandlerList.unregisterAll(this.getPlayerWW().getRole());
        IRole roleClone = role.publicClone();
        this.getPlayerWW().setRole(roleClone);
        assert roleClone != null;
        BukkitUtils.registerEvents(roleClone);
        if (this.isInfected()) {
            roleClone.setInfected();
        } else if (roleClone.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
        }
        if(this.isSolitary()){
            roleClone.setSolitary(true);
        }
        roleClone.setDeathRole(this.getKey());

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.thief.realized_theft",
                Formatter.role(game.translate(role.getKey())));
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.thief.details");

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"thief",0));

        Bukkit.getPluginManager().callEvent(new StealEvent(this.getPlayerWW(),
                playerWW,
                roleClone.getKey()));

        if (!isAbilityEnabled()) {
            roleClone.disableAbilities();
        }
        roleClone.removeTemporaryAuras();

        roleClone.recoverPower();
        roleClone.recoverPotionEffects();

        for (int i = 0; i < playerWW.getLovers().size(); i++) {
            ILover lover = playerWW.getLovers().get(i);
            if (lover.swap(playerWW, getPlayerWW())) {
                this.getPlayerWW().addLover(lover);
                playerWW.removeLover(lover);
                i--;
            }
        }
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        game.death(playerWW);
    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"thief",0));
    }
}
