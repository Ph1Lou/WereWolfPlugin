package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Role(key = RoleBase.TROUBLEMAKER,
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Troublemaker extends RoleImpl implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Troublemaker(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new TroubleMakerDeathEvent(getPlayerWW()));
        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.troublemaker.troublemaker_death"));

        AtomicInteger i = new AtomicInteger();

        game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .forEach(playerWW -> {
                    game.getMapManager().transportation(playerWW,
                            i.get() * 2 * Math.PI / game.getPlayersCount());
                    i.getAndIncrement();
                });
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
                .setDescription(game.translate("werewolf.roles.troublemaker.description"))
                .setPower(game.translate("werewolf.roles.troublemaker.chat"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    public void onRequestAccessWereWolfChat(WereWolfCanSpeakInChatEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (!isAbilityEnabled()) return;

        event.setCanSpeak(true);
    }
}
