package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.classesroles.InvisibleState;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.MischievousWereWolf;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.events.DayWillComeEvent;
import io.github.ph1lou.pluginlg.events.NightEvent;
import io.github.ph1lou.pluginlg.events.UpdateEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.Day;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class LittleGirl extends RolesVillage implements InvisibleState {

    private boolean invisible = false;

    public LittleGirl(GameManager game, UUID uuid) {
        super(game,uuid);
    }


    @EventHandler
    public void onNight(NightEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor"));
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());


        if (!isInvisible()) return;

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        setInvisible(false);
        player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
        game.optionlg.updateNameTag();
    }


    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.little_girl.soon_to_be_day"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        if (!event.getUuid().equals(game.getGameUUID())) {
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!plg.isState(State.ALIVE)) {
            return;
        }


        if(!isInvisible()){
            return;
        }

        for(UUID uuid:game.playerLG.keySet()){

            PlayerLG plg2 = game.playerLG.get(uuid);

            if(Bukkit.getPlayer(uuid)!=null){
                Player player2 = Bukkit.getPlayer(uuid);
                if(!uuid.equals(getPlayerUUID())){
                    if(plg2.isState(State.ALIVE)){
                        if(plg2.getRole() instanceof LittleGirl || plg2.getRole() instanceof MischievousWereWolf){
                            InvisibleState rolePower2= (InvisibleState) plg2.getRole();

                            if(rolePower2.isInvisible()){
                                if (plg2.getRole().isCamp(Camp.WEREWOLF)) {
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                }
                                else{
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.LITTLE_GIRL;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.little_girl.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.little_girl.display");
    }

    @Override
    public void stolen(UUID uuid) {
        setInvisible(false);
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible=invisible;
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
    }
}
