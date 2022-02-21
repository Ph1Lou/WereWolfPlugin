package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TenebrousWerewolf extends RoleWereWolf implements IPower, IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power = true;

    public TenebrousWerewolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.tenebrous_werewolf.description",
                        Formatter.format("&range&", game.getConfig().getDistanceTenebrous()),
                        Formatter.format("&time&", game.getConfig().getTenebrousDuration()/20)))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setCommand(game.translate("werewolf.role.tenebrous_werewolf.description_command"))
                .setPower(game.translate(power ? "werewolf.role.tenebrous_werewolf.power_available" : "werewolf.role.tenebrous_werewolf.power_not_available"))
                .build();
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player target = (Player) event.getEntity();
        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
        if (targetWW == null) return;

        if (!affectedPlayers.contains(targetWW)) return;

        Player damager = (Player) event.getDamager();
        IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);
        if (damagerWW == null) return;

        if (damagerWW.getRole().isWereWolf()) {
            targetWW.addPotionModifier(PotionModifier.remove(PotionEffectType.BLINDNESS, "tenebrous", 1));
            affectedPlayers.remove(targetWW);
        }
    }

    public static ClickableItem configDistance(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(UniversalMaterial.BLACK_WOOL.getType())
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.role.tenebrous_werewolf.darkness_distance",
                                Formatter.format("&range&",config.getDistanceTenebrous())))
                        .build(), e -> {
                    if (e.isLeftClick()) {
                        config.setDistanceTenebrous(config.getDistanceTenebrous() + 5);
                    } else if (config.getDistanceTenebrous() > 5) {
                        config.setDistanceTenebrous(config.getDistanceTenebrous() - 5);
                    }


                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setDisplayName(game.translate("werewolf.role.tenebrous_werewolf.darkness_distance",
                                    Formatter.format("&range&",config.getDistanceTenebrous())))
                            .build());

                });
    }

    public static ClickableItem configDuration(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(UniversalMaterial.CLOCK.getType())
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.role.tenebrous_werewolf.darkness_duration",
                                Formatter.format("&time&",config.getTenebrousDuration()/20)))
                        .build(), e -> {
                    if (e.isLeftClick()) {
                        config.setTenebrousDuration(config.getTenebrousDuration() + 100);
                    } else if (config.getTenebrousDuration() > 100) {
                        config.setTenebrousDuration(config.getTenebrousDuration() - 100);
                    }


                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setDisplayName(game.translate("werewolf.role.tenebrous_werewolf.darkness_duration",
                                    Formatter.format("&time&",config.getTenebrousDuration()/20)))
                            .build());

                });
    }
}