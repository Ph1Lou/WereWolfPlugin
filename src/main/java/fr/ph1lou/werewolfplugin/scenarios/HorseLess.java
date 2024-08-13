package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.InvocationTargetException;
import static org.bukkit.Bukkit.getServer;

@Scenario(key = ScenarioBase.HORSE_LESS, defaultValue = true, meetUpValue = true)
public class HorseLess extends ListenerWerewolf {

    public HorseLess(WereWolfAPI main) {
        super(main);
    }


    @Override
    public void register(boolean isActive) {

        if (isActive) {
            if (!this.register) {
                this.registerListener();
                this.register = true;
            }
        } else if (this.register) {
            HandlerList.unregisterAll(this);
            this.register = false;
        }
    }
    private void registerListener(){
        try{
            int version = BukkitUtils.loadServerVersion();
            String className;
            if(version >= 21){
                className = "org.bukkit.event.entity.EntityMountEvent";
            }
            else {
                className = "org.spigotmc.event.entity.EntityMountEvent";
            }

            Class<? extends Event> oldEvent = Class.forName(className).asSubclass(Event.class);
            getServer().getPluginManager().registerEvent(oldEvent, this, EventPriority.NORMAL, (ignored, event) -> {

                try {
                    if (oldEvent.getMethod("getEntity").invoke(event) instanceof Player) {
                        if (oldEvent.getMethod("getMount").invoke(event) instanceof Horse) {
                            oldEvent.getMethod("setCancelled", boolean.class).invoke(event, true);
                        }
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }, JavaPlugin.getPlugin(Main.class));
        } catch (ClassNotFoundException | ClassCastException ignored){
        }
    }
}
