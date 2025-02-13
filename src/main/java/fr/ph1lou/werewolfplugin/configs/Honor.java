package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.honor.HonorChangeEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.game.spy.SpyInfoEvent;
import fr.ph1lou.werewolfapi.events.game.vote.AbsentionistListVoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestStrengthRateEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.HONOR, loreKey = "werewolf.configurations.honor.description"))
public class Honor extends ListenerWerewolf {

    private final Set<IPlayerWW> playerWithExtraHeart = new HashSet<>();
    private final Set<IPlayerWW> playerWithFewerHeart = new HashSet<>();


    public Honor(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFinalDeathEvent(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.getLastKiller()
                .ifPresent(killerWW -> {
                    switch (playerWW.getRole().getCamp()) {
                        case WEREWOLF:
                            killerWW.modifyHonor(1);
                            break;
                        case NEUTRAL:
                            killerWW.modifyHonor(2);
                            break;
                        case VILLAGER:
                            killerWW.modifyHonor(-1);
                            break;
                    }
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVoteResult(VoteResultEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.modifyHonor(-2);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVoteResult(VoteEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.modifyHonor(1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAbstentionistList(AbsentionistListVoteEvent event) {

        event.getAbstentionistList()
                .stream()
                .filter(playerWW -> playerWW.getRole().isCamp(Camp.VILLAGER))
                .forEach(playerWW -> playerWW.modifyHonor(-1));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVoteEvent(VoteEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getRole().isCamp(Camp.VILLAGER)) return;

        switch (playerWW.getHonor()) {
            case -1:
            case -2:
            case -3:
                return;
            case 1:
                if (getGame().getRandom().nextInt(5) > 0) {
                    return;
                }
            case 2:
                if (getGame().getRandom().nextBoolean()) {
                    return;
                }
        }
        IVoteManager voteManager = getGame().getVoteManager();
        voteManager.setVotes(event.getTargetWW(), 1 + voteManager.getVotes(event.getTargetWW()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onAnnouncementDeathEvent(AnnouncementDeathEvent event) {

        IPlayerWW targetPlayer = event.getTargetPlayer();

        if (targetPlayer.getRole().isNeutral()) {
            if (getGame().getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
            return;
        }

        boolean priestressIsDead = getGame().getPlayersWW().stream()
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.PRIESTESS))
                .anyMatch(playerWW -> playerWW.isState(StatePlayer.DEATH));

        if (priestressIsDead) {
            if (getGame().getRandom().nextFloat() > 0.5 - targetPlayer.getHonor() * 0.1) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
        } else {
            if (getGame().getRandom().nextFloat() > 0.5 + targetPlayer.getHonor() * 0.1) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onAnnouncementDeathEvent2(AnnouncementDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getRole().isCamp(Camp.VILLAGER)) {
            return;
        }

        if (playerWW.getHonor() == -1) {
            if (getGame().getRandom().nextFloat() > 0.5) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
            return;
        }

        if (playerWW.getHonor() == -2) {
            if (getGame().getRandom().nextBoolean()) {
                event.setPlayerName(getGame().translate("werewolf.configurations.hidden_roles.magic"));
            }
            return;
        }

        if (playerWW.getHonor() == -3) {
            event.setCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onVote(VoteEvent event) {
        if (event.getPlayerWW().getHonor() == -3) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoverDeathMessage(AnnouncementLoverDeathEvent event) {

        if (!event.getPlayerWW().getRole().isCamp(Camp.VILLAGER)) {
            return;
        }
        if (event.getPlayerWW().getHonor() == -3) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = getGame().getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.getRole().isCamp(Camp.VILLAGER)) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        if (playerWW.getHonor() == -3) {
            event.setTabVisibility(false);
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFirstDeath(FirstDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getRole().isCamp(Camp.VILLAGER)) {
            return;
        }

        if (playerWW.getHonor() == -3) {
            return;
        }

        event.setCancelled(true);
        BukkitUtils.scheduleSyncDelayedTask(getGame(), () -> {
            if (playerWW.isState(StatePlayer.JUDGEMENT)) {
                getGame().death(playerWW);
            }
        }, 14 * 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVoteBegin(VoteBeginEvent event) {

        getGame().getAlivePlayersWW()
                .stream()
                .filter(playerWW -> playerWW.getRole().isCamp(Camp.VILLAGER))
                .filter(playerWW -> playerWW.getHonor() < -1)
                .forEach(playerWW -> getGame().getVoteManager().setVotes(playerWW, getGame().getVoteManager().getVotes(playerWW) + 1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHonorChange(HonorChangeEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getRole().isCamp(Camp.VILLAGER)) {
            return;
        }

        if (playerWW.getHonor() == -3 && !playerWithFewerHeart.contains(playerWW)) {
            playerWithFewerHeart.add(playerWW);
            playerWW.removePlayerMaxHealth(2);
        }

        if (playerWW.getHonor() == 3 && !playerWithExtraHeart.contains(playerWW)) {
            playerWithExtraHeart.add(playerWW);
            playerWW.addPlayerMaxHealth(2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHonorChangeNeutral(HonorChangeEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        IRole role = playerWW.getRole();

        if (!role.isNeutral()) {
            return;
        }

        if (event.getOldHonor() == 3) {
            role.setDisplayRole(null);
            role.setDisplayCamp(null);
            return;
        }

        if (playerWW.getHonor() == 3) {
            role.setDisplayRole(this.getGame().getAlivePlayersWW()
                    .stream()
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isCamp(Camp.VILLAGER))
                    .map(IRole::getKey)
                    .findFirst()
                    .orElse(RoleBase.VILLAGER));
            role.setDisplayCamp(Camp.VILLAGER.getKey());
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPatchPotion(RequestStrengthRateEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getRole().isWereWolf()) {
            return;
        }

        switch (playerWW.getHonor()) {
            case 1:
                event.setStrengthRate(event.getStrengthRate() * 11 / 12);
                break;
            case 2:
                event.setStrengthRate(event.getStrengthRate() * 5 / 6);
                break;
            case 3:
                event.setStrengthRate(event.getStrengthRate() * 3 / 4);
                break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onNight(NightEvent event) {

        getGame().getAlivePlayersWW()
                .stream()
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(playerWW -> {
                    switch (playerWW.getHonor()) {
                        case -2:
                        case -3:
                            playerWW.addPlayerHealth(2);
                            break;
                        case 3:
                            String playerNearWWList = getGame().getPlayersWW()
                                    .stream()
                                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                                    .filter(playerWW1 -> playerWW1.distance(playerWW) < 80)
                                    .map(IPlayerWW::getName)
                                    .collect(Collectors.joining(", "));
                            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.configurations.honor.list_near_player", Formatter.format("&list&", playerNearWWList));
                            break;
                    }
                });
    }

    @EventHandler(ignoreCancelled = true)
    private void onDay(DayEvent event) {

        getGame().getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isNeutral())
                .forEach(playerWW -> {
                    switch (playerWW.getHonor()) {
                        case -2:
                        case -3:
                            playerWW.addPlayerHealth(2);
                            break;
                        case 3:
                            String playerNearWWList = getGame().getAlivePlayersWW()
                                    .stream()
                                    .filter(playerWW1 -> playerWW1.distance(playerWW) < 80)
                                    .map(IPlayerWW::getName)
                                    .collect(Collectors.joining(", "));
                            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.configurations.honor.list_near_player", Formatter.format("&list&", playerNearWWList));
                            break;
                    }
                });
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = getGame().getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (playerWW.getRole().isCamp(Camp.VILLAGER)) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        if (playerWW.getHonor() != -3) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeathBySolo(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();

        IPlayerWW playerWW = getGame().getPlayerWW(killer.getUniqueId()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.getRole().isNeutral()) {
            return;
        }

        switch (playerWW.getHonor()) {
            case -1:
                playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 30 * 20, 0, playerWW.getRole().getKey()));
                playerWW.addPlayerAbsorptionHealth(2);
                break;
            case -2:
                playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 30 * 20, 0, playerWW.getRole().getKey()));
                playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 30 * 20, 0, playerWW.getRole().getKey()));
                break;
            case -3:
                playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 60 * 20, 0, playerWW.getRole().getKey()));
                playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 60 * 20, 0, playerWW.getRole().getKey()));
                break;

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpyInfo(SpyInfoEvent event) {

        IPlayerWW target = event.getTargetWW();

        if (!target.getRole().isNeutral()) {
            return;
        }

        if (target.getHonor() < 1) {
            return;
        }

        target.sendMessageWithKey(Prefix.RED, "werewolf.configurations.honor.spy_info");
    }

}
