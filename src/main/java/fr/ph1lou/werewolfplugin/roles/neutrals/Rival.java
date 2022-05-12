package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfplugin.roles.lovers.Lover;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalAnnouncementEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.RIVAL,
        category = Category.NEUTRAL,
        attributes = {RoleAttribute.NEUTRAL},
        timers = {@Timer(key = TimerBase.RIVAL_DURATION,
                defaultValue = 2400, meetUpValue = 2400,
        decrementAfterRole = true,
        onZero = RivalEvent.class)},
        requireRoles = {RoleBase.CUPID})
public class Rival extends RoleNeutral implements IPower {

    @Nullable
    private IPlayerWW cupidWW = null;

    @Nullable
    private ILover lover = null;

    private boolean power = true;

    public Rival(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @EventHandler
    public void onSwapEvent(SwapEvent event) {
        if (event.getPlayerWW1().equals(this.cupidWW)) {
            this.cupidWW = event.getPlayerWW2();
        } else if (event.getPlayerWW2().equals(this.cupidWW)) {
            this.cupidWW = event.getPlayerWW1();
        }
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.rival.description",
                                Formatter.timer(Utils.conversion(
                                Math.max(0, game.getConfig().getTimerValue(TimerBase.ROLE_DURATION))
                                        + game.getConfig().getTimerValue(TimerBase.RIVAL_DURATION)))))
                .setItems(game.translate("werewolf.role.rival.item"))
                .setEquipments(game.translate("werewolf.role.rival.extra",
                                Formatter.number(game.getConfig().getLimitPowerBow() + 1)))
                .build();
    }

    @EventHandler
    public void onLoverReveal(RevealLoversEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getLovers().isEmpty()) return;

        List<ILover> loverAPIs = event.getLovers().stream()
                .filter(ILover -> !ILover.isKey(LoverType.CURSED_LOVER.getKey()))
                .filter(loverAPI1 -> !loverAPI1.getLovers().contains(getPlayerWW()))
                .collect(Collectors.toList());

        if (loverAPIs.isEmpty()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.role.rival.error");
            return;
        }

        this.lover = loverAPIs.get((int) Math.floor(game.getRandom().nextFloat() * loverAPIs.size()));

        if (lover instanceof Lover) {
            this.cupidWW = game.getPlayersWW()
                    .stream().map(IPlayerWW::getRole)
                    .filter(roles -> roles.isKey(RoleBase.CUPID))
                    .map(roles -> (IAffectedPlayers) roles)
                    .filter(affectedPlayers -> affectedPlayers.getAffectedPlayers().contains(lover.getLovers().get(0)))
                    .map(affectedPlayers -> ((IRole) affectedPlayers).getPlayerWW())
                    .findFirst()
                    .orElse(null);
        }

        List<IPlayerWW> lovers = new ArrayList<>(lover.getLovers());

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.role.rival.lover",
                Formatter.format("&role1&",lovers.isEmpty() ? "" : game.translate(lovers.get(0).getRole().getKey())),
                Formatter.format("&role2&",lovers.size() == 2 ? game.translate(lovers.get(1).getRole().getKey()) : ""));
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }

    @EventHandler
    public void onMultiChoice(RivalEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (lover == null) return;

        List<IPlayerWW> playerWWS = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.equals(getPlayerWW()))
                .filter(playerWW -> !lover.getLovers().contains(playerWW))
                .collect(Collectors.toList());

        Collections.shuffle(playerWWS, game.getRandom());

        List<IPlayerWW> playerWW1S = new ArrayList<>(playerWWS.subList(0, Math.min(3, playerWWS.size())));

        playerWW1S.addAll(lover.getLovers());

        Collections.shuffle(playerWW1S, game.getRandom());

        if (playerWW1S.isEmpty()) return;

        RivalAnnouncementEvent rivalAnnouncementEvent = new RivalAnnouncementEvent(this.getPlayerWW(), playerWW1S);

        Bukkit.getPluginManager().callEvent(rivalAnnouncementEvent);

        if (rivalAnnouncementEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.role.rival.find_lovers",
                Formatter.format("&player1&",playerWW1S.get(0).getName()),
                Formatter.format("&player2&",playerWW1S.size() >= 2 ? playerWW1S.get(1).getName() : ""),
                Formatter.format("&player3&",playerWW1S.size() >= 3 ? playerWW1S.get(2).getName() : ""),
                Formatter.format("&player4&",playerWW1S.size() >= 4 ? playerWW1S.get(3).getName() : ""),
                Formatter.format("&player5&",playerWW1S.size() >= 5 ? playerWW1S.get(4).getName() : ""));
    }

    @EventHandler
    public void onLoverDeath(LoverDeathEvent event) {

        String loverKey = event.getLover().getKey();
        if(loverKey.equals(LoverType.LOVER.getKey()) ||
        loverKey.equals(LoverType.AMNESIAC_LOVER.getKey())){
            List<? extends IPlayerWW> lovers = event.getLover().getLovers();
            if(lovers.size() >= 2){
                loverDeath(lovers.get(0), lovers.get(1));
            }
        }
    }

    private void loverDeath(IPlayerWW playerWW1, IPlayerWW playerWW2) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!power) return;

        if (lover == null) return;

        if (!lover.getLovers().contains(playerWW1) || !lover.getLovers().contains(playerWW2)) return;


        int health = 5;
        if (this.getPlayerWW().getMaxHealth() < 10) { //si le joueur a moins de coeurs ont réduit le temps de récupération de coeurs
            health = this.getPlayerWW().getMaxHealth() / 2 - 1; //-1 car le joueur aura un coeur minimum quand il prend les votes
        }
        this.getPlayerWW().removePlayerMaxHealth(10);

        int task = BukkitUtils.scheduleSyncRepeatingTask(() -> {
            if (game.isState(StateGame.GAME)) {
                this.getPlayerWW().addPlayerMaxHealth(2);
            }
        }, 1200, 1200);

        BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getScheduler().cancelTask(task), (long) health * 62 * 20);

        this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.role.rival.lover_death");
        Bukkit.getPluginManager().callEvent(new RivalLoverDeathEvent(this.getPlayerWW(), new ArrayList<>(lover.getLovers())));
        lover = null;
    }

    @EventHandler
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {
        loverDeath(event.getPlayerWW1(), event.getPlayerWW2());
    }

    @EventHandler
    public void onSteal(StealEvent event) {

        if (!event.getPlayerWW().equals(cupidWW)) return;

        cupidWW = event.getThiefWW();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoverDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (lover == null) return;

        if (!lover.getLovers().contains(event.getPlayerWW())) return;

        IPlayerWW playerWW = event.getPlayerWW();

        Optional<IPlayerWW> killerWW = playerWW.getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().equals(getPlayerWW())) return;

        RivalLoverEvent rivalLoverEvent = new RivalLoverEvent(this.getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(rivalLoverEvent);

        if (rivalLoverEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        if (lover.swap(playerWW, getPlayerWW())) {
            this.getPlayerWW().addLover(lover);
            playerWW.removeLover(lover);
            power = false;
        }
    }


    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (cupidWW == null) return;

        if (!isAbilityEnabled()) return;

        if (cupidWW.isState(StatePlayer.ALIVE)) {

            stringBuilder
                    .append(" ")
                    .append(ChatColor.WHITE)
                    .append(game.translate(RoleBase.CUPID))
                    .append(" ")
                    .append(Utils.updateArrow(player,
                            cupidWW.getLocation()));
        }

        event.setActionBar(stringBuilder.toString());
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
