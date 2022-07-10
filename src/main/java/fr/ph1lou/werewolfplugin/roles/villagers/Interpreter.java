package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.INTERPRETER,
        category = Category.VILLAGER, 
        attributes = RoleAttribute.VILLAGER)
public class Interpreter extends RoleVillage implements IPower {

    private boolean power = false;
    private final Set<Wrapper<IRole, Role>> roles = new HashSet<>();

    public Interpreter(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
        List<Wrapper<IRole, Role>> roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .anyMatch(roleAttribute -> roleAttribute == RoleAttribute.VILLAGER))
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .anyMatch(roleAttribute -> roleAttribute == RoleAttribute.INFORMATION))
                .collect(Collectors.toList());

        if(roles.size() == 0){
            return;
        }

        Collections.shuffle(roles, game.getRandom());

        this.roles.add(roles.get(0));

        roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> !this.roles.contains(roleRegister))
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .anyMatch(roleAttribute -> roleAttribute == RoleAttribute.VILLAGER))
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .noneMatch(roleAttribute -> roleAttribute == RoleAttribute.MINOR_INFORMATION))
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .noneMatch(roleAttribute -> roleAttribute == RoleAttribute.INFORMATION))
                .collect(Collectors.toList());

        if(roles.size() == 0){
            return;
        }

        Collections.shuffle(roles, game.getRandom());

        this.roles.add(roles.get(0));

        roles = Register.get().getRolesRegister()
                .stream()
                .filter(roleRegister -> !this.roles.contains(roleRegister))
                .filter(roleRegister -> !roleRegister.getMetaDatas().requireDouble())
                .filter(roleRegister -> !roleRegister.getMetaDatas().key().equals(RoleBase.INTERPRETER))
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .anyMatch(roleAttribute -> roleAttribute == RoleAttribute.VILLAGER))
                .filter(roleRegister -> game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()) == 0)
                .filter(roleRegister -> Arrays.stream(roleRegister.getMetaDatas().attributes())
                        .noneMatch(roleAttribute -> roleAttribute == RoleAttribute.INFORMATION))
                .collect(Collectors.toList());

        if(roles.size() == 0){
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
    public void onNight(NightEvent nightEvent){

        if(!this.getPlayerWW().getRole().equals(this)){
            return;
        }

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if(!this.isAbilityEnabled()){
                return;
            }

            if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
                return;
            }

            this.setPower(true);

            this.roles.forEach(roleRegister -> this.getPlayerWW()
                    .sendMessage(changeRole(roleRegister.getMetaDatas().key())));
        }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L * 4 / 5L);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(FinalDeathEvent event){

        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        if(this.getPlayerWW().getRole().equals(this)){
            return;
        }

        HandlerList.unregisterAll(this.getPlayerWW().getRole());
        this.getPlayerWW().setRole(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event){

        if(!this.getPlayerWW().getRole().equals(this)){
            return;
        }

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(!this.hasPower()){
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.end_selection");

        this.setPower(false);

    }

    public boolean isRoleValid(String roleKey){
        return this.roles.stream()
                .anyMatch(roleRegister -> roleRegister.getMetaDatas().key().equals(roleKey));
    }

    public void activateRole(String roleKey){

        this.roles.removeIf(roleRegister -> {
            if (roleRegister.getMetaDatas().key().equals(roleKey)) {

                try {
                    IRole role = roleRegister.getClazz()
                            .getConstructor(WereWolfAPI.class,
                                    IPlayerWW.class).newInstance(game,
                            this.getPlayerWW());

                    if(this.isWereWolf()){
                        role.setInfected();
                    }
                    if(this.isNeutral()){
                        role.setTransformedToNeutral(true);
                    }

                    role.recoverPotionEffects();

                    BukkitUtils.registerListener(role);

                    this.getPlayerWW().setRole(role);

                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));

                    BukkitUtils.scheduleSyncDelayedTask(() -> {

                        if (!game.isState(StateGame.END) && !this.getPlayerWW().isState(StatePlayer.DEATH)) {
                            HandlerList.unregisterAll(role);
                            this.getPlayerWW().clearPotionEffects(this.getPlayerWW().getRole().getKey());
                            this.getPlayerWW().setRole(this);

                            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
                            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,"werewolf.roles.interpreter.end");
                        }
                    }, (long) (game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20 * 1.6));

                    this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.interpreter.perform",
                            Formatter.role(game.translate(roleKey)));
                    return true;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
                }

            }

            return false;
        });
    }

    @Override
    public void recoverPower() {

    }

    private TextComponent changeRole(String roleKey) {
        TextComponent textComponent = new TextComponent(this.game.translate(Prefix.GREEN,"werewolf.roles.interpreter.click",
                Formatter.role(game.translate(roleKey))));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s %s",
                this.game.translate("werewolf.roles.interpreter.command"), roleKey)));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.game.translate(roleKey)).create()));
        return textComponent;
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
