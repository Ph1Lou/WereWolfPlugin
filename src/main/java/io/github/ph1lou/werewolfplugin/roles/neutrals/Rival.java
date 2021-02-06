package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.AmnesiacLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.LoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.events.RivalAnnouncementEvent;
import io.github.ph1lou.werewolfapi.events.RivalEvent;
import io.github.ph1lou.werewolfapi.events.RivalLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.RivalLoverEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.events.SwapEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
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

public class Rival extends RolesNeutral implements Power {

    @Nullable
    private PlayerWW cupidWW = null;

    @Nullable
    private LoverAPI loverAPI = null;

    private boolean power = true;

    public Rival(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
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
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.rival.description",
                                game.getScore().conversion(
                                        Math.abs(game.getConfig().getTimerValue(TimersBase.ROLE_DURATION.getKey()))
                                                + Math.abs(game.getConfig().getTimerValue(TimersBase.RIVAL_DURATION.getKey())))))
                + game.translate("werewolf.description.item", game.translate("werewolf.role.rival.item")) +
                game.translate("werewolf.description.equipment",
                        game.translate("werewolf.role.rival.extra", game.getConfig().getLimitPowerBow() + 1));

    }

    @EventHandler
    public void onLoverReveal(RevealLoversEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getLovers().isEmpty()) return;

        List<LoverAPI> loverAPIs = event.getLovers().stream()
                .filter(loverAPI -> !loverAPI.isKey(LoverType.CURSED_LOVER.getKey()))
                .filter(loverAPI1 -> !loverAPI1.getLovers().contains(getPlayerWW()))
                .collect(Collectors.toList());

        if (loverAPIs.isEmpty()) return;

        this.loverAPI = loverAPIs.get((int) Math.floor(game.getRandom().nextFloat() * loverAPIs.size()));

        if (loverAPI instanceof Lover) {
            this.cupidWW = game.getPlayerWW()
                    .stream().map(PlayerWW::getRole)
                    .filter(roles -> roles.isKey(RolesBase.CUPID.getKey()))
                    .map(roles -> (AffectedPlayers) roles)
                    .filter(affectedPlayers -> affectedPlayers.getAffectedPlayers().contains(loverAPI.getLovers().get(0)))
                    .map(affectedPlayers -> ((Roles) affectedPlayers).getPlayerWW())
                    .findFirst()
                    .orElse(null);
        }

        List<PlayerWW> lovers = new ArrayList<>(loverAPI.getLovers());

        getPlayerWW().sendMessageWithKey("werewolf.role.rival.lover", lovers.isEmpty() ? "" : game.translate(lovers.get(0).getRole().getKey()), lovers.size() == 2 ? game.translate(lovers.get(1).getRole().getKey()) : "");
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

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (loverAPI == null) return;

        List<PlayerWW> playerWWS = game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.equals(getPlayerWW()))
                .filter(playerWW -> !loverAPI.getLovers().contains(playerWW))
                .collect(Collectors.toList());

        Collections.shuffle(playerWWS);

        List<PlayerWW> playerWWS1 = new ArrayList<>(playerWWS.subList(0, Math.min(3, playerWWS.size())));

        playerWWS1.addAll(loverAPI.getLovers());

        Collections.shuffle(playerWWS1);

        if (playerWWS1.isEmpty()) return;

        RivalAnnouncementEvent rivalAnnouncementEvent = new RivalAnnouncementEvent(getPlayerWW(), playerWWS1);

        Bukkit.getPluginManager().callEvent(rivalAnnouncementEvent);

        if (rivalAnnouncementEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        getPlayerWW().sendMessageWithKey("werewolf.role.rival.find_lovers", playerWWS1.get(0).getName(), playerWWS1.size() >= 2 ? playerWWS1.get(1).getName() : "", playerWWS1.size() >= 3 ? playerWWS1.get(2).getName() : "", playerWWS1.size() >= 4 ? playerWWS1.get(3).getName() : "", playerWWS1.size() >= 5 ? playerWWS1.get(4).getName() : "");
    }

    @EventHandler
    public void onLoverDeath(LoverDeathEvent event) {
        loverDeath(event.getPlayerWW1(), event.getPlayerWW2());
    }

    private void loverDeath(PlayerWW playerWW1, PlayerWW playerWW2) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!power) return;

        if (loverAPI == null) return;

        if (!loverAPI.getLovers().contains(playerWW1) || !loverAPI.getLovers().contains(playerWW2)) return;

        getPlayerWW().removePlayerMaxHealth(4);
        getPlayerWW().sendMessageWithKey("werewolf.role.rival.lover_death");
        Bukkit.getPluginManager().callEvent(new RivalLoverDeathEvent(getPlayerWW(), new ArrayList<>(loverAPI.getLovers())));
        loverAPI = null;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoverDeath(FinalDeathEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (loverAPI == null) return;

        if (!loverAPI.getLovers().contains(event.getPlayerWW())) return;

        PlayerWW playerWW = event.getPlayerWW();

        Optional<PlayerWW> killerWW = playerWW.getLastKiller();

        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().equals(getPlayerWW())) return;

        RivalLoverEvent rivalLoverEvent = new RivalLoverEvent(getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(rivalLoverEvent);

        if (rivalLoverEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        if (loverAPI.swap(playerWW, getPlayerWW())) {
            getPlayerWW().addLover(loverAPI);
            playerWW.removeLover(loverAPI);
            power = false;
        }
    }


    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (cupidWW == null) return;

        if (cupidWW.isState(StatePlayer.ALIVE)) {

            stringBuilder
                    .append(ChatColor.WHITE)
                    .append(game.translate(RolesBase.CUPID.getKey()))
                    .append(" ")
                    .append(game.getScore()
                            .updateArrow(player,
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
