package io.github.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public static ClickableItem config(WereWolfAPI game) {

        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(Material.STICK)
                        .setLore(game.translate(config.isWitchAutoResurrection() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                        .setDisplayName(game.translate("werewolf.role.witch.auto_rez_witch"))
                        .build(), e -> {
                    config.setWitchAutoResurrection(!config.isWitchAutoResurrection());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate(config.isWitchAutoResurrection() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                            .build());

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
                .setDescription(game.translate("werewolf.role.witch.description"))
                .setPower(game.translate(power ? "werewolf.role.witch.power_available" : "werewolf.role.witch.power_not_available"))
                .setItems(game.translate("werewolf.role.witch.items"))
                .addExtraLines(game.translate("werewolf.description.power",
                        game.translate(game.getConfig().isWitchAutoResurrection()
                                ?
                                "werewolf.role.witch.himself"
                                :
                                "werewolf.role.witch.not_himself")))
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
                                "werewolf.role.witch.resuscitation_message",
                                playerWW.getName()));
        textComponent.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                String.format("/ww %s %s",
                        game.translate("werewolf.role.witch.command"),
                        playerWW.getUUID())));
        this.getPlayerWW().sendMessage(textComponent);
    }

    private boolean autoResurrection(IPlayerWW player) {

        if (!game.getConfig().isWitchAutoResurrection()) {
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

        player.sendMessageWithKey("werewolf.check.cancel");

        return false;
    }

}
