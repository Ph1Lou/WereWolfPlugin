package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistAddPlayerOnWerewolfListEvent;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistGetNamesEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Role(key = RoleBase.ILLUSIONIST,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Illusionist extends RoleImpl implements IPower, IAffectedPlayers {

    private boolean power = true;

    private boolean wait = false;

    private IPlayerWW playerWW;

    public Illusionist(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.illusionist.description"))
                .setPower(game.translate("werewolf.roles.illusionist.power"))
                .setCommand(game.translate(this.hasPower() ? "werewolf.roles.illusionist.activate" : "werewolf.roles.illusionist.already_activate"))
                .build();
    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!this.isWait()) {
            return;
        }
        this.setWait(false);

        List<IPlayerWW> playersWW = game.getAlivePlayersWW()
                .stream()
                .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                .map(IPlayerWW::getRole)
                .filter(iRole -> !iRole.isWereWolf())
                .map(IRole::getPlayerWW)
                .collect(Collectors.toList());

        if (playersWW.isEmpty()) {
            return;
        }

        Collections.shuffle(playersWW, game.getRandom());

        IPlayerWW playerWW = playersWW.get(0);

        IllusionistAddPlayerOnWerewolfListEvent illusionistAddPlayerOnWerewolfListEvent =
                new IllusionistAddPlayerOnWerewolfListEvent(this.getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(illusionistAddPlayerOnWerewolfListEvent);

        if (illusionistAddPlayerOnWerewolfListEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.addAffectedPlayer(playerWW);

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));

        this.game.getAlivePlayersWW().stream()
                .filter(playerWW1 -> playerWW1.getRole().isWereWolf())
                .forEach(player1 -> {
                    player1.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.new_werewolf");
                    Sound.WOLF_HOWL.play(player1);
                });

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {

            if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
                return;
            }
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.illusionist.reveal");
            List<IPlayerWW> players1WW = game.getAlivePlayersWW()
                    .stream()
                    .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                    .filter(playerWW1 -> !playerWW1.equals(playerWW))
                    .map(IPlayerWW::getRole)
                    .map(IRole::getPlayerWW)
                    .collect(Collectors.toList());

            if (players1WW.size() < 2) {
                return;
            }

            Collections.shuffle(players1WW, game.getRandom());

            List<IPlayerWW> finalPlayersWW = new ArrayList<>(Arrays.asList(playerWW, players1WW.get(0), players1WW.get(1)));

            Collections.shuffle(finalPlayersWW, game.getRandom());

            IllusionistGetNamesEvent illusionistGetNamesEvent =
                    new IllusionistGetNamesEvent(this.getPlayerWW(), new HashSet<>(finalPlayersWW));

            Bukkit.getPluginManager().callEvent(illusionistGetNamesEvent);

            if (illusionistGetNamesEvent.isCancelled()) {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                return;
            }

            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                    "werewolf.roles.illusionist.reveal_pseudos",
                    Formatter.format("&names&", finalPlayersWW.stream().map(IPlayerWW::getName)
                            .collect(Collectors.joining(", "))));
        }, 20 * 60L);
    }

    @EventHandler
    public void onWerewolfListRequest(AppearInWereWolfListEvent event) {

        if (event.getTargetWW().equals(this.playerWW)) {
            event.setAppear(true);
        }
    }

    @Override
    public void recoverPower() {

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
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.playerWW = iPlayerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        if (iPlayerWW.equals(this.playerWW)) {
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if (this.playerWW == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.playerWW);
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }
}
