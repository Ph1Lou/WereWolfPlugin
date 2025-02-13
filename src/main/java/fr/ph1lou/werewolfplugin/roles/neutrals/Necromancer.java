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
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.necromancer.NecromancerResurrectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Role(key = RoleBase.NECROMANCER,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        sharpnessIronModifier = 1,
        sharpnessDiamondModifier = 1,
        configValues = { @IntValue(key = IntValueBase.NECROMANCER_DISTANCE,
                defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.BLACK_WOOL) })
public class Necromancer extends RoleNeutral implements IProgress, IAffectedPlayers {

    @Nullable
    private IPlayerWW playerWW;
    private float progress = 0;
    private int health = 0;
    private final List<IPlayerWW> markedPlayers = new ArrayList<>();

    public Necromancer(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.necromancer.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.NECROMANCER_DISTANCE))))
                .setPower(game.translate(
                        "werewolf.roles.necromancer.power_enable",
                        Formatter.number((int) this.getProgress())))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void OnFirstDeath(SecondDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.playerWW != null) {
            return;
        }

        if (!this.getAffectedPlayers().contains(event.getPlayerWW())) {
            return;
        }

        if (event.getPlayerWW().getDeathLocation()
                    .distance(this.getPlayerWW().getLocation())
            > game.getConfig().getValue(IntValueBase.NECROMANCER_DISTANCE)) {
            return;
        }

        NecromancerResurrectionEvent necromancerResurrectionEvent =
                new NecromancerResurrectionEvent(this.getPlayerWW(), event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(necromancerResurrectionEvent);

        if (necromancerResurrectionEvent.isCancelled()) {
            this.getPlayerWW()
                    .sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.playerWW = event.getPlayerWW();

        this.clearAffectedPlayer();

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));

        this.playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.necromancer.resurrection");

        event.setCancelled(true);

        game.resurrection(event.getPlayerWW());

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.necromancer.perform",
                Formatter.player(event.getPlayerWW().getName()));
    }

    @EventHandler
    public void onDeadAliveDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(this.playerWW)) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (event.getPlayerWW().getLastKiller().isPresent()) {

            IPlayerWW killer = event.getPlayerWW().getLastKiller().get();

            if (killer.equals(getPlayerWW())) {
                return;
            }

            this.playerWW = killer;

            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.necromancer.new_victim",
                    Formatter.player(this.playerWW.getName()));

        } else {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.necromancer.pve");
            this.playerWW = null;
        }
        this.progress = 0;

    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.health >= 7) {
            return;
        }

        if (this.playerWW == null) {

            int distanceConfig = game.getConfig().getValue(IntValueBase.NECROMANCER_DISTANCE);

            game.getAlivePlayersWW()
                    .stream()
                    .filter(playerWW1 -> !playerWW1.equals(getPlayerWW()))
                    .filter(iPlayerWW -> iPlayerWW.distance(getPlayerWW()) < distanceConfig)
                    .forEach(iPlayerWW -> {
                        if (!this.getAffectedPlayers().contains(iPlayerWW)) {
                            this.addAffectedPlayer(iPlayerWW);
                            BukkitUtils.scheduleSyncDelayedTask(game, () -> this.removeAffectedPlayer(iPlayerWW), 5 * 60 * 20);
                            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
                        }
                    });

            return;
        }

        if (!this.playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        double distance = this.playerWW.getLocation()
                .distance(this.getPlayerWW().getLocation());

        if (distance < 15) {
            this.progress += 4;
        } else if (distance < 45) {
            this.progress += 3;
        } else if (distance < 75) {
            this.progress += 2;
        } else if (distance < 100) {
            this.progress += 1;
        }

        if (this.progress >= 600) {
            this.progress = 0;
            health++;
            this.playerWW.removePlayerMaxHealth(2);
            this.getPlayerWW().addPlayerMaxHealth(2);
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.necromancer.steal",
                    Formatter.player(this.playerWW.getName()));
            this.playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.necromancer.necromancer_steal");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNameTagUpdate(UpdatePlayerNameTagEvent event) {

        if (!event.getTargetUUID().equals(getPlayerUUID())) {
            return;
        }

        game.getPlayerWW(event.getPlayerUUID()).ifPresent(iPlayerWW -> {
            if (this.getAffectedPlayers().contains(iPlayerWW)) {
                event.setSuffix(" " + game.translate("werewolf.roles.necromancer.mark") + " " + event.getSuffix());
            }
        });
    }


    @EventHandler
    public void onActionBar(ActionBarEvent event) {

        if (!this.game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();

        if (!getPlayerUUID().equals(uuid)) {
            return;
        }

        IPlayerWW playerWW = this.game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        StringBuilder sb = event.getActionBar();

        if (this.playerWW != null) {
            sb
                    .append(" ")
                    .append(game.translate("werewolf.roles.necromancer.progress", Formatter.number((int) this.progress)))
                    .append(" ");
        }
    }

    @Override
    public float getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.markedPlayers.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.markedPlayers.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.markedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.markedPlayers;
    }
}
