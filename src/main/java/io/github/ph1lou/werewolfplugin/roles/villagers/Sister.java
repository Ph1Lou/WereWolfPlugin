package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.SisterDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sister extends RolesVillage {

    public Sister(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.sister.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.sister.display";
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        Player sister1 = Bukkit.getPlayer(getPlayerUUID());
        Location location = sister1.getLocation();

        for(UUID uuid:game.getPlayersWW().keySet()){
            PlayerWW plg = game.getPlayersWW().get(uuid);
            if(!uuid.equals(getPlayerUUID())){
                if(plg.isState(State.ALIVE)){
                    if(plg.getRole().isDisplay("werewolf.role.sister.display")){
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

    @EventHandler
    public void onSisterDeath(FinalDeathEvent event){

        UUID uuid = event.getUuid();
        PlayerWW plg =game.getPlayersWW().get(uuid);
        PlayerWW plg2 = game.getPlayersWW().get(getPlayerUUID());

        if(uuid.equals(getPlayerUUID())){
            List<UUID> sisters =new ArrayList<>();
            for(UUID uuid1:game.getPlayersWW().keySet()){
                if(game.getPlayersWW().get(uuid1).getRole().isDisplay("werewolf.role.sister.display")){
                    sisters.add(uuid1);
                }
            }
            Bukkit.getPluginManager().callEvent(new SisterDeathEvent(uuid,sisters,plg.getLastKiller()));
            return;
        }

        if(!(plg.getRole().isDisplay("werewolf.role.sister.display"))){
            return;
        }

        if (!plg2.isState(State.ALIVE)) {
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.role.sister.reveal_killer", plg.getName(),plg.getLastKiller()!=null? game.getPlayersWW().get(plg.getLastKiller()).getName():game.translate("werewolf.utils.pve")));
        }
    }
}
