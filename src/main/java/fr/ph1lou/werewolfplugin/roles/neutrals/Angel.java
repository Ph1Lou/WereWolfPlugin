package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelTargetDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelTargetEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AutoAngelEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.FallenAngelTargetDeathEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.utils.Utils;
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


@Role(key = RoleBase.ANGEL,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.NEUTRAL,
        timers = @Timer(key = TimerBase.ANGEL_DURATION,
                defaultValue = 240, meetUpValue = 240,
        decrementAfterRole = true,
        onZero = AutoAngelEvent.class),
        configurations = @Configuration(config = @ConfigurationBasic(key = ConfigBase.SWEET_ANGEL))
)
public class Angel extends RoleNeutral implements IAffectedPlayers, ILimitedUse {

    private int use = 0;
    private AngelForm choice = AngelForm.ANGEL;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Angel(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                    .setPower(game.translate("werewolf.roles.fallen_angel.power"))
                    .setEffects(game.translate("werewolf.roles.fallen_angel.effect"))
                    .addExtraLines(affectedPlayer.isEmpty() ?
                            game.translate("werewolf.description.power",
                                    Formatter.format("&on&",game.translate("werewolf.roles.fallen_angel.wait",
                                            Formatter.timer(game, TimerBase.ANGEL_DURATION))))
                            : game.translate("werewolf.roles.angel.target",
                                    Formatter.format("&target&",affectedPlayer.get(0).getName())))
                    .build();


        } else if (choice.equals(AngelForm.GUARDIAN_ANGEL)) {

            return new DescriptionBuilder(game, this)
                    .setEffects(game.translate("werewolf.roles.guardian_angel.effect"))
                    .setDescription(game.getConfig().isConfigActive(ConfigBase.SWEET_ANGEL) ?
                            game.translate("werewolf.roles.guardian_angel.description") :
                            game.translate("werewolf.roles.guardian_angel.description_patch"))
                    .addExtraLines(affectedPlayer.isEmpty() ?
                            game.translate("werewolf.description.power",
                                    Formatter.format("&on&",game.translate("werewolf.roles.guardian_angel.wait",Formatter.timer(
                                            game, TimerBase.ANGEL_DURATION)))) :
                            game.translate("werewolf.roles.guardian_angel.protege",
                                    Formatter.player(affectedPlayer.get(0).getName()),
                                    Formatter.role(game.translate(affectedPlayer.get(0).getRole().getKey()))))
                    .setCommand(game.translate("werewolf.roles.guardian_angel.show_command"))
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
                                    Prefix.YELLOW , "werewolf.roles.fallen_angel.deadly_target"));
                        } else {
                            sb.append(game.translate(
                                    Prefix.RED , "werewolf.roles.fallen_angel.deadly_target_by_other"));
                        }

                    } else {
                        if (game.getConfig().isConfigActive(ConfigBase.SWEET_ANGEL)) {
                            sb.append(game.translate(
                                    Prefix.YELLOW , "werewolf.roles.guardian_angel.protege_death"));
                        } else {
                            sb.append(game.translate(
                                    Prefix.YELLOW , "werewolf.roles.guardian_angel.protege_death_patch"));
                        }
                    }


                } else if (isChoice(AngelForm.FALLEN_ANGEL)) {

                    sb.append(game.translate(
                            Prefix.YELLOW , "werewolf.roles.fallen_angel.reveal_target",
                            Formatter.format("&target&",targetWW.getName()),
                            Formatter.role(game.translate(targetWW.getRole().getKey()))));
                } else {
                    extraHearts += 6;
                    sb.append(game.translate(
                            Prefix.YELLOW , "werewolf.roles.guardian_angel.reveal_protege",
                            Formatter.player(targetWW.getName()),
                            Formatter.role(game.translate(targetWW.getRole().getKey()))));
                }
            }


            textComponent = new TextComponent(sb.toString());
        }

        return new Pair<>(extraHearts, textComponent);
    }


    public TextComponent choiceAngel() {


        TextComponent guardian = new TextComponent(
                ChatColor.AQUA + game.translate(RoleBase.GUARDIAN_ANGEL));
        guardian.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.roles.angel.command_1"))));
        guardian.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                game.translate(
                                        game.getConfig().isConfigActive(ConfigBase.SWEET_ANGEL)
                                                ? "werewolf.roles.angel.guardian_choice" :
                                                "werewolf.roles.angel.guardian_choice_patch"))
                                .create()));

        TextComponent fallen = new TextComponent(
                ChatColor.AQUA + game.translate(RoleBase.FALLEN_ANGEL));
        fallen.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s",
                        game.translate("werewolf.roles.angel.command_2"))));
        fallen.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                game.translate(
                                        "werewolf.roles.angel.fallen_choice"))
                                .create()));

        TextComponent choice = new TextComponent(
                game.translate(Prefix.YELLOW , "werewolf.roles.angel.angel_choice"));

        choice.addExtra(guardian);
        choice.addExtra(new TextComponent(
                game.translate("werewolf.roles.angel.or")));
        choice.addExtra(fallen);
        choice.addExtra(new TextComponent(
                game.translate("werewolf.roles.angel.time",
                        Formatter.timer(game, TimerBase.ANGEL_DURATION
                ))));

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
                        Prefix.YELLOW , "werewolf.roles.angel.angel_choice_perform",
                        Formatter.format("&form&",game.translate(RoleBase.FALLEN_ANGEL)));
                setChoice(AngelForm.FALLEN_ANGEL);
                if (game.isDay(Day.NIGHT)) {
                    this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,this.getKey()));
                }
            } else {
                this.getPlayerWW().sendMessageWithKey(
                        Prefix.YELLOW , "werewolf.roles.angel.angel_choice_perform",
                        Formatter.format("&form&",game.translate(RoleBase.GUARDIAN_ANGEL)));
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(this.getPlayerWW(), getChoice()));
        }

        IPlayerWW targetWW = Utils.autoSelect(game, getPlayerWW());
        addAffectedPlayer(targetWW);

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.YELLOW , "werewolf.roles.fallen_angel.reveal_target",
                    Formatter.format("&target&",targetWW.getName()),
                    Formatter.role(game.translate(targetWW.getRole().getKey())));
        } else {
            this.getPlayerWW().addPlayerMaxHealth(6);
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.YELLOW , "werewolf.roles.guardian_angel.reveal_protege",
                    Formatter.player(targetWW.getName()),
                    Formatter.role(game.translate(targetWW.getRole().getKey())));
        }
        this.getPlayerWW().sendSound(Sound.PORTAL_TRIGGER);

        Bukkit.getPluginManager().callEvent(
                new AngelTargetEvent(this.getPlayerWW(), targetWW));

        game.checkVictory();
    }




    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();
        if (isKey(RoleBase.ANGEL) && !isChoice(AngelForm.ANGEL)) {
            sb.append(", ").append(game.translate("werewolf.roles.angel.choice",
                    Formatter.format("&form&",game.translate(isChoice(AngelForm.ANGEL) ?
                            "werewolf.roles.angel.display" :
                            isChoice(AngelForm.FALLEN_ANGEL) ?
                                    "werewolf.roles.fallen_angel.display" :
                                    "werewolf.roles.guardian_angel.display"))));
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


            if (playerWW.getLastMinutesDamagedPlayer().contains(this.getPlayerWW()) ||
                    (playerWW.getLastKiller().isPresent() &&
                            this.getPlayerWW().equals(playerWW.getLastKiller().get()))) {
                Bukkit.getPluginManager().callEvent(
                        new FallenAngelTargetDeathEvent(this.getPlayerWW(), playerWW));
                this.getPlayerWW().addPlayerMaxHealth(6);
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.fallen_angel.deadly_target");
            }

        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            this.getPlayerWW().removePlayerMaxHealth(6);

            if (game.getConfig().isConfigActive(ConfigBase.SWEET_ANGEL)) {
                this.getPlayerWW().sendMessageWithKey(
                        Prefix.YELLOW , "werewolf.roles.guardian_angel.protege_death");
            } else {
                this.getPlayerWW().sendMessageWithKey(
                        Prefix.YELLOW , "werewolf.roles.guardian_angel.protege_death_patch");
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
            this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE , "werewolf.roles.fallen_angel.new_target",
                    Formatter.format("&target&",targetName),
                    Formatter.role(game.translate(thiefWW.getRole().getKey())));
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE , "werewolf.roles.guardian_angel.new_protege",
                    Formatter.player(targetName),
                    Formatter.role(game.translate(thiefWW.getRole().getKey())));
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
                (!game.getConfig().isConfigActive(ConfigBase.SWEET_ANGEL)
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
                    .filter(roles -> roles.isKey(RoleBase.ANGEL)
                            || roles.isKey(RoleBase.GUARDIAN_ANGEL))
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

        if (game.getPlayersCount() == list.size()) {
            event.setCancelled(true);
            event.setVictoryTeam(RoleBase.GUARDIAN_ANGEL);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,this.getKey(),0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {

        if (!choice.equals(AngelForm.FALLEN_ANGEL)) return;

        if(!this.isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,this.getKey()));
    }


    @Override
    public void disableAbilitiesRole() {
        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,this.getKey(),0));
    }
}
