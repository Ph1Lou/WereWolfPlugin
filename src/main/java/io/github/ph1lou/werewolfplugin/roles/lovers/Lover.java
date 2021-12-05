package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import io.github.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Lover extends AbstractLover {


    public Lover(WereWolfAPI game, List<IPlayerWW> lovers) {
        super(game, lovers);
    }

    @Override
    public LoverType getLoverType() {
        return LoverType.LOVER;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (this.death) return;

        if (!this.lovers.contains(event.getPlayerWW())) return;

        this.death = true;
        this.lovers.stream()
                .filter(playerWW1 -> !playerWW1.equals(event.getPlayerWW()))
                .forEach(playerWW1 -> {
                    game.getPlayersWW().forEach(playerWW -> {
                        AnnouncementLoverDeathEvent event1 = new AnnouncementLoverDeathEvent(event.getPlayerWW(),playerWW,"werewolf.role.lover.lover_death");
                        Bukkit.getPluginManager().callEvent(event1);

                        if(!event1.isCancelled()){
                            playerWW.sendMessageWithKey("werewolf.role.lover.lover_death",Formatter.player(playerWW1.getName()));
                        }

                    });

                    game.getModerationManager().getModerators().stream()
                            .filter(uuid -> !game.getPlayerWW(uuid).isPresent())
                            .map(Bukkit::getPlayer)
                            .filter(Objects::nonNull)
                            .forEach(player1 -> player1.sendMessage(game.translate("werewolf.role.lover.lover_death",Formatter.player(playerWW1.getName()))));

                    Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.role.lover.lover_death",Formatter.player(playerWW1.getName())));
                    this.game.death(playerWW1);
                });
        Bukkit.getPluginManager().callEvent(new LoverDeathEvent(this));

        this.game.getConfig().removeOneLover(LoverType.LOVER.getKey());
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!this.lovers.contains(playerWW)) return;

        StringBuilder sb = event.getEndMessage();
        StringBuilder sb2 = new StringBuilder();
        this.lovers.stream()
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .forEach(playerWW1 -> sb2.append(playerWW1.getName()).append(" "));

        sb.append(this.game.translate("werewolf.end.lover",
                Formatter.player(sb2.toString())));
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (this.getLovers().contains(playerWW1)) return false;

        if (this.death) return false;

        this.lovers.remove(playerWW);
        this.lovers.add(playerWW1);

        this.lovers.forEach(this::announceLovers);

        this.game.getPlayersWW()
                .stream().map(IPlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.CUPID.getKey()))
                .map(roles -> (IAffectedPlayers) roles)
                .filter(affectedPlayers -> affectedPlayers.getAffectedPlayers().contains(playerWW))
                .forEach(affectedPlayers -> {
                    affectedPlayers.removeAffectedPlayer(playerWW);
                    affectedPlayers.addAffectedPlayer(playerWW1);
                });

        return true;
    }


    @EventHandler
    public void onAroundLoverEvent(AroundLoverEvent event) {


        if (this.death) return;

        for (IPlayerWW playerWW : event.getPlayerWWS()) {
            if (getLovers().contains(playerWW)) {
                for (IPlayerWW playerWW1 : getLovers()) {
                    event.addPlayer(playerWW1);
                }
                break;
            }
        }
    }

    public void addLover(IPlayerWW playerWW) {

        if (lovers.contains(playerWW)) return;

        lovers.forEach(playerWW1 -> playerWW1.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.random_events.triple.lover_join",
                Formatter.player(playerWW.getName())));

        playerWW.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.random_events.triple.join", Formatter.format("&lovers&",getLovers().stream()
                .map(IPlayerWW::getName)
                .collect(Collectors.joining(" "))));

        lovers.add(playerWW);
        playerWW.addLover(this);

    }
}
