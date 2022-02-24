package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
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
                                    new SecondDeathEvent(playerWW, event.getLastStrikers()));
                        }

                    }, 20L);
                }
            }

        },7*20);
    }


    public void thiefRecoverRole(IPlayerWW playerWW) {

        IRole role = playerWW.getRole();

        this.setPower(false);
        HandlerList.unregisterAll(this);
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
        this.getPlayerWW().addDeathRole(this.getKey());

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
    public void disableAbilitiesRole() {

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"thief",0));
    }
}
