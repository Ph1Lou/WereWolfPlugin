package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.ElectionState;
import fr.ph1lou.werewolfapi.enums.MayorState;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorDeathEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorElectionApplicationBeginEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorElectionVoteBeginEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorElectionVoteEndEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorExtraGoldenAppleEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorGoldenAppleEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorResurrectionEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorSelectionEvent;
import fr.ph1lou.werewolfapi.events.elections.MayorVoteEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.ELECTIONS, loreKey = "werewolf.elections.lore"),
        timers = {
                @Timer(key = TimerBase.ELECTIONS_BEGIN, defaultValue = 1800,
                        meetUpValue = 180,
                        decrementAfterRole = true,
                        onZero = MayorElectionApplicationBeginEvent.class),
                @Timer(key = TimerBase.ELECTIONS_DURATION_APPLICATION,
                        defaultValue = 120,
                        meetUpValue = 120,
                        decrementAfterTimer = TimerBase.ELECTIONS_BEGIN,
                        onZero = MayorElectionVoteBeginEvent.class),
                @Timer(key = TimerBase.ELECTIONS_DURATION_CHOICE,
                        defaultValue = 90,
                        meetUpValue = 90,
                        onZero = MayorElectionVoteEndEvent.class,
                        decrementAfterTimer = TimerBase.ELECTIONS_DURATION_APPLICATION)})
public class Elections extends ListenerWerewolf {
    private static final String MAYOR = "mayor";
    private final Map<IPlayerWW, String> playerMessages = new HashMap<>();
    private final Map<IPlayerWW, IPlayerWW> votes = new HashMap<>();
    private final MayorState mayorState = MayorState.values()[(int) Math.floor(new Random(System.currentTimeMillis()).nextFloat() * MayorState.values().length)];
    private ElectionState electionState = ElectionState.NOT_BEGIN;
    private IPlayerWW mayor;
    private boolean power = true;

    public Elections(WereWolfAPI game) {
        super(game);
    }

    public void setState(ElectionState electionState) {
        this.electionState = electionState;
    }

    public Optional<IPlayerWW> getMayor() {
        return Optional.ofNullable(this.mayor);
    }

    public void setMayor(@Nullable IPlayerWW mayor) {
        if (this.mayor != null) {
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.mayor));
        }
        this.mayor = mayor;
        if (mayor != null) {
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(mayor));
        }
    }

    public void addMessage(IPlayerWW playerWW, String message) {
        this.playerMessages.put(playerWW, message);
    }

    public void addVote(IPlayerWW playerWW, IPlayerWW target) {

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (playerWW.equals(target)) {
            return;
        }
        this.votes.put(playerWW, target);

        Bukkit.getPluginManager().callEvent(new MayorVoteEvent(playerWW, target));
    }

    public Optional<String> getPlayerMessage(IPlayerWW playerWW) {
        if (this.playerMessages.containsKey(playerWW)) {
            return Optional.of(this.playerMessages.get(playerWW));
        }
        return Optional.empty();
    }

    public Set<IPlayerWW> getCandidates() {
        return this.playerMessages.keySet();
    }

    public Set<IPlayerWW> getVoters(IPlayerWW playerWW) {
        Set<IPlayerWW> voters = new HashSet<>();
        this.votes.forEach((playerWW1, playerWW2) -> {
            if (playerWW2.equals(playerWW)) {
                voters.add(playerWW1);
            }
        });
        return voters;
    }

    public boolean isState(ElectionState state) {
        return this.electionState == state;
    }


    public void getResult() {
        Map<IPlayerWW, Integer> votes = new HashMap<>();
        AtomicInteger max = new AtomicInteger();
        AtomicReference<IPlayerWW> mayor = new AtomicReference<>();
        this.votes.values().forEach(playerWW -> votes.merge(playerWW, 1, Integer::sum));

        votes.forEach((playerWW, integer) -> {
            if (integer > max.get()) {
                max.set(integer);
                mayor.set(playerWW);
            }
        });

        if (max.get() == 0) return;

        this.setState(ElectionState.FINISH);
        this.setMayor(mayor.get());
        Bukkit.getPluginManager().callEvent(new MayorSelectionEvent(mayor.get(), this.mayorState, max.get()));

        if (this.mayorState == MayorState.FARMER) {
            mayor.get().addPotionModifier(PotionModifier.add(PotionEffectType.SATURATION, MAYOR));
        }

        Bukkit.broadcastMessage(getGame().translate(Prefix.BLUE, "werewolf.elections.election.result",
                Formatter.format("&name&", mayor.get().getName()),
                Formatter.format("&votes&", max.get()),
                Formatter.format("&forme&", getGame().translate(this.getMayorState().getKey()))));

        mayor.get().sendMessageWithKey(Prefix.LIGHT_BLUE, this.mayorState.getDescription());
    }

    public boolean isPower() {
        return power;
    }

    public void unSetPower() {
        this.power = false;
    }

    public MayorState getMayorState() {
        return mayorState;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNameTagUpdate(UpdatePlayerNameTagEvent event) {
        this.getMayor().ifPresent(playerWW -> {
            if (playerWW.getUUID().equals(event.getPlayerUUID())) {
                event.setPrefix(event.getPrefix() + this.getGame().translate("werewolf.elections.election.star"));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFinalDeath(FinalDeathEvent event) {
        this.getMayor().ifPresent(playerWW -> {
            if (event.getPlayerWW().equals(playerWW)) {
                this.setMayor(null);
                Bukkit.broadcastMessage(this.getGame().translate(Prefix.RED, "werewolf.elections.election.death"));
                Bukkit.getPluginManager().callEvent(new MayorDeathEvent(event.getPlayerWW()));
            } else if (playerWW.isState(StatePlayer.ALIVE) && this.getMayorState() == MayorState.UNDERTAKER) {

                if (event.getPlayerWW().getRole().getCamp().equals(playerWW.getRole().getCamp())) {
                    playerWW.addItem(new ItemStack(Material.GOLDEN_APPLE));
                    playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.elections.election.regime.undertaker.message");
                    Bukkit.getPluginManager().callEvent(new MayorGoldenAppleEvent(playerWW, event.getPlayerWW()));

                }
            }
        });
    }


    @EventHandler
    public void onGoldenAppleCraft(CraftItemEvent event) {
        this.getMayor().ifPresent(playerWW -> {

            if (this.getMayorState() != MayorState.FARMER) {
                return;
            }

            if (event.getWhoClicked().getUniqueId().equals(playerWW.getUUID())) {

                if (!event.getRecipe().getResult().getType().equals(Material.GOLDEN_APPLE)) {
                    return;
                }

                if (this.getGame().getRandom().nextFloat() * 100 < 20) {
                    playerWW.addItem(new ItemStack(Material.GOLDEN_APPLE));
                    Bukkit.getPluginManager().callEvent(new MayorExtraGoldenAppleEvent(playerWW));
                }
            }
        });
    }

    @EventHandler
    public void onMayorVoteEnd(MayorElectionVoteEndEvent event) {
        this.getResult();
    }

    @EventHandler
    public void onElectionBegin(MayorElectionApplicationBeginEvent event) {

        WereWolfAPI game = this.getGame();

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.elections.election.begin", Formatter.format("&timer&",
                Utils.conversion(game.getConfig().getTimerValue(TimerBase.ELECTIONS_DURATION_APPLICATION)))));
        this.setState(ElectionState.MESSAGE);
    }

    @EventHandler
    public void onMayorVoteBegin(MayorElectionVoteBeginEvent event) {

        WereWolfAPI game = this.getGame();

        this.setState(ElectionState.ELECTION);
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.elections.election.vote",
                Formatter.format("&timer&",
                        Utils.conversion(game.getConfig().getTimerValue(TimerBase.ELECTIONS_DURATION_CHOICE)))));
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        this.getMayor().ifPresent(playerWW -> {

            if (!this.isPower()) {
                return;
            }

            if (this.getMayorState() != MayorState.DOCTOR) {
                return;
            }

            if (!playerWW.equals(event.getPlayerWW())) {
                return;
            }

            this.getGame().resurrection(playerWW);

            event.setCancelled(true);

            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.elections.election.regime.doctor.resurrection");

            Bukkit.getPluginManager().callEvent(new MayorResurrectionEvent(event.getPlayerWW()));


            this.unSetPower();
        });
    }

    @EventHandler
    private void onMayorBlackSmith(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;


        Player player = (Player) event.getEntity();

        this.getMayor().ifPresent(playerWW -> {

            if (this.getMayorState() != MayorState.BLACK_SMITH) {
                return;
            }

            if (!playerWW.getUUID().equals(player.getUniqueId())) {
                return;
            }

            event.setDamage(event.getDamage() * 90 / 100f);
        });
    }


    @EventHandler
    public void onVote(VoteEvent event) {

        this.getMayor().ifPresent(playerWW -> {

            if (playerWW.equals(event.getPlayerWW())) {
                Map<IPlayerWW, Integer> votes = this.getGame().getVoteManager().getVotes();
                votes.put(event.getTargetWW(), votes.getOrDefault(event.getTargetWW(), 0) + 1);
            }
        });
    }
}
