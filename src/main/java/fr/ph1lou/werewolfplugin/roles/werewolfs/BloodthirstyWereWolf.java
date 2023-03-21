package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.bloodthirsty_werewolf.BloodthirstyWerewolfLifeDetectionEvent;
import fr.ph1lou.werewolfapi.events.roles.bloodthirsty_werewolf.BloodthirstyWereWolfLowLifeListDisplay;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.BLOODTHIRSTY_WEREWOLF,
        category = Category.WEREWOLF,
        attributes = {RoleAttribute.WEREWOLF},
        configValues = {
                @IntValue(key = IntValueBase.BLOODTHIRSTY_SPEED,
                        defaultValue = 15,
                        meetUpValue = 15,
                        step = 5,
                        item = UniversalMaterial.FEATHER
                ),
                @IntValue(key = IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT,
                        defaultValue = 80,
                        meetUpValue = 80,
                        step = 5,
                        item = UniversalMaterial.REDSTONE
                ),
                @IntValue(key = IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT_DAY,
                        defaultValue = 6,
                        meetUpValue = 2,
                        step = 1,
                        item = UniversalMaterial.REDSTONE
                ),
                @IntValue(key = IntValueBase.BLOODTHIRSTY_TAKE_DAMAGE_DAY,
                        defaultValue = 8,
                        meetUpValue = 3,
                        step = 1,
                        item = UniversalMaterial.CLOCK
                )
        },
        timers = {
                @Timer(key = TimerBase.BLOODTHIRSTY_COOL_DOWN,
                        defaultValue = 15*60,
                        meetUpValue = 5*60,
                        step = 30
                )}
)
public class BloodthirstyWereWolf extends RoleWereWolf implements IAffectedPlayers, IPower {
    private boolean havePower = false;
    public boolean haveDealDamage = false;
    public boolean speedIsAdded = false;
    private float speedModification;
    private String arrowDirection = "";

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private IPlayerWW traquedPlayerWW = null;

    public BloodthirstyWereWolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
        this.speedModification = 0.2F * (game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_SPEED) / 100F);
    }

    @Override
    public @NotNull String getDescription() {
        String powerString;
        String timeS = Utils.conversion(game.getConfig().getTimerValue(TimerBase.BLOODTHIRSTY_COOL_DOWN));
        if(this.havePower){
            powerString = game.translate("werewolf.roles.bloodthirsty_werewolf.power_enable");
        } else if(this.traquedPlayerWW != null){
            powerString = game.translate("werewolf.roles.bloodthirsty_werewolf.power_already_used", Formatter.player(this.traquedPlayerWW.getName()));
        } else{
            powerString = game.translate("werewolf.roles.bloodthirsty_werewolf.power_disable");
        }

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate(
                        "werewolf.roles.bloodthirsty_werewolf.description",
                        Formatter.format("&time&", timeS),
                        Formatter.number(game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT)),
                        Formatter.format("&speed&", game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_SPEED)),
                        Formatter.format("&list_day&", game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT_DAY)),
                        Formatter.format("&damage_day&", game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_TAKE_DAMAGE_DAY))
                        ))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(powerString)
                .build();
    }

    @Override
    public void recoverPower() {
        if (!this.hasPower()){
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bloodthirsty_werewolf.recover_power");
        }
        this.havePower = true;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
        if(this.affectedPlayers.size() == 0){
            this.clearAffectedPlayer();
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
        this.traquedPlayerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Override
    public void setPower(boolean b) {
        this.havePower = b;
    }

    @Override
    public boolean hasPower() {
        return this.havePower;
    }

    public void startHuntingDownPlayer(IPlayerWW traquedPlayer){
        this.addAffectedPlayer(traquedPlayer);
        this.setPower(false);
        this.traquedPlayerWW = traquedPlayer;

        this.getPlayerWW().sendMessageWithKey("werewolf.roles.bloodthirsty_werewolf.hunt_down_confirm", Formatter.player(traquedPlayer.getName()));
    }

    @Override
    public void second(){

        if(this.traquedPlayerWW == null) return;

        Player player = Bukkit.getPlayer(this.getPlayerUUID());
        Player traquedPlayer = Bukkit.getPlayer(this.traquedPlayerWW.getUUID());

        if(player == null | traquedPlayer == null) return;

        if (this.arrowDirection.endsWith("↑")){
            if(!this.speedIsAdded){
                float playerWalkSpeed = player.getWalkSpeed();

                this.speedModification = 0.2F * game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_SPEED) / 100F;
                player.setWalkSpeed(playerWalkSpeed + this.speedModification);

                this.speedIsAdded = true;
            }
        } else if (this.speedIsAdded){
            float playerWalkSpeed = player.getWalkSpeed();
            player.setWalkSpeed(playerWalkSpeed - this.speedModification);

            this.speedIsAdded = false;

        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFinalDeath(FinalDeathEvent e){
        if(!e.getPlayerWW().equals(this.traquedPlayerWW)){
            return;
        }
        int coolDownValue = game.getConfig().getTimerValue(TimerBase.BLOODTHIRSTY_COOL_DOWN);
        String time = Utils.conversion(coolDownValue);
        this.getPlayerWW().sendMessageWithKey(
                Prefix.ORANGE,
                "werewolf.roles.bloodthirsty_werewolf.player_death",
                Formatter.format("&time&", time));

        BukkitUtils.scheduleSyncDelayedTask(
                BloodthirstyWereWolf.this::recoverPower,
                (long) coolDownValue * 20);

        this.traquedPlayerWW = null;

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player & e.getDamager() instanceof Player)) return;

        IPlayerWW damagerWW = game.getPlayerWW(e.getDamager().getUniqueId()).orElse(null);

        if(damagerWW == null) return;
        if(damagerWW.getRole() != this) return;


        IPlayerWW playerWW = game.getPlayerWW(e.getEntity().getUniqueId()).orElse(null);
        if(playerWW == null) return;


        if(playerWW.getRole().isWereWolf()) return;

        this.haveDealDamage = true;
    }

    public void check_list(DayEvent e){
        // ne prend que les jours après le jour spécifié
        if (e.getNumber() < game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT_DAY)) {
            return;
        }

        // qu'une seule fois tout les deux jours
        if(e.getNumber() % 2 != game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT_DAY) % 2){
            return;
        }

        float max_life = game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT) / 100f;
        List<IPlayerWW> players = game.getPlayersWW().stream()
                .filter(player1 -> player1 != this.getPlayerWW())
                .filter(player1 -> {
                    double ratio = player1.getHealth() / player1.getMaxHealth();
                    return (ratio < max_life);
                })
                .filter(player1 ->{
                    BloodthirstyWerewolfLifeDetectionEvent event = new BloodthirstyWerewolfLifeDetectionEvent(this.getPlayerWW(), player1);
                    Bukkit.getPluginManager().callEvent(event);

                    return !event.isCancelled();
                })
                .collect(Collectors.toList());

        Bukkit.getPluginManager().callEvent(new BloodthirstyWereWolfLowLifeListDisplay(
                this.getPlayerWW(),
                new HashSet<>(players)));

        String playerString = players.stream().map(IPlayerWW::getName).collect(Collectors.joining(" "));

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bloodthirsty_werewolf.low_life_players_list",
                Formatter.format("&names&", playerString),
                Formatter.format("&number&", game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_MAX_LIFE_DETECT)));
    }

    public void check_damage(DayEvent e){
        //seulement un jour sur deux
        if (e.getNumber() % 2 != game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_TAKE_DAMAGE_DAY) % 2){
            return;
        }

        //seulement a partir du jour spécifié dans la configuration
        if (e.getNumber() >= (game.getConfig().getValue(IntValueBase.BLOODTHIRSTY_TAKE_DAMAGE_DAY))){
            if(this.haveDealDamage){
                this.getPlayerWW().sendMessageWithKey(Prefix.BLUE,"werewolf.roles.bloodthirsty_werewolf.have_deal_damage");
            }else if(this.getPlayerWW().getState().equals(StatePlayer.ALIVE)){
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.bloodthirsty_werewolf.havent_deal_damage");

                if(this.getPlayerWW().getHealth() > 8){
                    this.getPlayerWW().removePlayerHealth(8);
                }else{
                    this.getPlayerWW().removePlayerHealth(this.getPlayerWW().getHealth() - 0.5);
                }
            }
        }

        this.haveDealDamage = false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDay(DayEvent e){
        this.check_list(e);
        this.check_damage(e);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onActionBarRequest(ActionBarEvent e){
        if(!this.getPlayerUUID().equals(e.getPlayerUUID())) return;

        if(this.traquedPlayerWW == null) return;

        Player player = Bukkit.getPlayer(e.getPlayerUUID());

        if(player == null) return;

        StringBuilder builder = new StringBuilder(e.getActionBar());

        Location arrowLoc = this.traquedPlayerWW.getLocation();

        String str = Utils.updateArrow(player, arrowLoc);
        this.arrowDirection = String.valueOf(str.charAt(str.length() - 1));


        if(this.traquedPlayerWW.isState(StatePlayer.ALIVE)){
            builder
                    .append(" ")
                    .append(traquedPlayerWW.getName())
                    .append(" ")
                    .append(arrowDirection);
        }

        e.setActionBar(builder.toString());
    }
}
