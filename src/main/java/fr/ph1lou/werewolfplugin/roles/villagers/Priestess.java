package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Role(key = RoleBase.PRIESTESS,
        category = Category.VILLAGER,
        auraDescriptionSpecialUseCase = "werewolf.roles.priestess.aura",
        attribute = RoleAttribute.INFORMATION,
        configValues = {@IntValue(key = IntValueBase.PRIESTESS_DISTANCE, defaultValue = 10, meetUpValue = 10, step = 2, item = UniversalMaterial.BLUE_WOOL)})
public class Priestess extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Priestess(WereWolfAPI main, IPlayerWW playerWW) {
        super(main, playerWW);

        setPower(false);
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


    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!this.affectedPlayer.contains(event.getPlayerWW())) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.priestess.werewolf_death");

        this.affectedPlayer.remove(event.getPlayerWW());

        this.getPlayerWW().addPlayerMaxHealth(2);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW, "werewolf.roles.priestess.perform",
                Formatter.number(game.getConfig().getValue(IntValueBase.PRIESTESS_DISTANCE)),
                Formatter.timer(game, TimerBase.POWER_DURATION));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.priestess.description"))
                .setItems(game.translate("werewolf.roles.priestess.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getAura() {
        return Aura.LIGHT;
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void sendDeathMessage(AnnouncementDeathEvent event) {

        if (event.getTargetPlayer().equals(this.getPlayerWW())) {
            return; //la prêtresse voit les vrais rôles
        }

        if (event.getTargetPlayer().equals(event.getPlayerWW())) {
            return; //le mort voit son vrai rôle
        }

        IPlayerWW playerWW = event.getTargetPlayer();

        if (playerWW.getRole().isNeutral()) {
            if (this.getPlayerWW().isState(StatePlayer.ALIVE) && game.getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.roles.priestess.magic");
            }
        } else if (game.getRandom().nextFloat() < 0.8) {

            if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.roles.priestess.magic");
                }

            } else {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.roles.priestess.magic");
                }
            }
        } else {
            if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.roles.priestess.magic");
                }
            } else {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.roles.priestess.magic");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix("");
    }
}
