package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.LimitedUse;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.classesroles.Progress;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.WhiteWereWolf;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.FalsifierWereWolf;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.events.UpdateEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fox extends RolesVillage implements Progress, LimitedUse, AffectedPlayers, Power {

    private float progress = 0;
    private int use = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Fox(GameManager game, UUID uuid) {
        super(game,uuid);
        setPower(false);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }


    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(Float progress) {
        this.progress = progress;
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

        if (getUse() < game.config.getUseOfFlair()) {
            setPower(true);
            player.sendMessage(game.translate("werewolf.role.fox.smell_message", game.config.getUseOfFlair() - getUse()));
        }
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.FOX;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.fox.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.fox.display");
    }

    @Override
    public void recoverPotionEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
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
        if(getAffectedPlayers().isEmpty()){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());
        UUID playerSmellUUID = getAffectedPlayers().get(0);
        PlayerLG plf = game.playerLG.get(playerSmellUUID);

        if (!plf.isState(State.ALIVE)) {
            return;
        }

        if(Bukkit.getPlayer(playerSmellUUID) == null){
            return;
        }

        Player flair = Bukkit.getPlayer(playerSmellUUID);
        Location renardLocation = player.getLocation();
        Location playerLocation = flair.getLocation();

        if (renardLocation.distance(playerLocation) > game.config.getDistanceFox()) {
            return;
        }

        float temp = getProgress() + 100f / (game.config.getTimerValues().get(TimerLG.FOX_SMELL_DURATION) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f / (game.config.getTimerValues().get(TimerLG.FOX_SMELL_DURATION) + 1)) {
            player.sendMessage(game.translate("werewolf.role.fox.progress", Math.min(100,Math.floor(temp))));
        }

        if (temp >= 100) {

            if (plf.getRole() instanceof FalsifierWereWolf && (!((FalsifierWereWolf) plf.getRole()).isPosterCamp(Camp.WEREWOLF) && !(((FalsifierWereWolf) plf.getRole()).getPosterRole() instanceof WhiteWereWolf))) {
                player.sendMessage(game.translate("werewolf.role.fox.not_werewolf", plf.getName()));
            } else if (game.roleManage.isWereWolf(plf)) {
                player.sendMessage(game.translate("werewolf.role.fox.werewolf", plf.getName()));
            } else {
                player.sendMessage(game.translate("werewolf.role.fox.not_werewolf", plf.getName()));
            }
            clearAffectedPlayer();
            setProgress(0f);
        }
    }
}
