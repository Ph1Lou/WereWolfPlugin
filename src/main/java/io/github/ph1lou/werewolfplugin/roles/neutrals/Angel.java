package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.events.AngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.AngelTargetEvent;
import io.github.ph1lou.werewolfapi.events.AroundLover;
import io.github.ph1lou.werewolfapi.events.AutoAngelEvent;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.FallenAngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.events.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Angel extends RolesNeutral implements AffectedPlayers, LimitedUse, AngelRole {

    private int use = 0;
    private AngelForm choice = AngelForm.ANGEL;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();


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

        StringBuilder sb = new StringBuilder(super.getDescription());

        if (choice.equals(AngelForm.FALLEN_ANGEL)) {
            sb.append(game.translate("werewolf.description.power",
                    game.translate("werewolf.role.fallen_angel.power")));

            sb.append(game.translate("werewolf.description.effect",
                    game.translate("werewolf.role.fallen_angel.effect")));

            if (affectedPlayer.isEmpty()) {
                sb.append(game.translate("werewolf.description.power",
                        game.translate("werewolf.role.fallen_angel.wait",
                                game.getScore().conversion(game.getConfig()
                                        .getTimerValue(TimersBase.ANGEL_DURATION.getKey())))));
            } else {
                sb.append(game.translate("werewolf.role.angel.target",
                        affectedPlayer.get(0).getName()));
            }
        } else if (choice.equals(AngelForm.GUARDIAN_ANGEL)) {

            sb.append(game.translate("werewolf.description.effect",
                    game.translate("werewolf.role.guardian_angel.effect")));

            if (game.getConfig().isConfigActive(ConfigsBase.SWEET_ANGEL.getKey())) {
                sb.append(game.translate("werewolf.description.description",
                        game.translate("werewolf.role.guardian_angel.description")));
            } else {
                sb.append(game.translate("werewolf.description.description",
                        game.translate("werewolf.role.guardian_angel.description_patch")));
            }
            if (affectedPlayer.isEmpty()) {
                sb.append(game.translate("werewolf.description.power",
                        game.translate("werewolf.role.guardian_angel.wait",
                                game.getScore().conversion(
                                        game.getConfig().getTimerValue(TimersBase.ANGEL_DURATION.getKey())))));
            } else {
                sb.append(game.translate("werewolf.role.guardian_angel.protege",
                        affectedPlayer.get(0).getName()));
            }
            sb.append(game.translate("werewolf.description.command",
                    game.translate("werewolf.role.guardian_angel.show_command")));
        }

        return sb.toString();
    }


    /**
     * @return nb de coeur en plus qu'a l'ange plus le texte
     */

    private Pair<Integer, TextComponent> heartAndMessageTargetManagement() {


        int extraHearts = 4;

        TextComponent textComponent = new TextComponent(" ");

        if (isChoice(AngelForm.ANGEL)) {
            textComponent = choiceAngel();

        } else if (!getAffectedPlayers().isEmpty()) {

            StringBuilder sb = new StringBuilder();

            PlayerWW targetWW = getAffectedPlayers().get(0);

            if (targetWW != null) {

                if (targetWW.isState(StatePlayer.DEATH)) {

                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        if (targetWW.getKillers().contains(getPlayerWW())) {
                            extraHearts += 6;
                            sb.append(game.translate(
                                    "werewolf.role.fallen_angel.deadly_target"));
                        } else {
                            sb.append(game.translate(
                                    "werewolf.role.fallen_angel.deadly_target_by_other"));
                        }

                    } else {
                        if (game.getConfig().isConfigActive(ConfigsBase.SWEET_ANGEL.getKey())) {
                            sb.append(game.translate(
                                    "werewolf.role.guardian_angel.protege_death"));
                        } else {
                            sb.append(game.translate(
                                    "werewolf.role.guardian_angel.protege_death_patch"));
                        }
                    }


                } else if (isChoice(AngelForm.FALLEN_ANGEL)) {

                    sb.append(game.translate(
                            "werewolf.role.fallen_angel.reveal_target",
                            targetWW.getName()));
                } else {
                    extraHearts += 6;
                    sb.append(game.translate(
                            "werewolf.role.guardian_angel.reveal_protege",
                            targetWW.getName()));
                }
            }


            textComponent = new TextComponent(sb.toString());
        }

        return new Pair<>(extraHearts, textComponent);
    }


    public TextComponent choiceAngel() {


        TextComponent guardian = new TextComponent(
                ChatColor.AQUA + game.translate(RolesBase.GUARDIAN_ANGEL.getKey()));
        guardian.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.role.angel.command_1"))));
        guardian.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                game.translate(
                                        game.getConfig().isConfigActive(
                                                ConfigsBase.SWEET_ANGEL.getKey())
                                                ? "werewolf.role.angel.guardian_choice" :
                                                "werewolf.role.angel.guardian_choice_patch"))
                                .create()));

        TextComponent fallen = new TextComponent(
                ChatColor.AQUA + game.translate(RolesBase.FALLEN_ANGEL.getKey()));
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
                                game.getConfig().getTimerValue(
                                        TimersBase.ANGEL_DURATION.getKey()))
                )));

        return choice;

    }


    @Override
    public void recoverPower() {

        Pair<Integer, TextComponent> pair = heartAndMessageTargetManagement();

        getPlayerWW().addPlayerMaxHealth(pair.getValue0());

        getPlayerWW().sendMessage(pair.getValue1());
    }


    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (isChoice(AngelForm.ANGEL)) {

            if (game.getRandom().nextBoolean()) {
                getPlayerWW().sendMessageWithKey(
                        "werewolf.role.angel.angel_choice_perform",
                        game.translate(RolesBase.FALLEN_ANGEL.getKey()));
                setChoice(AngelForm.FALLEN_ANGEL);
                if (game.isDay(Day.NIGHT)) {
                    getPlayerWW().addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                }
            } else {
                getPlayerWW().sendMessageWithKey(
                        "werewolf.role.angel.angel_choice_perform",
                        game.translate(RolesBase.GUARDIAN_ANGEL.getKey()));
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(getPlayerWW(), getChoice()));
        }

        PlayerWW targetWW = game.autoSelect(getPlayerWW());
        addAffectedPlayer(targetWW);

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            getPlayerWW().sendMessageWithKey(
                    "werewolf.role.fallen_angel.reveal_target",
                    targetWW.getName());
        } else {
            getPlayerWW().addPlayerMaxHealth(6);
            getPlayerWW().sendMessageWithKey(
                    "werewolf.role.guardian_angel.reveal_protege",
                    targetWW.getName());
        }
        Sound.PORTAL_TRIGGER.play(getPlayerWW());

        Bukkit.getPluginManager().callEvent(
                new AngelTargetEvent(getPlayerWW(), targetWW));

        game.checkVictory();
    }




    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();
        if (isKey(RolesBase.ANGEL.getKey()) && !isChoice(AngelForm.ANGEL)) {
            sb.append(", ").append(game.translate("werewolf.role.angel.choice",
                    game.translate(isChoice(AngelForm.ANGEL) ?
                            "werewolf.role.angel.display" :
                            isChoice(AngelForm.FALLEN_ANGEL) ?
                                    "werewolf.role.fallen_angel.display" :
                                    "werewolf.role.guardian_angel.display")));
        }
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        Bukkit.getPluginManager().callEvent(new AngelTargetDeathEvent(getPlayerWW(),
                playerWW));
        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            playerWW.getLastKiller().ifPresent(playerWW1 -> {
                if (getPlayerWW().equals(playerWW1)) {
                    Bukkit.getPluginManager().callEvent(
                            new FallenAngelTargetDeathEvent(getPlayerWW(), playerWW));
                    getPlayerWW().addPlayerMaxHealth(6);
                    getPlayerWW().sendMessageWithKey("werewolf.role.fallen_angel.deadly_target");
                }
            });

        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            getPlayerWW().removePlayerMaxHealth(6);

            if (game.getConfig().isConfigActive(
                    ConfigsBase.SWEET_ANGEL.getKey())) {
                getPlayerWW().sendMessageWithKey(
                        "werewolf.role.guardian_angel.protege_death");
            } else {
                getPlayerWW().sendMessageWithKey(
                        "werewolf.role.guardian_angel.protege_death_patch");
            }
        }

    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        PlayerWW playerWW = event.getPlayerWW();
        PlayerWW thiefWW = event.getThiefWW();

        String targetName = thiefWW.getName();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;


        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            getPlayerWW().sendMessageWithKey("werewolf.role.fallen_angel.new_target", targetName);
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            getPlayerWW().sendMessageWithKey("werewolf.role.guardian_angel.new_protege", targetName);
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

            if (playerWW.isState(StatePlayer.ALIVE)) {

                stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" ")
                        .append(game.getScore()
                                .updateArrow(player,
                                        playerWW.getLocation()));
            }
        }

        event.setActionBar(stringBuilder.toString());
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() &&
                (!game.getConfig().isConfigActive(ConfigsBase.SWEET_ANGEL.getKey())
                        || !choice.equals(AngelForm.GUARDIAN_ANGEL)
                        || affectedPlayer.isEmpty()
                        || !affectedPlayer.get(0).isState(StatePlayer.DEATH));
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        getPlayerWW().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        getPlayerWW().addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

}
