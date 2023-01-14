package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.devoted_servant.DevotedServantEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;


@Role(key = RoleBase.DEVOTED_SERVANT,
           category = Category.VILLAGER,
           attributes = RoleAttribute.VILLAGER)
public class DevotedServant extends RoleVillage implements IPower, IAffectedPlayers {

    @Nullable
    private IPlayerWW playerWW;
    private boolean power = true;
    private boolean hide = false;

    public DevotedServant(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @EventHandler
    public void onAnnouncementDeath(AnnouncementDeathEvent event){
        if(!this.isAbilityEnabled()){
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if(this.playerWW == null){
            return;
        }

        if(this.playerWW.equals(event.getPlayerWW())){
            event.setRole(this.getKey());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {

        if(this.playerWW == null){
            return;
        }

        if(!event.getReason().equals(UpdateCompositionReason.DEATH)){
            return;
        }

        if(!event.getKey().equals(this.playerWW.getRole().getKey())){
            return;
        }

        if (this.hide) {
            return;
        }
        this.hide = true;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID())
                .orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        if(!playerWW.equals(this.playerWW)){
            return;
        }

        event.setSuffix(game.translate(this.getKey()));
    }

    @EventHandler
    public void onMaskedDeath(FirstDeathEvent event){

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.hasPower()){
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if(!event.getPlayerWW().getRole().isCamp(Camp.VILLAGER)){
            return;
        }

        if(event.getPlayerWW().getLastKiller().isPresent() &&
                (event.getPlayerWW().getLastKiller().get().equals(this.getPlayerWW())
                || event.getPlayerWW().getLastKiller().get().getRole().isKey(RoleBase.GRIMY_WEREWOLF))){
            return;
        }

        TextComponent resurrectionMessage = new TextComponent(
                game.translate(
                        Prefix.YELLOW , "werewolf.roles.devoted_servant.click"));
        resurrectionMessage.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.roles.devoted_servant.command"),
                                event.getPlayerWW().getUUID())));
        getPlayerWW().sendMessage(resurrectionMessage);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {

            if(!this.isAbilityEnabled()){
                return;
            }
            if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

            if(this.playerWW == null){
                return;
            }

            if(!this.playerWW.isState(StatePlayer.DEATH)){
                this.playerWW = null;
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,"werewolf.roles.devoted_servant.resurrection",
                        Formatter.player(event.getPlayerWW().getName()));
                return;
            }

            DevotedServantEvent devotedServantEvent = new DevotedServantEvent(this.getPlayerWW(), event.getPlayerWW());

            Bukkit.getPluginManager().callEvent(devotedServantEvent);

            this.setPower(false);

            if(devotedServantEvent.isCancelled()){
                this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                return;
            }

            IRole role = this.playerWW.getRole();
            HandlerList.unregisterAll(this);
            IRole roleClone = role.publicClone();
            this.getPlayerWW().setRole(roleClone);
            assert roleClone != null;
            BukkitUtils.registerListener(roleClone);
            if (this.isInfected()) {
                roleClone.setInfected();
            } else if (roleClone.isWereWolf()) {
                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
            }
            if(this.isSolitary()){
                roleClone.setSolitary(true);
            }
            this.getPlayerWW().addDeathRole(this.getKey());

            roleClone.removeTemporaryAuras();
            roleClone.recoverPower();
            roleClone.recoverPotionEffects();

            this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,
                    "werewolf.roles.devoted_servant.steal",
                    Formatter.player(event.getPlayerWW().getName()));
        },15*20);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.devoted_servant.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
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
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if(playerWW.equals(this.playerWW)){
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if(this.playerWW == null){
            return Collections.emptyList();
        }
        return Collections.singletonList(this.playerWW);
    }
}
