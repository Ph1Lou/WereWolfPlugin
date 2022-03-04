package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Witch extends RoleVillage implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Witch(WereWolfAPI api, IPlayerWW playerWW, String key) {
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
                .setDescription(game.translate("werewolf.role.witch.description"))
                .setPower(game.translate(power ? "werewolf.role.witch.power_available" : "werewolf.role.witch.power_not_available"))
                .setItems(game.translate("werewolf.role.witch.items"))
                .addExtraLines(game.translate("werewolf.description.power",
                                Formatter.format("&on&",game.translate(game.getConfig().isConfigActive(ConfigBase.WITCH_AUTO_RESURRECTION.getKey())
                                ?
                                "werewolf.role.witch.himself"
                                :
                                "werewolf.role.witch.not_himself"))))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }


    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(autoResurrection(playerWW));
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        TextComponent textComponent =
                new TextComponent(
                        game.translate(
                                Prefix.YELLOW.getKey() , "werewolf.role.witch.resuscitation_message",
                                Formatter.player(playerWW.getName())));
        textComponent.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s %s",
                        game.translate("werewolf.role.witch.command"),
                        playerWW.getUUID())));
        this.getPlayerWW().sendMessage(textComponent);
    }

    private boolean autoResurrection(IPlayerWW player) {

        if (!game.getConfig().isConfigActive(ConfigBase.WITCH_AUTO_RESURRECTION.getKey())) {
            return false;
        }

        WitchResurrectionEvent witchResurrectionEvent =
                new WitchResurrectionEvent(this.getPlayerWW(),
                        getPlayerWW());
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);
        setPower(false);

        if (!witchResurrectionEvent.isCancelled()) {
            game.resurrection(getPlayerWW());
            return true;
        }

        player.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");

        return false;
    }

}
