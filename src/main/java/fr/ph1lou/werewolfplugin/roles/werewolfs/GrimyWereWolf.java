package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequirePower;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.grim_werewolf.GrimEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.GRIMY_WEREWOLF,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        defaultAura = Aura.LIGHT)
public class GrimyWereWolf extends RoleWereWolf implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;
    private boolean hide = false;
    private String grimedRoleKey = RoleBase.WEREWOLF;

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
                .setDescription(game.translate("werewolf.roles.grimy_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @Override
    public void recoverPower() {
        if (!game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) {
            game.getConfig().addOneRole(RoleBase.WEREWOLF);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @RequireAbility
    @RequirePower
    public void onSecondDeathEvent(SecondDeathEvent event) {

        IPlayerWW victim = event.getPlayerWW();

        Optional<IPlayerWW> killerWW = victim.getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().equals(this.getPlayerWW())) {
            return;
        }

        if (victim.equals(this.getPlayerWW())) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        TextComponent grimeMessage = VersionUtils.getVersionUtils().createClickableText(
                game.translate(Prefix.YELLOW, "werewolf.roles.grimy_werewolf.grime_message",
                        Formatter.player(victim.getName())),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.grimy_werewolf.command"),
                        victim.getUUID()),
                ClickEvent.Action.RUN_COMMAND);

        this.getPlayerWW().sendMessage(grimeMessage);
    }

    public void grime(IPlayerWW victim) {

        GrimEvent grimEvent = new GrimEvent(this.getPlayerWW(), victim);
        Bukkit.getPluginManager().callEvent(grimEvent);

        if (grimEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.power = false;

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.grimy_werewolf.perform",
                Formatter.player(victim.getName()),
                Formatter.role(game.translate(victim.getRole().getKey())));

        game.getConfig().removeOneRole(RoleBase.WEREWOLF);
        this.affectedPlayer.add(victim);

        List<IPlayerWW> aliveWolves = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (aliveWolves.isEmpty()) {
            this.grimedRoleKey = RoleBase.WEREWOLF;
        } else {
            this.grimedRoleKey = aliveWolves.get(game.getRandom().nextInt(aliveWolves.size())).getRole().getKey();
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
            Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.roles.grimy_werewolf.actualize",
                    Formatter.role(game.translate(this.affectedPlayer.get(0).getRole().getKey()))));
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathAnnounce(AnnouncementDeathEvent event) {

        if (this.affectedPlayer.isEmpty()) {
            return;
        }

        if (event.getPlayerWW().equals(this.affectedPlayer.get(0))) {
            event.setRole(this.grimedRoleKey);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSelfDeathAnnounce(AnnouncementDeathEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        if (!this.affectedPlayer.isEmpty()) {
            event.setRole(this.affectedPlayer.get(0).getRole().getKey());
        } else {
            event.setRole(this.getKey());
        }
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
                    + game.translate(this.grimedRoleKey));
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
}
