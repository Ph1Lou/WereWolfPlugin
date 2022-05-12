package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.grim_werewolf.GrimEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.GRIMY_WEREWOLF,
        category = Category.WEREWOLF, 
        attributes = {RoleAttribute.WEREWOLF})
public class GrimyWereWolf extends RoleWereWolf implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;
    private boolean hide = false;

    public GrimyWereWolf(WereWolfAPI main, IPlayerWW playerWW) {
        super(main, playerWW);
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
                .setDescription(game.translate("werewolf.role.grimy_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @Override
    public void recoverPower() {
        if (!game.getConfig().isTrollSV()) {
            game.getConfig().addOneRole(RoleBase.WEREWOLF);
        }
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        if (this.power) {
            game.getConfig().removeOneRole(RoleBase.WEREWOLF);
            this.power = false;
        } else if (!this.affectedPlayer.isEmpty()) {
            game.getConfig().removeOneRole(this.affectedPlayer.get(0).getRole().getKey());
            Bukkit.broadcastMessage(game.translate(Prefix.GREEN , "werewolf.role.grimy_werewolf.actualize",
                    Formatter.role(game.translate(this.affectedPlayer.get(0).getRole().getKey()))));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathAnnounce(AnnouncementDeathEvent event) {

        if (this.affectedPlayer.isEmpty()) {
            return;
        }

        if (event.getPlayerWW().equals(this.affectedPlayer.get(0))) {
            event.setRole(RoleBase.WEREWOLF);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnnounceDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().getLastKiller().isPresent()) return;

        if (!event.getPlayerWW().getLastKiller().get().equals(this.getPlayerWW())) return;

        if (!this.power) return;

        if (!isAbilityEnabled()) return;

        this.power = false;

        GrimEvent grimEvent = new GrimEvent(this.getPlayerWW(), event.getPlayerWW());
        Bukkit.getPluginManager().callEvent(grimEvent);

        if (grimEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN , "werewolf.role.grimy_werewolf.perform",
                Formatter.player(event.getPlayerWW().getName()),
                Formatter.role(game.translate(event.getPlayerWW().getRole().getKey())));

        game.getConfig().removeOneRole(RoleBase.WEREWOLF);
        this.affectedPlayer.add(event.getPlayerWW());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {

        if (this.affectedPlayer.isEmpty()) {
            return;
        }

        if (event.getReason() != UpdateCompositionReason.DEATH) {
            return;
        }

        if (!event.getKey().equals(this.affectedPlayer.get(0).getRole().getKey())) {
            return;
        }

        if (this.hide) {
            return;
        }
        this.hide = true;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        if (!this.affectedPlayer.contains(playerWW)) return;

        if (game.getConfig().isConfigActive(ConfigBase.SHOW_ROLE_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(game.translate(playerWW.getRole().getKey()),
                            "")
                    + game.translate(RoleBase.WEREWOLF));
        } else if (game.getConfig().isConfigActive(ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(game.translate(playerWW.getRole().getCamp().getKey()),
                            "")
                    + game.translate(Camp.WEREWOLF.getKey()));
        }

    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }
}
