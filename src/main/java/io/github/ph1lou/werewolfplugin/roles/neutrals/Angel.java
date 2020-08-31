package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Angel extends RolesNeutral implements AffectedPlayers, LimitedUse, AngelRole, Transformed {

    private int use = 0;
    private AngelForm choice=AngelForm.ANGEL;
    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean transformed=false;

    public Angel(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
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
    public boolean isChoice(AngelForm AngelForm) {
        return AngelForm==choice;
    }

    @Override
    public AngelForm getChoice() {
        return this.choice;
    }

    @Override
    public void setChoice(AngelForm choice) {
        this.choice = choice;
    }

    @Override
    public String getDescription() {
        if(choice.equals(AngelForm.FALLEN_ANGEL)) return game.translate("werewolf.role.fallen_angel.description");
        if(choice.equals(AngelForm.GUARDIAN_ANGEL)) return game.translate("werewolf.role.guardian_angel.description");
        return game.translate("werewolf.role.angel.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.angel.display";
    }

    @Override
    public void stolen(@NotNull UUID uuid) {

        getPlayerUUID();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (isChoice(AngelForm.ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration"))));
            VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 4);

        } else {
            if (!getAffectedPlayers().isEmpty()) {
                UUID targetUUID = getAffectedPlayers().get(0);
                PlayerWW target = game.getPlayersWW().get(targetUUID);

                if(target.isState(State.DEATH)) {
                    VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 4);
                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        if(target.getKillers().contains(getPlayerUUID())){
                            player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
                            VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 6);
                        }
                    }
                    else player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));


                } else {
                    VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 4);
                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target", target.getName()));
                    }
                    else {
                        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 6);
                        player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege", target.getName()));
                    }
                }
            }
        }
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player == null) return null;
        if (isChoice(AngelForm.ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration"))));
        }
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
        player.setHealth(24);
        return player;
    }


    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        getPlayerUUID();

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!plg.isState(State.ALIVE)) {
            return;
        }

        if (isChoice(AngelForm.ANGEL)) {

            if (game.getRandom().nextBoolean()) {
                if (player != null) {
                    player.sendMessage(game.translate("werewolf.role.angel.angel_choice_perform", game.translate("werewolf.role.fallen_angel.display")));
                }
                setChoice(AngelForm.FALLEN_ANGEL);
            } else {
                if (player != null) {
                    player.sendMessage(game.translate("werewolf.role.angel.angel_choice_perform", game.translate("werewolf.role.guardian_angel.display")));
                }
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(getPlayerUUID(), getChoice()));
        }

        UUID targetUUID = game.autoSelect(getPlayerUUID());
        addAffectedPlayer(targetUUID);
        PlayerWW target = game.getPlayersWW().get(targetUUID);

        if (player != null) {

            if (isChoice(AngelForm.FALLEN_ANGEL)) {
                player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target", target.getName()));
            } else {
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 6);
                player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege", target.getName()));
            }
            Sounds.PORTAL_TRIGGER.play(player);
        }

        Bukkit.getPluginManager().callEvent(new AngelTargetEvent(getPlayerUUID(),targetUUID));

        game.checkVictory();
    }




    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        StringBuilder sb = event.getEndMessage();
        if(isDisplay("werewolf.role.angel.display") && !isChoice(AngelForm.ANGEL)){
            sb.append(", ").append(game.translate("werewolf.role.angel.choice",game.translate(isChoice(AngelForm.ANGEL)?"werewolf.role.angel.display":isChoice(AngelForm.FALLEN_ANGEL)?"werewolf.role.fallen_angel.display":"werewolf.role.guardian_angel.display")));
        }
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        getPlayerUUID();

        UUID uuid = event.getUuid();

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if (!getAffectedPlayers().contains(uuid)) return;

        PlayerWW target = game.getPlayersWW().get(uuid);
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!plg.isState(State.ALIVE)) return;

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new AngelTargetDeathEvent(getPlayerUUID(), uuid));
        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            if (target.getLastKiller().equals(getPlayerUUID())) {
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 6);
                player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
            }
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            VersionUtils.getVersionUtils().setPlayerMaxHealth(player, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) - 6);
            player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));
            transformed=true;
        }
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {

        getPlayerUUID();

        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());
        Player player = Bukkit.getPlayer(getPlayerUUID());
        String targetName = game.getPlayersWW().get(newUUID).getName();

        if (!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if (!plg.isState(State.ALIVE)) return;

        if (player == null) return;

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.fallen_angel.new_target", targetName));
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.new_protege",targetName));
        }
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        getPlayerUUID();

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        if (Bukkit.getPlayer(event.getPlayerUUID()) == null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if(!isChoice(AngelForm.GUARDIAN_ANGEL)) {
            return;
        }

        for (UUID uuid : getAffectedPlayers()) {
            Player playerAffected = Bukkit.getPlayer(uuid);
            if (game.getPlayersWW().get(uuid).isState(State.ALIVE) && playerAffected != null) {
                stringBuilder.append("§b ").append(game.getPlayersWW().get(uuid).getName()).append(" ").append(game.getScore().updateArrow(player, playerAffected.getLocation()));
            }
        }

        event.setActionBar(stringBuilder.toString());
    }

    @Override
    public boolean getTransformed() {
        return this.transformed;
    }

    @Override
    public void setTransformed(boolean b) {
        this.transformed=b;
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() && !transformed;
    }
}
