package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
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
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Role(key = RoleBase.INFECT, defaultAura = Aura.DARK, category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        weight = 1.5f,
        configurations = @Configuration(config = @ConfigurationBasic(key = ConfigBase.INFECT_AUTO_RESURRECTION)))
public class InfectFatherOfTheWolves extends RoleWereWolf implements IAffectedPlayers, IPower {

    private static final double CORRUPTION_DISTANCE = 20d;
    private static final double INFECT_CORRUPTION_RATE = 1.0 / 5;
    private static final double WOLF_CORRUPTION_RATE = 1.0 / 20;
    private static final double CORRUPTION_THRESHOLD = 100.0;

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final Map<UUID, Double> corruption = new HashMap<>();
    private boolean power = true;

    public InfectFatherOfTheWolves(WereWolfAPI api,
                                    IPlayerWW playerWW) {
        super(api, playerWW);
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

    public boolean isCorrupted(UUID playerUUID) {
        return this.corruption.getOrDefault(playerUUID, 0.0) >= CORRUPTION_THRESHOLD;
    }

    @Override
    public void second() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        IPlayerWW self = this.getPlayerWW();
        Location selfLoc = self.getLocation();

        List<IPlayerWW> wolves = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(self))
                .filter(p -> p.getRole().isWereWolf())
                .collect(Collectors.toList());

        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(self) || p.getRole().isWereWolf()) {
                continue;
            }

            double rate = 0;
            Location pLoc = p.getLocation();

            if (pLoc.getWorld() == selfLoc.getWorld() && pLoc.distance(selfLoc) < CORRUPTION_DISTANCE) {
                rate += INFECT_CORRUPTION_RATE;
            }

            for (IPlayerWW wolf : wolves) {
                Location wolfLoc = wolf.getLocation();
                if (pLoc.getWorld() == wolfLoc.getWorld() && pLoc.distance(wolfLoc) < CORRUPTION_DISTANCE) {
                    rate += WOLF_CORRUPTION_RATE;
                }
            }

            if (rate > 0) {
                double current = Math.min(this.corruption.getOrDefault(p.getUUID(), 0.0) + rate, CORRUPTION_THRESHOLD);
                this.corruption.put(p.getUUID(), current);
            }
        }
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.infect_father_of_the_wolves.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate(power ? "werewolf.roles.infect_father_of_the_wolves.power_available" : "werewolf.roles.infect_father_of_the_wolves.power_not_available"))
                .setItems(game.translate("werewolf.roles.infect_father_of_the_wolves.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();


        Optional<IPlayerWW> killerWW = playerWW.getLastKiller();


        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().getRole().isWereWolf()) {
            return;
        }

        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(this.autoResurrection());
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!this.isCorrupted(playerWW.getUUID())) return;

        TextComponent infectMessage = VersionUtils.getVersionUtils().createClickableText(
                game.translate(
                        Prefix.YELLOW, "werewolf.roles.infect_father_of_the_wolves.infection_message",
                        Formatter.player(playerWW.getName())),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.infect_father_of_the_wolves.command"),
                        playerWW.getUUID()),
                ClickEvent.Action.RUN_COMMAND
                );

        getPlayerWW().sendMessage(infectMessage);
    }

    private boolean autoResurrection() {

        if (!game.getConfig().isConfigActive(ConfigBase.INFECT_AUTO_RESURRECTION)) {
            return false;
        }

        InfectionEvent infectionEvent =
                new InfectionEvent(this.getPlayerWW(),
                        getPlayerWW());
        Bukkit.getPluginManager().callEvent(infectionEvent);
        setPower(false);

        if (!infectionEvent.isCancelled()) {
            game.resurrection(getPlayerWW());
            return true;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");

        return false;
    }
}
