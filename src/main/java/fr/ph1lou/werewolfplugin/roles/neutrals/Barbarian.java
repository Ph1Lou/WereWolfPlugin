package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Barbarian extends RoleNeutral implements IPower, IAffectedPlayers {
    @Nullable
    private IPlayerWW playerWW;
    private boolean power = true;
    private final Set<IPlayerWW> damagedPlayers = new HashSet<>();

    public Barbarian(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.barbarian.description",
                        Formatter.number(game.getConfig().getDistanceBarbarian())))
                .setPower(game.translate("werewolf.role.barbarian.power"))
                .setItems(game.translate("werewolf.role.barbarian.item"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onMaskedDeath(FirstDeathEvent event){

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.hasPower()){
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if(event.getPlayerWW().getDeathLocation().getWorld() !=
                this.getPlayerWW().getLocation().getWorld()){
            return;
        }

        if(event.getPlayerWW().getDeathLocation().distance(this.getPlayerWW().getLocation())
                > game.getConfig().getDistanceBarbarian()){
            return;
        }

        TextComponent hideMessage = new TextComponent(
                game.translate(
                        Prefix.YELLOW.getKey() , "werewolf.role.barbarian.click_message",
                        Formatter.player(event.getPlayerWW().getName())));
        hideMessage.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.barbarian.command"),
                                event.getPlayerWW().getUUID())));
        getPlayerWW().sendMessage(hideMessage);
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMaskedDeathAnnouncement(AnnouncementDeathEvent event){

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(event.getTargetPlayer().equals(this.getPlayerWW())){
            return;
        }

        if(event.getPlayerWW().equals(this.playerWW)){
            event.setFormat("werewolf.announcement.death_message");
        }
    }

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!game.isState(StateGame.GAME)){
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        Player striker;


        if (!(event.getDamager() instanceof Player)) {

            if (!(event.getDamager() instanceof Arrow)) return;

            ProjectileSource shooter = ((Arrow) event.getDamager()).getShooter();

            if (!(shooter instanceof Player)) return;

            striker = (Player) shooter;
        }
        else{
            striker = (Player) event.getDamager();
        }

        if(!striker.getUniqueId().equals(this.getPlayerUUID())){
            return;
        }

        game.getPlayerWW(player.getUniqueId())
                .ifPresent(playerWW -> {
                    if(!this.damagedPlayers.contains(playerWW)){
                        this.damagedPlayers.add(playerWW);
                        event.setDamage(event.getDamage() * 2);
                    }
                });
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if(playerWW.equals(this.playerWW)){
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return null;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.GRAY_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.barbarian",
                                Formatter.number(config.getDistanceBarbarian())))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceBarbarian((config.getDistanceBarbarian() + 2));
            } else if (config.getDistanceBarbarian() - 2 > 0) {
                config.setDistanceBarbarian(config.getDistanceBarbarian() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.barbarian",
                            Formatter.number(config.getDistanceBarbarian())))
                    .build());

        });
    }
}
