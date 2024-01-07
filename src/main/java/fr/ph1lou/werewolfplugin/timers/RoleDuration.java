package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.TrollEvent;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.roles.villagers.Villager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Timer(key = TimerBase.ROLE_DURATION,
        defaultValue = 20 * 60,
        meetUpValue = 60,
        decrement = true,
        onZero = RepartitionEvent.class)
public class RoleDuration extends ListenerWerewolf {


    public RoleDuration(WereWolfAPI main) {
        super(main);
    }


    private void roleAnnouncement(IPlayerWW playerWW) {

        IRole iRole = playerWW.getRole();
        Sound.EXPLODE.play(playerWW);
        playerWW.sendMessageWithKey("werewolf.description.description_message",
                Formatter.format("&description&", iRole.getDescription()));
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.announcement.review_role");

        iRole.recoverPotionEffects();
        iRole.recoverPower();

        if (this.getGame().getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        for (ItemStack i : getGame().getStuffs().getStuffRole(iRole.getKey())) {
            playerWW.addItem(i);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {

        GameManager game = (GameManager) this.getGame();

        game.setState(StateGame.GAME);

        List<IPlayerWW> playerWWS = new ArrayList<>(game.getPlayersWW());
        List<Wrapper<IRole, Role>> config = new ArrayList<>();

        game.getConfig().setConfig(ConfigBase.CHAT, false);

        game.getConfig().setRole(RoleBase.VILLAGER,
                Math.max(0,
                        game.getConfig()
                                .getRoleCount(RoleBase.VILLAGER) +
                                playerWWS.size() -
                                game.getTotalRoles()));

        Register.get().getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()); i++) {
                        config.add(roleRegister);
                    }
                });

        Collections.shuffle(playerWWS, game.getRandom());

        while (!playerWWS.isEmpty()) {
            IPlayerWW playerWW = playerWWS.remove(0);
            Wrapper<IRole, Role> roleRegister = config.remove(0);
            IRole role;
            try {
                role = roleRegister.getClazz().getConstructor(WereWolfAPI.class,
                        IPlayerWW.class).newInstance(game,
                        playerWW);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException exception) {
                exception.printStackTrace();
                role = new Villager(game, playerWW);
            }
            BukkitUtils.registerListener(role);
            playerWW.setRole(role);
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
        }
        game.getPlayersWW()
                .forEach(this::roleAnnouncement);

        BukkitUtils.scheduleSyncDelayedTask(game, game::checkVictory);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onRepartitionFirst(RepartitionEvent event) {
        if (this.getGame().getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) {
            Bukkit.getPluginManager().callEvent(new TrollEvent(this.getGame().getConfig().getTrollKey()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTroll(TrollEvent event) {

        GameManager game = (GameManager) this.getGame();

        game.setState(StateGame.GAME);
        game.getConfig().setConfig(ConfigBase.CHAT, false);

        Register.get().getRolesRegister()
                .forEach(roleRegister -> {

                    if (roleRegister.getMetaDatas().key().equals(event.getTrollKey())) {

                        game.getPlayersWW()
                                .forEach(playerWW -> {
                                    IRole role;
                                    try {
                                        role = roleRegister.getClazz().getConstructor(WereWolfAPI.class,
                                                        IPlayerWW.class)
                                                .newInstance(game,
                                                        playerWW);
                                    } catch (InstantiationException | InvocationTargetException |
                                             IllegalAccessException | NoSuchMethodException exception) {
                                        exception.printStackTrace();
                                        role = new Villager(game, playerWW);
                                    }
                                    BukkitUtils.registerListener(role);
                                    playerWW.setRole(role);
                                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                                });
                    }
                });


        game.getPlayersWW()
                .forEach(this::roleAnnouncement);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {

            game.getPlayersWW()
                    .forEach(playerWW -> {
                        HandlerList.unregisterAll(playerWW.getRole());
                        Sound.PORTAL_TRIGGER.play(playerWW);
                        playerWW.clearPotionEffects();
                        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.announcement.troll");
                        playerWW.addPlayerMaxHealth(20 - playerWW.getMaxHealth());
                    });
            game.getPlayersWW().forEach(IPlayerWW::clearLover);
            Iterator<? extends ILover> iterator = game.getLoversManager().getLovers().iterator();
            while (iterator.hasNext()) {
                HandlerList.unregisterAll(iterator.next());
                iterator.remove();
            }

            if (game.getConfig().isConfigActive(ConfigBase.DOUBLE_TROLL)) {
                Bukkit.getPluginManager().callEvent(new TrollEvent(game.getConfig().getTrollKey()));
                game.getConfig().switchConfigValue(ConfigBase.DOUBLE_TROLL);
                game.setDebug(false);
            } else {
                game.getConfig().setConfig(ConfigBase.TROLL_ROLE, false);
                Bukkit.getPluginManager().callEvent(new RepartitionEvent());
            }

        }, 1800L);
    }

}
