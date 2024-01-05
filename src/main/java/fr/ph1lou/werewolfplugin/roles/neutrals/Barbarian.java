package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Role(key = RoleBase.BARBARIAN,
        defaultAura = Aura.NEUTRAL,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        configValues = @IntValue(key = IntValueBase.BARBARIAN_DISTANCE,
                defaultValue = 25,
                meetUpValue = 25,
                step = 5,
                item = UniversalMaterial.GRAY_WOOL))
public class Barbarian extends RoleNeutral implements IPower, IAffectedPlayers {

    private final Set<IPlayerWW> damagedPlayers = new HashSet<>();
    @Nullable
    private IPlayerWW playerWW;
    private boolean power = true;

    public Barbarian(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.barbarian.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.BARBARIAN_DISTANCE))))
                .setPower(game.translate("werewolf.roles.barbarian.power"))
                .setItems(game.translate("werewolf.roles.barbarian.item"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onMaskedDeath(FirstDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.hasPower()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getPlayerWW().getDeathLocation().getWorld() !=
                this.getPlayerWW().getLocation().getWorld()) {
            return;
        }

        if (event.getPlayerWW().getDeathLocation().distance(this.getPlayerWW().getLocation())
                > game.getConfig().getValue(IntValueBase.BARBARIAN_DISTANCE)) {
            return;
        }

        TextComponent hideMessage = VersionUtils.getVersionUtils().createClickableText(
                game.translate(
                        Prefix.YELLOW, "werewolf.roles.barbarian.click_message",
                        Formatter.player(event.getPlayerWW().getName())),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.barbarian.command"),
                        event.getPlayerWW().getUUID()),
                ClickEvent.Action.RUN_COMMAND
        );
        getPlayerWW().sendMessage(hideMessage);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        if (event.getPlayerWW().equals(this.playerWW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaskedDeathAnnouncement(AnnouncementDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (event.getTargetPlayer().equals(this.getPlayerWW())) {
            return;
        }

        if (event.getPlayerWW().equals(this.playerWW)) {
            event.setFormat("werewolf.announcement.death_message");
        }
    }

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!game.isState(StateGame.GAME)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        Player striker;


        if (!(event.getDamager() instanceof Player)) {

            if (!(event.getDamager() instanceof Arrow)) return;

            ProjectileSource shooter = ((Arrow) event.getDamager()).getShooter();

            if (!(shooter instanceof Player)) return;

            striker = (Player) shooter;
        } else {
            striker = (Player) event.getDamager();
        }

        if (!striker.getUniqueId().equals(this.getPlayerUUID())) {
            return;
        }

        game.getPlayerWW(player.getUniqueId())
                .ifPresent(playerWW -> {
                    if (!this.damagedPlayers.contains(playerWW)) {
                        this.damagedPlayers.add(playerWW);
                        event.setDamage(event.getDamage() * 2);
                    }
                });
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if (playerWW.equals(this.playerWW)) {
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return Collections.singletonList(this.playerWW);
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
