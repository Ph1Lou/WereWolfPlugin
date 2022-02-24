package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cupid extends RoleVillage implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Cupid(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
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

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.cupid.description"))
                .setItems(game.translate("werewolf.role.cupid.items"))
                .setEquipments(game.translate("werewolf.role.cupid.extra",
                                Formatter.number(game.getConfig().getLimitPowerBow() + 1)))
                .addExtraLines(game.translate("werewolf.role.cupid.lover",
                                Formatter.format("&lovers&",this.affectedPlayer.isEmpty() ?
                                this.hasPower() ?
                                        game.getConfig().isConfigActive(ConfigBase.RANDOM_CUPID.getKey()) ?
                                        game.translate("werewolf.role.cupid.wait",Formatter.timer(Utils.conversion(
                                                game.getConfig()
                                                        .getTimerValue("werewolf.menu.timers.lover_duration")))):
                                        game.translate("werewolf.role.cupid.lover_designation_message",
                                                Formatter.timer(Utils.conversion(
                                                        game.getConfig()
                                                                .getTimerValue("werewolf.menu.timers.lover_duration")))) :
                                        game.translate("werewolf.role.cupid.none") :
                                affectedPlayer.stream().map(IPlayerWW::getName)
                                        .collect(Collectors.joining(" ")))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onAroundLoverEvent(AroundLoverEvent event) {

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
    public void onEnchantment(EnchantmentEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }

}
