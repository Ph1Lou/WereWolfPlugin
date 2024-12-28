package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.vote.MultiVoteResultEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.SCAPE_GOAT,
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION)
public class ScapeGoat extends RoleImpl implements IPower, IAffectedPlayers {

    private IPlayerWW playerWW;
    private boolean power;

    public ScapeGoat(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.scape_goat.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onMultiVoteResultEvent(MultiVoteResultEvent multiVoteResultEvent) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.setPower(true);

        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.scape_goat.multi_vote_equality");

        multiVoteResultEvent.getPlayerWW()
                .forEach(iPlayerWW -> {
                    TextComponent select = VersionUtils.getVersionUtils().createClickableText(
                            game.translate("werewolf.roles.scape_goat.select", Formatter.player(iPlayerWW.getName())),
                            String.format("/ww %s %s",
                                    game.translate("werewolf.roles.scape_goat.command_name"),
                                    iPlayerWW.getUUID().toString()),
                            ClickEvent.Action.RUN_COMMAND
                    );
                    getPlayerWW().sendMessage(select);
                });

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (this.playerWW != null) {
                        Bukkit.getPluginManager().callEvent(new VoteResultEvent(this.playerWW));
                    } else if (!multiVoteResultEvent.getPlayerWW().isEmpty()) {
                        Bukkit.getPluginManager().callEvent(new VoteResultEvent(this.getPlayerWW()));
                    }
                    this.setPower(false);
                    this.clearAffectedPlayer();
                },
                game.getConfig().getTimerValue(TimerBase.VOTE_WAITING) * 20L);

    }


    @EventHandler(ignoreCancelled = true)
    public void onVoteResultEvent(VoteResultEvent voteResultEvent) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.hasPower()) {
            return;
        }

        if (voteResultEvent.getPlayerWW() == null) {
            this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.scape_goat.vote_no_result");
            this.getPlayerWW().addItem(UniversalMaterial.GOLDEN_APPLE.getStack());
            return;
        }

        List<IPlayerWW> playerWWs = game.getPlayersWW()
                .stream()
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE))
                .filter(iPlayerWW -> !voteResultEvent.getPlayerWW().getRole().getKey().equals(iPlayerWW.getRole().getKey()))
                .collect(Collectors.toList());

        if (playerWWs.isEmpty()) {
            return;
        }

        Collections.shuffle(playerWWs, game.getRandom());
        IPlayerWW playerWW = playerWWs.get(0);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.scape_goat.vote_no_equality",
                Formatter.role(game.translate(playerWW.getRole().getKey())));
        this.getPlayerWW().addItem(UniversalMaterial.GOLDEN_APPLE.getStack());
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
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if (playerWW.equals(this.playerWW)) {
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return Collections.singletonList(this.playerWW);
    }
}
