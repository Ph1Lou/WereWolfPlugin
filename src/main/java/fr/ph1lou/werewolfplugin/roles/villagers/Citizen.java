// 
// Decompiled by Procyon v0.5.36
// 

package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.NewVoteResultEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.RegisterManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Citizen extends RoleVillage implements ILimitedUse, IAffectedPlayers, IPower
{
    private int use;
    private final List<IPlayerWW> affectedPlayer;
    private boolean power;

    public Citizen(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        this.use = 0;
        this.affectedPlayer = new ArrayList<>();
        this.power = true;
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
    public int getUse() {
        return this.use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @EventHandler
    public void onVoteEnd(VoteEndEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if(!this.isAbilityEnabled()){
            return;
        }
        if (this.game.getConfig().isConfigActive(ConfigBase.NEW_VOTE.getKey())) {
            return;
        }
        if (this.getUse() < 2) {
            this.getPlayerWW().sendMessage(this.seeVote());
        }
        if (this.hasPower()) {
            this.getPlayerWW().sendMessage(this.cancelVote());
        }
    }

    @NotNull
    @Override
    public String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate(this.game.getConfig()
                        .isConfigActive(ConfigBase.NEW_VOTE.getKey()) ? "werewolf.role.citizen.description_new_vote" : "werewolf.role.citizen.description")).addExtraLines(this.game.translate("werewolf.role.citizen.description_extra")).build();
    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (this.getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }
        if(!this.isAbilityEnabled()){
            return;
        }
        if (!this.game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION.getKey())) {
            return;
        }
        List<String> roles = RegisterManager.get().getRolesRegister().stream().map(RoleRegister::getKey).collect(Collectors.toList());
        if (roles.size() < 3) {
            return;
        }
        Collections.shuffle(roles, this.game.getRandom());
        int count = roles.subList(0, 3).stream().mapToInt(s -> this.game.getConfig().getRoleCount(s)).sum();
        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.role.citizen.hide_composition", Formatter.format("&role1&", this.game.translate(roles.get(0))), Formatter.format("&role2&", this.game.translate(roles.get(1))), Formatter.format("&role3&", this.game.translate(roles.get(2))), Formatter.number(count));
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    public void onNewVote(NewVoteResultEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if(!this.isAbilityEnabled()){
            return;
        }
        if (event.getPlayerVotedByWerewolfWW() != null && this.hasPower()) {
            this.getPlayerWW().sendMessage(this.seeWerewolfVote(event.getPlayerVotedByWerewolfWW().getUUID()));
        }
        if (event.getPlayerVotedByVillagerWW() == null) {
            return;
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.role.citizen.new_vote_count",
                Formatter.player(event.getPlayerVotedByVillagerWW().getName()),
                Formatter.number(this.game.getVoteManager().getVotes().getOrDefault(event.getPlayerVotedByVillagerWW(), 0)));
    }

    private TextComponent cancelVote() {
        TextComponent cancelVote = new TextComponent(this.game.translate("werewolf.role.citizen.click"));
        cancelVote.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s", this.game.translate("werewolf.role.citizen.command_2"))));
        cancelVote.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.game.translate("werewolf.role.citizen.cancel")).create()));
        TextComponent cancel = new TextComponent(this.game.translate(Prefix.YELLOW.getKey(), "werewolf.role.citizen.cancel_vote_message", Formatter.number(this.hasPower() ? 1 : 0)));
        cancel.addExtra(cancelVote);
        cancel.addExtra(new TextComponent(this.game.translate("werewolf.role.citizen.time_left", Formatter.timer(Utils.conversion(this.game.getConfig().getTimerValue(TimerBase.VOTE_WAITING.getKey()))))));
        return cancel;
    }

    private TextComponent seeVote() {
        TextComponent seeVote = new TextComponent(this.game.translate("werewolf.role.citizen.click"));
        seeVote.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s", this.game.translate("werewolf.role.citizen.command_1"))));
        seeVote.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.game.translate("werewolf.role.citizen.see")).create()));
        TextComponent see = new TextComponent(this.game.translate(Prefix.YELLOW.getKey(), "werewolf.role.citizen.see_vote_message", Formatter.number(2 - this.getUse())));
        see.addExtra(seeVote);
        see.addExtra(new TextComponent(this.game.translate("werewolf.role.citizen.time_left", Formatter.timer(Utils.conversion(this.game.getConfig().getTimerValue(TimerBase.VOTE_WAITING.getKey()))))));
        return see;
    }

    private TextComponent seeWerewolfVote(UUID werewolf) {
        TextComponent seeVote = new TextComponent(this.game.translate(Prefix.GREEN.getKey(), "werewolf.role.citizen.click_to_see_werewolf_vote"));
        seeVote.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s %s", this.game.translate("werewolf.role.citizen.command_1"),
                werewolf.toString())));
        return seeVote;
    }

    @EventHandler
    public void onRumor(RumorsWriteEvent event){
        if(event.isCancelled()){
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if(!this.isAbilityEnabled()){
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(), "werewolf.role.citizen.rumor",
                Formatter.player(event.getPlayerWW().getName()),
                Formatter.format("&message&", event.getMessage()));
    }
}
