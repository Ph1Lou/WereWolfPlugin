package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.INTERPRETER,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Interpreter extends RoleImpl implements IPower {

    private final Set<Wrapper<IRole, Role>> roles = new HashSet<>();
    private boolean power = false;
    @Nullable
    private IRole role;

    public Interpreter(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
        List<Wrapper<IRole, Role>> roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> roleRegister.getMetaDatas().category() == Category.VILLAGER)
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .filter(roleRegister -> roleRegister.getMetaDatas().attribute() == RoleAttribute.INFORMATION)
                .collect(Collectors.toList());

        if (roles.size() == 0) {
            return;
        }

        Collections.shuffle(roles, game.getRandom());

        this.roles.add(roles.get(0));

        roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> !this.roles.contains(roleRegister))
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> roleRegister.getMetaDatas().attribute() == RoleAttribute.VILLAGER)
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .collect(Collectors.toList());

        if (roles.size() == 0) {
            return;
        }

        Collections.shuffle(roles, game.getRandom());

        this.roles.add(roles.get(0));

        roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> !this.roles.contains(roleRegister))
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> roleRegister.getMetaDatas().category() == Category.VILLAGER)
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .filter(roleRegister -> roleRegister.getMetaDatas().attribute() != RoleAttribute.INFORMATION)
                .filter(roleRegister -> roleRegister.getMetaDatas().attribute() != RoleAttribute.HYBRID)
                .collect(Collectors.toList());

        if (roles.size() == 0) {
            return;
        }

        Collections.shuffle(roles, game.getRandom());

        this.roles.add(roles.get(0));
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.interpreter.description"))
                .setPower(game.translate("werewolf.roles.interpreter.power",
                        Formatter.format("&roles&",
                                this.roles.stream().map(roleRegister -> game.translate(roleRegister.getMetaDatas().key()))
                                        .collect(Collectors.joining(", ")))))
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent nightEvent) {

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {

            if (!this.isAbilityEnabled()) {
                return;
            }

            if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
                return;
            }

            this.setPower(true);

            this.roles.forEach(roleRegister -> this.getPlayerWW()
                    .sendMessage(changeRole(roleRegister.getMetaDatas().key())));
        }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L * 4 / 5L);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        if (this.getPlayerWW().getRole().equals(this)) {
            return;
        }

        if(this.role == null){
            return;
        }

        HandlerList.unregisterAll(this.role);

        this.role = null;
        this.getPlayerWW().setRole(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().getRole().equals(this)) {
            return;
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.hasPower()) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.end_selection");

        this.setPower(false);

    }

    public boolean isRoleValid(String roleKey) {
        return this.roles.stream()
                .anyMatch(roleRegister -> roleRegister.getMetaDatas().key().equals(roleKey));
    }

    public void activateRole(String roleKey) {

        this.roles.removeIf(roleRegister -> {
            if (roleRegister.getMetaDatas().key().equals(roleKey)) {

                try {
                    this.role = roleRegister.getClazz()
                            .getConstructor(WereWolfAPI.class,
                                    IPlayerWW.class).newInstance(game,
                                    this.getPlayerWW());

                    if (this.isWereWolf()) {
                        role.setInfected();
                    }
                    if (this.isNeutral()) {
                        role.setTransformedToNeutral(true);
                    }

                    role.recoverPotionEffects();

                    BukkitUtils.registerListener(role);

                    HandlerList.unregisterAll(this);

                    this.getPlayerWW().setRole(role);

                    this.getPlayerWW().addDeathRole(this.getKey());

                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));

                    this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.interpreter.perform",
                            Formatter.role(game.translate(roleKey)));

                    BukkitUtils.scheduleSyncDelayedTask(game, () -> {

                        if (this.getPlayerWW().getRole().equals(this)) {
                            return; // Si interprete ne fait rien sinon redevient interprete
                        }

                        if (this.role == null) {
                            return;
                        }

                        if (!this.getPlayerWW().isState(StatePlayer.DEATH)) {

                            HandlerList.unregisterAll(this.role);
                            BukkitUtils.registerListener(this);

                            this.role = null;
                            this.getPlayerWW().clearPotionEffects(this.getPlayerWW().getRole().getKey());
                            this.getPlayerWW().setRole(this);
                            this.getPlayerWW().removeDeathRole(this.getKey());

                            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
                            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.interpreter.end");
                        }
                    }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L * 2);
                    return true;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ignored) {
                }

            }

            return false;
        });
    }

    @Override
    public void recoverPower() {

    }

    private TextComponent changeRole(String roleKey) {
        return VersionUtils.getVersionUtils().createClickableText(
                this.game.translate(Prefix.GREEN, "werewolf.roles.interpreter.click",
                Formatter.role(game.translate(roleKey))),
                String.format("/ww %s %s",
                        this.game.translate("werewolf.roles.interpreter.command"), roleKey),
                ClickEvent.Action.RUN_COMMAND,
                this.game.translate(roleKey)
                );
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
