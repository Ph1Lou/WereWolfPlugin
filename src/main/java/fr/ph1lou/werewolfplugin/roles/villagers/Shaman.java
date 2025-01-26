package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.SHAMAN,
        auraDescriptionSpecialUseCase = "werewolf.roles.shaman.aura",
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION)
public class Shaman extends RoleImpl implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public Shaman(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.shaman.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onFirstDeathEvent(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getLastKiller().isPresent()) {
            return;
        }

        TextComponent textComponent = VersionUtils.getVersionUtils().createClickableText(
                game.translate(Prefix.YELLOW, "werewolf.roles.shaman.choice_message"),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.shaman.command"), playerWW.getUUID()),
                ClickEvent.Action.RUN_COMMAND
        );

        this.getPlayerWW().sendMessage(textComponent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (!event.getTargetPlayer().equals(getPlayerWW())) {
            return;
        }

        if (!isAbilityEnabled()) return;

        event.setFormat("werewolf.announcement.death_message_with_role");
        event.setRole(event.getPlayerWW().getRole().getKey());
        event.setPlayerName(event.getPlayerWW().getName());
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
}
