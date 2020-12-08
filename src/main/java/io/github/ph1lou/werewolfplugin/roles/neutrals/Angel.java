package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Angel extends RolesNeutral implements AffectedPlayers, LimitedUse, AngelRole, Transformed {

    private int use = 0;
    private AngelForm choice = AngelForm.ANGEL;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean transformed = false;

    public Angel(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
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
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
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
    public @NotNull String getDescription() {

        StringBuilder sb = new StringBuilder();

        if (choice.equals(AngelForm.FALLEN_ANGEL))
            sb.append(game.translate("werewolf.role.fallen_angel.description"));
        else if (choice.equals(AngelForm.GUARDIAN_ANGEL)) {

            if (game.getConfig().getConfigValues().get(ConfigsBase.SWEET_ANGEL.getKey())) {
                sb.append(game.translate("werewolf.role.guardian_angel.description"));
            } else {
                sb.append(game.translate("werewolf.role.guardian_angel.description_patch"));
            }
        } else {
            sb.append(game.translate("werewolf.role.angel.description"));
        }
        sb.append("\n§f").append(heartAndMessageTargetManagement().getValue1());

        return sb.toString();
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        Pair<Integer, String> pair = heartAndMessageTargetManagement();

        VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                VersionUtils.getVersionUtils().getPlayerMaxHealth(player)
                        + pair.getValue0());

        player.sendMessage(pair.getValue1());
    }

    /**
     * @return nb de coeur en plus qu'a l'ange plus le texte
     */

    private Pair<Integer, String> heartAndMessageTargetManagement() {


        int extraHearts = 0;
        StringBuilder sb = new StringBuilder();

        if (isChoice(AngelForm.ANGEL)) {
            extraHearts += 4;
            Player player = Bukkit.getPlayer(getPlayerUUID());

            if (player != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> player.spigot().sendMessage(choiceAngel()), 1);

            }

        } else if (!getAffectedPlayers().isEmpty()) {

            PlayerWW targetWW = getAffectedPlayers().get(0);

            if (targetWW != null) {

                if (targetWW.isState(StatePlayer.DEATH)) {

                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        if (targetWW.getKillers().contains(getPlayerWW())) {
                            extraHearts += 10;
                            sb.append(game.translate(
                                    "werewolf.role.fallen_angel.deadly_target"));
                        } else {
                            extraHearts += 4;
                            sb.append(game.translate(
                                    "werewolf.role.fallen_angel.deadly_target_by_other"));
                        }

                    } else {
                        extraHearts += 4;
                        if (game.getConfig().getConfigValues()
                                .get(ConfigsBase.SWEET_ANGEL.getKey())) {
                            sb.append(game.translate(
                                    "werewolf.role.guardian_angel.protege_death"));
                        } else {
                            sb.append(game.translate(
                                    "werewolf.role.guardian_angel.protege_death_patch"));
                        }
                    }


                } else if (isChoice(AngelForm.FALLEN_ANGEL)) {
                    extraHearts += 4;
                    sb.append(game.translate(
                            "werewolf.role.fallen_angel.reveal_target",
                            targetWW.getName()));
                } else {
                    extraHearts += 10;
                    sb.append(game.translate(
                            "werewolf.role.guardian_angel.reveal_protege",
                            targetWW.getName()));
                }
            }
        }

        return new Pair<>(extraHearts, sb.toString());
    }


    public TextComponent choiceAngel() {

        TextComponent guardian = new TextComponent(
                game.translate(RolesBase.GUARDIAN_ANGEL.getKey()));
        guardian.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.role.angel.command_1"))));
        guardian.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                game.translate(
                                        game.getConfig().getConfigValues().get(
                                                ConfigsBase.SWEET_ANGEL.getKey())
                                                ? "werewolf.role.angel.guardian_choice" :
                                                "werewolf.role.angel.guardian_choice_patch"))
                                .create()));

        TextComponent fallen = new TextComponent(
                game.translate(RolesBase.FALLEN_ANGEL.getKey()));
        fallen.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.role.angel.command_2"))));
        fallen.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                game.translate(
                                        "werewolf.role.angel.fallen_choice"))
                                .create()));

        TextComponent choice = new TextComponent(
                game.translate("werewolf.role.angel.angel_choice"));

        choice.addExtra(guardian);
        choice.addExtra(new TextComponent(
                game.translate("werewolf.role.angel.or")));
        choice.addExtra(fallen);
        choice.addExtra(new TextComponent(
                game.translate("werewolf.role.angel.time",
                        game.getScore().conversion(
                                game.getConfig().getTimerValues().get(
                                        TimersBase.ANGEL_DURATION.getKey()))
                )));

        return choice;

    }


    @Override
    public void recoverPower() {

        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;

        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
        player.setHealth(24);
    }


    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (isChoice(AngelForm.ANGEL)) {

            if (game.getRandom().nextBoolean()) {
                if (player != null) {
                    player.sendMessage(
                            game.translate(
                                    "werewolf.role.angel.angel_choice_perform",
                                    game.translate(RolesBase.FALLEN_ANGEL.getKey())));
                }
                setChoice(AngelForm.FALLEN_ANGEL);
            } else {
                if (player != null) {
                    player.sendMessage(
                            game.translate(
                                    "werewolf.role.angel.angel_choice_perform",
                                    game.translate(RolesBase.FALLEN_ANGEL.getKey())));
                }
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(getPlayerWW(), getChoice()));
        }

        PlayerWW targetWW = game.autoSelect(getPlayerWW());
        addAffectedPlayer(targetWW);

        if (player != null) {

            if (isChoice(AngelForm.FALLEN_ANGEL)) {
                player.sendMessage(game.translate(
                        "werewolf.role.fallen_angel.reveal_target",
                        targetWW.getName()));
            } else {
                VersionUtils.getVersionUtils()
                        .setPlayerMaxHealth(player,
                                VersionUtils
                                        .getVersionUtils()
                                        .getPlayerMaxHealth(player) + 6);
                player.sendMessage(game.translate(
                        "werewolf.role.guardian_angel.reveal_protege",
                        targetWW.getName()));
            }
            Sounds.PORTAL_TRIGGER.play(player);
        }

        Bukkit.getPluginManager().callEvent(
                new AngelTargetEvent(getPlayerWW(), targetWW));

        game.checkVictory();
    }




    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();
        if (isKey(RolesBase.ANGEL.getKey()) && !isChoice(AngelForm.ANGEL)) {
            sb.append(", ").append(game.translate("werewolf.role.angel.choice", game.translate(isChoice(AngelForm.ANGEL) ? "werewolf.role.angel.display" : isChoice(AngelForm.FALLEN_ANGEL) ? "werewolf.role.fallen_angel.display" : "werewolf.role.guardian_angel.display")));
        }
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new AngelTargetDeathEvent(getPlayerWW(),
                playerWW));
        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            if (getPlayerWW().equals(playerWW.getLastKiller())) {
                Bukkit.getPluginManager().callEvent(
                        new FallenAngelTargetDeathEvent(getPlayerWW(), playerWW));
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                        VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 6);
                player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
            }
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                    VersionUtils.getVersionUtils().getPlayerMaxHealth(player) - 6);

            if (game.getConfig().getConfigValues().get(
                    ConfigsBase.SWEET_ANGEL.getKey())) {
                player.sendMessage(game.translate(
                        "werewolf.role.guardian_angel.protege_death"));
            } else {
                player.sendMessage(game.translate(
                        "werewolf.role.guardian_angel.protege_death_patch"));
            }

            transformed = true;
        }

    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());
        PlayerWW playerWW = event.getPlayerWW();
        PlayerWW thiefWW = event.getThiefWW();


        String targetName = thiefWW.getName();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.fallen_angel.new_target", targetName));
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.new_protege", targetName));
        }
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!isChoice(AngelForm.GUARDIAN_ANGEL)) {
            return;
        }

        for (PlayerWW playerWW : getAffectedPlayers()) {
            Player playerAffected = Bukkit.getPlayer(playerWW.getUUID());

            if (playerWW.isState(StatePlayer.ALIVE) &&
                    playerAffected != null) {

                stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" ")
                        .append(game.getScore()
                                .updateArrow(player,
                                        playerAffected.getLocation()));
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
        this.transformed = b;
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() &&
                (!game.getConfig().getConfigValues().get(ConfigsBase.SWEET_ANGEL.getKey())
                        || !transformed);
    }

    @EventHandler
    public void onLover(AroundLover event) {

        if (!choice.equals(AngelForm.GUARDIAN_ANGEL)) return;

        if (!Objects.requireNonNull(
                game.getPlayerWW(
                        getPlayerUUID())).isState(StatePlayer.ALIVE)) return;

        if (event.getPlayerWWS().contains(getPlayerWW())) {
            for (PlayerWW playerWW : affectedPlayer) {
                event.addPlayer(playerWW);
            }
            return;
        }

        for (PlayerWW playerWW : event.getPlayerWWS()) {
            if (affectedPlayer.contains(playerWW)) {
                event.addPlayer(getPlayerWW());
                break;
            }
        }
    }

    @EventHandler
    public void onDetectVictoryWithProtege(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (!Objects.requireNonNull(
                game.getPlayerWW(
                        getPlayerUUID())).isState(StatePlayer.ALIVE)) return;

        if (affectedPlayer.isEmpty()) return;


        PlayerWW playerWW = affectedPlayer.get(0);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;


        List<PlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            PlayerWW playerWW2 = list.get(i);

            game.getPlayerWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .map(PlayerWW::getRole)
                    .filter(roles -> roles.isKey(RolesBase.ANGEL.getKey())
                            || roles.isKey(RolesBase.GUARDIAN_ANGEL.getKey()))
                    .filter(roles -> ((AngelRole) roles).isChoice(AngelForm.GUARDIAN_ANGEL))
                    .forEach(role -> {
                        if (((AffectedPlayers) role).getAffectedPlayers().contains(playerWW2)) {
                            if (!list.contains(role.getPlayerWW())) {
                                list.add(role.getPlayerWW());
                            }
                        }
                    });

        }

        if (game.getScore().getPlayerSize() == list.size()) {
            event.setCancelled(true);
            event.setVictoryTeam(RolesBase.GUARDIAN_ANGEL.getKey());
        }
    }

}
