package io.github.ph1lou.werewolfplugin.roles.neutrals;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AutoAngelEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.FallenAngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Angel extends RoleNeutral implements IAffectedPlayers, ILimitedUse {

    private int use = 0;
    private AngelForm choice = AngelForm.ANGEL;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();


    public Angel(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
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
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    public boolean isChoice(AngelForm AngelForm) {
        return AngelForm==choice;
    }


    public AngelForm getChoice() {
        return this.choice;
    }


    public void setChoice(AngelForm choice) {
        this.choice = choice;
    }

    @Override
    public @NotNull String getDescription() {

        if (choice.equals(AngelForm.FALLEN_ANGEL)) {

            return new DescriptionBuilder(game, this)
                    .setPower(game.translate("werewolf.role.fallen_angel.power"))
                    .setEffects(game.translate("werewolf.role.fallen_angel.effect"))
                    .addExtraLines(affectedPlayer.isEmpty() ?
                            game.translate("werewolf.description.power",
                                    game.translate("werewolf.role.fallen_angel.wait",
                                            Utils.conversion(game.getConfig()
                                                    .getTimerValue(TimerBase.ANGEL_DURATION.getKey()))))
                            : game.translate("werewolf.role.angel.target", affectedPlayer.get(0).getName()))
                    .build();


        } else if (choice.equals(AngelForm.GUARDIAN_ANGEL)) {

            return new DescriptionBuilder(game, this)
                    .setEffects(game.translate("werewolf.role.guardian_angel.effect"))
                    .setDescription(game.getConfig().isSweetAngel() ?
                            game.translate("werewolf.role.guardian_angel.description") :
                            game.translate("werewolf.role.guardian_angel.description_patch"))
                    .addExtraLines(affectedPlayer.isEmpty() ?
                            game.translate("werewolf.description.power",
                                    game.translate("werewolf.role.guardian_angel.wait",
                                            Utils.conversion(
                                                    game.getConfig().getTimerValue(TimerBase.ANGEL_DURATION.getKey())))) :
                            game.translate("werewolf.role.guardian_angel.protege", affectedPlayer.get(0).getName()))
                    .setCommand(game.translate("werewolf.role.guardian_angel.show_command"))
                    .build();
        } else {
            return new DescriptionBuilder(game, this).build();
        }


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

            IPlayerWW targetWW = getAffectedPlayers().get(0);

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
                        if (game.getConfig().isSweetAngel()) {
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
                                        game.getConfig().isSweetAngel()
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
                        Utils.conversion(
                                game.getConfig().getTimerValue(
                                        TimerBase.ANGEL_DURATION.getKey()))
                )));

        return choice;

    }


    @Override
    public void recoverPower() {

        Pair<Integer, TextComponent> pair = heartAndMessageTargetManagement();

        this.getPlayerWW().addPlayerMaxHealth(pair.getValue0());

        this.getPlayerWW().sendMessage(pair.getValue1());
    }


    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (isChoice(AngelForm.ANGEL)) {

            if (game.getRandom().nextBoolean()) {
                this.getPlayerWW().sendMessageWithKey(
                        "werewolf.role.angel.angel_choice_perform",
                        game.translate(RolesBase.FALLEN_ANGEL.getKey()));
                setChoice(AngelForm.FALLEN_ANGEL);
                if (game.isDay(Day.NIGHT)) {
                    this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,"fallen_angel"));
                }
            } else {
                this.getPlayerWW().sendMessageWithKey(
                        "werewolf.role.angel.angel_choice_perform",
                        game.translate(RolesBase.GUARDIAN_ANGEL.getKey()));
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(this.getPlayerWW(), getChoice()));
        }

        IPlayerWW targetWW = game.autoSelect(getPlayerWW());
        addAffectedPlayer(targetWW);

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            this.getPlayerWW().sendMessageWithKey(
                    "werewolf.role.fallen_angel.reveal_target", Sound.PORTAL_TRIGGER,
                    targetWW.getName());
        } else {
            this.getPlayerWW().addPlayerMaxHealth(6);
            this.getPlayerWW().sendMessageWithKey(
                    "werewolf.role.guardian_angel.reveal_protege", Sound.PORTAL_TRIGGER,
                    targetWW.getName());
        }

        Bukkit.getPluginManager().callEvent(
                new AngelTargetEvent(this.getPlayerWW(), targetWW));

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

        IPlayerWW playerWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        Bukkit.getPluginManager().callEvent(new AngelTargetDeathEvent(this.getPlayerWW(),
                playerWW));
        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            playerWW.getLastKiller().ifPresent(iPlayerWW -> {
                if (this.getPlayerWW().equals(iPlayerWW)) {
                    Bukkit.getPluginManager().callEvent(
                            new FallenAngelTargetDeathEvent(this.getPlayerWW(), playerWW));
                    this.getPlayerWW().addPlayerMaxHealth(6);
                    this.getPlayerWW().sendMessageWithKey("werewolf.role.fallen_angel.deadly_target");
                }
            });

        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            this.getPlayerWW().removePlayerMaxHealth(6);

            if (game.getConfig().isSweetAngel()) {
                this.getPlayerWW().sendMessageWithKey(
                        "werewolf.role.guardian_angel.protege_death");
            } else {
                this.getPlayerWW().sendMessageWithKey(
                        "werewolf.role.guardian_angel.protege_death_patch");
            }
        }

    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW thiefWW = event.getThiefWW();

        String targetName = thiefWW.getName();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            this.getPlayerWW().sendMessageWithKey("werewolf.role.fallen_angel.new_target", targetName);
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            this.getPlayerWW().sendMessageWithKey("werewolf.role.guardian_angel.new_protege", targetName);
        }
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!isChoice(AngelForm.GUARDIAN_ANGEL)) {
            return;
        }

        for (IPlayerWW playerWW : getAffectedPlayers()) {

            if (playerWW.isState(StatePlayer.ALIVE)) {

                stringBuilder.append("§b ")
                        .append(playerWW.getName())
                        .append(" ")
                        .append(Utils.updateArrow(player,
                                playerWW.getLocation()));
            }
        }

        event.setActionBar(stringBuilder.toString());
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() &&
                (!game.getConfig().isSweetAngel()
                        || !choice.equals(AngelForm.GUARDIAN_ANGEL)
                        || affectedPlayer.isEmpty()
                        || !affectedPlayer.get(0).isState(StatePlayer.DEATH));
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    @Override
    public Aura getAura() {
        return Aura.LIGHT;
    }

    @EventHandler
    public void onLover(AroundLoverEvent event) {

        if (!choice.equals(AngelForm.GUARDIAN_ANGEL)) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getPlayerWWS().contains(getPlayerWW())) {
            for (IPlayerWW playerWW : affectedPlayer) {
                event.addPlayer(playerWW);
            }
            return;
        }

        for (IPlayerWW playerWW : event.getPlayerWWS()) {
            if (affectedPlayer.contains(playerWW)) {
                event.addPlayer(getPlayerWW());
                break;
            }
        }
    }

    @EventHandler
    public void onDetectVictoryWithProtege(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (affectedPlayer.isEmpty()) return;


        IPlayerWW playerWW = affectedPlayer.get(0);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;


        List<IPlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            IPlayerWW playerWW2 = list.get(i);

            game.getPlayersWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isKey(RolesBase.ANGEL.getKey())
                            || roles.isKey(RolesBase.GUARDIAN_ANGEL.getKey()))
                    .map(iRole -> (Angel) iRole)
                    .filter(roles -> roles.isChoice(AngelForm.GUARDIAN_ANGEL))
                    .forEach(role -> {
                        if (((IAffectedPlayers) role).getAffectedPlayers().contains(playerWW2)) {
                            if (!list.contains(role.getPlayerWW())) {
                                list.add(role.getPlayerWW());
                            }
                        }
                    });

        }

        if (game.getPlayerSize() == list.size()) {
            event.setCancelled(true);
            event.setVictoryTeam(RolesBase.GUARDIAN_ANGEL.getKey());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"fallen_angel"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        if(!this.isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,"fallen_angel"));
    }

    public static ClickableItem config(WereWolfAPI game) {

        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(Material.MELON)
                        .setLore(game.translate(config.isSweetAngel() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                        .setDisplayName(game.translate("werewolf.role.angel.sweet_angel"))
                        .build(), e -> {
                    config.setSweetAngel(!config.isSweetAngel());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate(config.isSweetAngel() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                            .build());

                });
    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"fallen_angel"));
    }
}
