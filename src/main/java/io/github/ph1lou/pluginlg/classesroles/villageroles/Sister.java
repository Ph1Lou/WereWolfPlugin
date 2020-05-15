package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.events.UpdateEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Sister extends RolesVillage{

    public Sister(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.SISTER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.sister.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.sister.display");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        Player sister1 = Bukkit.getPlayer(getPlayerUUID());
        Location location = sister1.getLocation();

        for(UUID uuid:game.playerLG.keySet()){
            PlayerLG plg = game.playerLG.get(uuid);
            if(!uuid.equals(getPlayerUUID())){
                if(plg.isState(State.ALIVE)){
                    if(plg.getRole() instanceof Sister){
                        if(Bukkit.getPlayer(uuid)!=null){

                            Player sister2 = Bukkit.getPlayer(uuid);
                            Location location2= sister2.getLocation();

                            if(location.distance(location2)<20){
                                sister1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
                                sister2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
                            }
                        }
                    }
                }
            }
        }
    }
}
