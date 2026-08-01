package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.*;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote1StartEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote2StartEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote3StartEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteCastEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenChangeVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Role(key = RoleBase.CITIZEN,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION,
        configValues = @IntValue(key = IntValueBase.CITIZEN_SEE_VOTE_NUMBER,
                defaultValue = 3,
                meetUpValue = 3,
                step = 1,
                item = UniversalMaterial.ANVIL))
public class Citizen extends RoleImpl implements ILimitedUse, IAffectedPlayers, IPower {

    private static final int REDIRECT_WINDOW = 30;
    private static final int OFFICE_PROXIMITY = 15;

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final Set<Integer> canceledSessions = new HashSet<>();
    private int use = 0;
    private boolean power = true;

    @Nullable
    private IVoteOffice votedOffice;
    private int votedSession = -1;
    private int currentUrnSession = 0;

    @Nullable
    private IPlayerWW redirectTarget;
    private boolean canRedirect = false;

    private final IRegisterManager registerManager;

    public Citizen(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        GetWereWolfAPI getWereWolfAPI = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
        this.registerManager = getWereWolfAPI.getRegisterManager();
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public int getUse() {
        return this.use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
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
        return this.affectedPlayer;
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onDay(DayEvent event) {
        sendHiddenComposition();
    }

    private void sendHiddenComposition() {
        if (!game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) return;

        List<String> present = this.registerManager.getRolesRegister().stream()
                .map(w -> w.getMetaDatas().key())
                .filter(k -> game.getConfig().getRoleCount(k) > 0)
                .collect(Collectors.toList());
        List<String> absent = this.registerManager.getRolesRegister().stream()
                .map(w -> w.getMetaDatas().key())
                .filter(k -> game.getConfig().getRoleCount(k) == 0)
                .collect(Collectors.toList());

        if (present.isEmpty() || absent.size() < 2) return;

        Collections.shuffle(present, game.getRandom());
        Collections.shuffle(absent, game.getRandom());

        List<String> three = new ArrayList<>();
        three.add(present.get(0));
        three.add(absent.get(0));
        three.add(absent.get(1));
        Collections.shuffle(three, game.getRandom());

        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.citizen.hide_composition",
                Formatter.format("&role1&", game.translate(three.get(0))),
                Formatter.format("&role2&", game.translate(three.get(1))),
                Formatter.format("&role3&", game.translate(three.get(2))));
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onVoteEnd(VoteEndEvent event) {
        if (game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) return;

        int waiting = game.getConfig().getTimerValue(TimerBase.VOTE_WAITING);
        long delay = Math.max(0, waiting - REDIRECT_WINDOW) * 20L;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
            if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) return;

            if (this.getUse() < game.getConfig().getValue(IntValueBase.CITIZEN_SEE_VOTE_NUMBER)) {
                this.getPlayerWW().sendMessage(this.buildSeeVoteMessage());
            }
            if (this.hasPower()) {
                this.canRedirect = true;
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.citizen.redirect_window");
            }
        }, delay);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @RequireAbility
    @RequireAlive
    public void onVoteResult(VoteResultEvent event) {
        if (game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) return;

        this.canRedirect = false;

        if (this.redirectTarget != null) {
            CitizenChangeVoteEvent changeVoteEvent = new CitizenChangeVoteEvent(this.getPlayerWW(), this.redirectTarget);
            Bukkit.getPluginManager().callEvent(changeVoteEvent);

            if (changeVoteEvent.isCancelled()) {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            } else {
                event.setPlayerWW(this.redirectTarget);
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.redirect_perform",
                        Formatter.player(this.redirectTarget.getName()));
            }
            this.redirectTarget = null;
        }
    }

    private TextComponent buildSeeVoteMessage() {
        TextComponent seeVote = VersionUtils.getVersionUtils().createClickableText(
                game.translate("werewolf.roles.citizen.click"),
                String.format("/ww %s", game.translate("werewolf.roles.citizen.command_1")),
                ClickEvent.Action.RUN_COMMAND,
                game.translate("werewolf.roles.citizen.see"));
        TextComponent see = new TextComponent(game.translate(Prefix.YELLOW, "werewolf.roles.citizen.see_vote_message",
                Formatter.number(game.getConfig().getValue(IntValueBase.CITIZEN_SEE_VOTE_NUMBER) - this.getUse())));
        see.addExtra(seeVote);
        return see;
    }

    @EventHandler
    public void onUrnVoteCast(UrnVoteCastEvent event) {
        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;
        if (event.getOffice() != null) {
            this.votedOffice = event.getOffice();
            this.votedSession = event.getSession();
        }
    }

    @EventHandler
    public void onUrnVote1Start(UrnVote1StartEvent event) {
        currentUrnSession = 1;
        scheduleUrnVoteInfo(1);
    }

    @EventHandler
    public void onUrnVote2Start(UrnVote2StartEvent event) {
        currentUrnSession = 2;
        scheduleUrnVoteInfo(2);
    }

    @EventHandler
    public void onUrnVote3Start(UrnVote3StartEvent event) {
        currentUrnSession = 3;
        scheduleUrnVoteInfo(3);
    }

    private void scheduleUrnVoteInfo(int session) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if (!isAbilityEnabled()) return;

        int duration = game.getConfig().getTimerValue(TimerBase.URN_VOTE_DURATION);
        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (this.currentUrnSession == session) {
                this.currentUrnSession = 0;
            }
            if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
            if (this.votedOffice == null || this.votedSession != session) return;

            int nonBlank = this.votedOffice.getVotesCast() - this.votedOffice.getBlankVotes();
            Set<IPlayerWW> voted = this.votedOffice.getVotesAgainst().keySet();

            String votedList = voted.stream()
                    .map(IPlayerWW::getName)
                    .collect(Collectors.joining(", "));

            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.urn_info",
                    Formatter.number(nonBlank),
                    Formatter.format("&list&", votedList.isEmpty()
                            ? game.translate("werewolf.roles.citizen.urn_no_target") : votedList));

            this.votedOffice = null;
            this.votedSession = -1;
        }, duration * 20L);
    }

    @EventHandler(ignoreCancelled = true)
    @RequireAbility
    @RequireAlive
    public void onRumor(RumorsWriteEvent event) {

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.rumor",
                Formatter.player(event.getPlayerWW().getName()),
                Formatter.format("&message&", event.getMessage()));
    }

    public boolean canCancelSession(int session) {
        return !this.canceledSessions.contains(session);
    }

    @Nullable
    public IVoteOffice findNearestOffice() {
        Location loc = this.getPlayerWW().getLocation();
        IVoteOffice nearest = null;
        double nearestDist = (double) OFFICE_PROXIMITY;
        for (IVoteOffice office : game.getBuildingsManager().getVoteOffices()) {
            Location oLoc = office.getUrnLocation();
            if (oLoc == null || oLoc.getWorld() != loc.getWorld()) continue;
            double d = loc.distance(oLoc);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = office;
            }
        }
        return nearest;
    }

    public void cancelVotes(IVoteOffice office, int session) {
        office.resetSession();
        this.canceledSessions.add(session);
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.urn_cancel_perform");
    }

    public boolean canRedirectNow() {
        return this.canRedirect;
    }

    public void setRedirectTarget(IPlayerWW target) {
        this.redirectTarget = target;
        this.setPower(false);
    }

    public int getCurrentUrnSession() {
        return this.currentUrnSession;
    }

    @NotNull
    @Override
    public String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.citizen.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.CITIZEN_SEE_VOTE_NUMBER))))
                .addExtraLines(game.translate("werewolf.roles.citizen.description_extra"))
                .setCommand(game.translate("werewolf.roles.citizen.detourner_command"))
                .build();
    }
}
