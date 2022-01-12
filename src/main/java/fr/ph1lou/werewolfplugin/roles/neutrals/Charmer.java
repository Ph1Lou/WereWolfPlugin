package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmedDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmerGetEffectDeathEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.lovers.AbstractLover;
import fr.ph1lou.werewolfplugin.roles.lovers.FakeLoverCharmer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Charmer extends RoleNeutral implements IPower, IAffectedPlayers {

    @Nullable
    private IPlayerWW playerWW;

    private boolean power = true;

    public Charmer(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.charmer.description"))
                .setPower(
                        this.playerWW == null ?
                                game.translate("werewolf.role.charmer.choose", Formatter.timer(
                                                Utils.conversion(
                                                        game.getConfig().getTimerValue(TimerBase.LOVER_DURATION.getKey())
                                                )
                                        )
                                )
                                :
                                game.getConfig().getTimerValue(TimerBase.CHARMER_COUNTDOWN.getKey())>0?
                                        game.translate("werewolf.role.charmer.timer",
                                                Formatter.timer(
                                                        Utils.conversion(
                                                                game.getConfig().getTimerValue(TimerBase.CHARMER_COUNTDOWN.getKey())
                                                        )
                                                ),
                                                Formatter.player(this.playerWW.getName())
                                        )
                                        :
                                        game.translate("werewolf.role.charmer.affected")
                )
                .setEffects(game.translate("werewolf.role.charmer.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onCharmedDeath(FinalDeathEvent event){
        if(!event.getPlayerWW().equals(this.playerWW)){
            return;
        }

        if(this.getPlayerWW().isState(StatePlayer.DEATH)){
            return;
        }

        CharmedDeathEvent charmedDeathEvent = new CharmedDeathEvent(this.getPlayerWW(),
                this.playerWW,
                this.game.getConfig().getTimerValue(TimerBase.CHARMER_COUNTDOWN.getKey())>0);
        Bukkit.getPluginManager().callEvent(charmedDeathEvent);

        if(!charmedDeathEvent.isCancelled()){
            if(charmedDeathEvent.isBeforeCountDown()){
                this.getPlayerWW().removePlayerMaxHealth(6);
                this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.charmer.before_count_down");
            }
        }
    }

    @EventHandler
    public void onLoverDead(LoverDeathEvent event){


        if(!event.getLover().getKey().equals(LoverType.LOVER.getKey()) &&
                !event.getLover().getKey().equals(LoverType.AMNESIAC_LOVER.getKey())){
            return;
        }

        CharmerGetEffectDeathEvent effectDeath = new CharmerGetEffectDeathEvent(this.getPlayerWW(), event.getLover());

        Bukkit.getPluginManager().callEvent(effectDeath);

        if(effectDeath.isCancelled() || !this.isAbilityEnabled()){
            this.getPlayerWW()
                    .sendMessageWithKey(Prefix.RED.getKey() ,
                            "werewolf.check.cancel");
            return;
        }
        this.getPlayerWW().addPotionModifier(PotionModifier
                .add(PotionEffectType.ABSORPTION,Integer.MAX_VALUE,4,"charmer"));
        this.getPlayerWW().addPotionModifier(PotionModifier
                .add(PotionEffectType.SPEED,"charmer"));
        this.getPlayerWW().sendMessageWithKey(Prefix.LIGHT_BLUE.getKey(),
                "werewolf.role.charmer.lover_death");
    }

    @EventHandler
    public void onCharmerDeath(FinalDeathEvent event){
        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        if(this.playerWW != null){
            this.playerWW.sendMessageWithKey(Prefix.BLUE.getKey(),"werewolf.role.charmer.reveal",
                    Formatter.player(this.getPlayerWW().getName()));
        }
    }


    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW=playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if(playerWW.equals(this.playerWW)){
            this.playerWW=null;
        }
    }

    @EventHandler
    public void onLoverDurationEnd(RevealLoversEvent event){

        if(this.playerWW==null){
            this.setPower(false);

            List<IPlayerWW> playerWWS = game.getPlayersWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                    .collect(Collectors.toList());
            Collections.shuffle(playerWWS, game.getRandom());
            if(playerWWS.isEmpty()){
                return;
            }

            IPlayerWW playerWW = playerWWS.get(0);
            this.playerWW=playerWW;
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(),"werewolf.role.charmer.perform",
                    Formatter.player(playerWW.getName()));

            FakeLoverCharmer fakeLover = new FakeLoverCharmer(game,new ArrayList<>(Arrays.asList(this.getPlayerWW(), playerWW)), this.getPlayerWW());
            game.getLoversManager().addLover(fakeLover);
            BukkitUtils.registerEvents(fakeLover);
            fakeLover.announceLovers();
        }
        else{
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(),
                    "werewolf.role.charmer.announcement",
                    Formatter.player(this.playerWW.getName()));

            this.getPlayerWW().getLovers().stream()
                    .filter(iLover -> iLover instanceof FakeLoverCharmer)
                    .map(iLover -> (FakeLoverCharmer)iLover)
                    .filter(fakeLoverCharmer -> fakeLoverCharmer.getCharmer().equals(this.getPlayerWW()))
                    .forEach(AbstractLover::announceLovers);
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW=null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return Collections.singletonList(this.playerWW);
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }
}
