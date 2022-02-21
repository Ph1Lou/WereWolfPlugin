package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.necromancer.NecromancerResurrectionEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Necromancer extends RoleNeutral implements IPower, IProgress {

    private boolean power = true;
    @Nullable
    private IPlayerWW playerWW;
    private float progress = 0;
    private int health = 0;

    public Necromancer(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.necromancer.description",
                        Formatter.number(game.getConfig().getDistanceNecromancer())))
                .setPower(game.translate(this.power?"werewolf.role.necromancer.power_disable":
                        "werewolf.role.necromancer.power_enable",
                        Formatter.number((int) Math.min(100, Math.floor(this.getProgress())))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void OnFirstDeath(FirstDeathEvent event){

        if(!this.isAbilityEnabled()){
            return;
        }

        if(this.hasPower()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(this.playerWW != null){
            return;
        }

        if(event.getPlayerWW().getDeathLocation()
                .distance(this.getPlayerWW().getLocation())
                > game.getConfig().getDistanceNecromancer()){
            return;
        }

        NecromancerResurrectionEvent necromancerResurrectionEvent =
                new NecromancerResurrectionEvent(this.getPlayerWW(), event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(necromancerResurrectionEvent);

        if(necromancerResurrectionEvent.isCancelled()){
            this.getPlayerWW()
                    .sendMessageWithKey(Prefix.RED.getKey(), "werewolf.check.cancel");
            return;
        }

        this.playerWW = event.getPlayerWW();

        this.playerWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.necromancer.resurrection");

        event.setCancelled(true);

        game.resurrection(event.getPlayerWW());

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(),"werewolf.role.necromancer.perform",
                Formatter.player(event.getPlayerWW().getName()));
    }

    @EventHandler
    public void OnDeath(FinalDeathEvent event){
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(!event.getPlayerWW().equals(this.playerWW)){
            return;
        }

        this.playerWW = event.getPlayerWW().getLastKiller().orElse(null);

        if(this.getPlayerWW().equals(this.playerWW)){
            this.playerWW = Bukkit.getOnlinePlayers().stream()
                    .map(Entity::getUniqueId)
                    .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                    .map(game::getPlayerWW)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .min(Comparator.comparingDouble(value -> {
                        if (event.getPlayerWW().getDeathLocation().getWorld() != value.getLocation().getWorld()) {
                            return Integer.MAX_VALUE;
                        }
                        return event.getPlayerWW().getDeathLocation().distance(value.getLocation());
                    }))
                    .orElse(null);
        }

        if(this.playerWW != null){
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.necromancer.new_victim",
                    Formatter.player(this.playerWW.getName()));
        }
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @EventHandler
    public void OnNecromancerDeath(FinalDeathEvent event){

        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        if(this.playerWW == null){
            return;
        }

        if(!this.playerWW.isState(StatePlayer.ALIVE)){
            return;
        }

        this.playerWW.sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.necromancer.necromancer_death");

        int task = BukkitUtils.scheduleSyncRepeatingTask(() -> {
            if (this.game.isState(StateGame.GAME)) {
                playerWW.addPlayerMaxHealth(2);
            }
        }, 20 * 60 * 3, 20 * 60 * 3);
        BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getScheduler().cancelTask(task), this.health * 20 * 61 * 3L);
    }

    @Override
    public void second() {

        if(this.playerWW == null){
            return;
        }

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(this.health >= 7){
            return;
        }

        if(!this.playerWW.isState(StatePlayer.ALIVE)){
            return;
        }

        double distance = this.playerWW.getLocation()
                .distance(this.getPlayerWW().getLocation());

        if(distance >
                game.getConfig().getDistanceNecromancer()){
            return;
        }

        this.progress += game.getConfig().getDistanceNecromancer() /
                Math.max(distance, game.getConfig().getDistanceNecromancer()/4f)
                *
                1 / 6f;

        if(this.progress >= 100){
            this.progress = 0;
            health++;
            this.playerWW.removePlayerMaxHealth(2);
            this.getPlayerWW().addPlayerMaxHealth(2);
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.necromancer.steal",
                    Formatter.player(this.playerWW.getName()));
            this.playerWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.necromancer.necromancer_steal");
        }
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public float getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BLACK_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.role.necromancer.config",
                                Formatter.number(config.getDistanceNecromancer())))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceNecromancer((config.getDistanceNecromancer() + 2));
            } else if (config.getDistanceNecromancer() - 2 > 0) {
                config.setDistanceNecromancer(config.getDistanceNecromancer() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.role.necromancer.config",
                            Formatter.number(config.getDistanceNecromancer())))
                    .build());

        });
    }
}
