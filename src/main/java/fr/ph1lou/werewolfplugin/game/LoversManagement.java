package fr.ph1lou.werewolfplugin.game;


import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealCursedLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealNormalLoversEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.lovers.ILoverManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import fr.ph1lou.werewolfplugin.roles.lovers.CursedLover;
import fr.ph1lou.werewolfplugin.roles.lovers.LoverImpl;
import fr.ph1lou.werewolfplugin.roles.villagers.Cupid;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class LoversManagement implements ILoverManager {


    private final List<ILover> lovers = new ArrayList<>();

    private final WereWolfAPI game;

    public LoversManagement(WereWolfAPI game) {
        this.game = game;
    }


    private void autoCursedLovers() {

        List<IPlayerWW> cursedLovers = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getLovers().isEmpty())
                .collect(Collectors.toList());


        if (cursedLovers.size() < 2 && game.getConfig().getLoverCount(LoverBase.CURSED_LOVER) > 0) {
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.lovers.cursed_lover.not_enough_players"));
            game.getConfig().setLoverCount(LoverBase.CURSED_LOVER, 0);
            return;
        }

        int i = 0;

        while (cursedLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverBase.CURSED_LOVER)) {

            IPlayerWW playerWW1 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
            cursedLovers.remove(playerWW1);
            IPlayerWW playerWW2 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
            cursedLovers.remove(playerWW2);
            CursedLover cursedLover = new CursedLover(game, playerWW1, playerWW2);
            i++;
            lovers.add(cursedLover);
            cursedLover.announceCursedLoversOnJoin(playerWW1);
            cursedLover.announceCursedLoversOnJoin(playerWW2);
        }
    }

    private void autoAmnesiacLovers() {

        List<IPlayerWW> amnesiacLovers = game.getAlivePlayersWW().stream()
                .filter(playerWW -> playerWW.getLovers().isEmpty())
                .filter(playerWW -> !playerWW.getRole().isKey(RoleBase.CHARMER))
                .filter(playerWW -> !playerWW.getRole().isKey(RoleBase.RIVAL))
                .collect(Collectors.toList());

        if (amnesiacLovers.size() < 2 && game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER) > 0) {
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.lovers.amnesiac_lover.not_enough_players"));
            game.getConfig().setLoverCount(LoverBase.AMNESIAC_LOVER, 0);
            return;
        }

        int i = 0;

        while (amnesiacLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER)) {

            IPlayerWW playerWW1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
            amnesiacLovers.remove(playerWW1);
            IPlayerWW playerWW2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
            amnesiacLovers.remove(playerWW2);
            lovers.add(new AmnesiacLover(game, playerWW1, playerWW2));
            i++;
        }
    }


    public void repartition() {
        List<ILover> temp = new ArrayList<>(this.lovers); // sauvegarder les couples mis manuellement avant la repartition pour pas les trier
        this.lovers.clear();
        this.autoLovers();
        this.rangeLovers();
        game.getConfig().setLoverCount(LoverBase.LOVER, this.lovers.size());
        this.autoAmnesiacLovers();
        this.autoCursedLovers();
        this.lovers.addAll(temp);
        this.lovers
                .forEach(lovers -> {
                    BukkitUtils
                            .registerListener(lovers);
                    lovers.getLovers().forEach(playerWW -> Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW)));

                    if (lovers.isKey(LoverBase.CURSED_LOVER)) {
                        Bukkit.getPluginManager().callEvent(new RevealCursedLoversEvent(new HashSet<>(lovers.getLovers())));
                    } else {
                        Bukkit.getPluginManager().callEvent(new RevealNormalLoversEvent(new HashSet<>(lovers.getLovers())));
                    }
                });
        Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.lovers));
        this.game.checkVictory();
    }

    private void autoLovers() {

        List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isKey(RoleBase.CHARMER))
                .filter(playerWW -> !playerWW.getRole().isKey(RoleBase.RIVAL))
                .filter(playerWW -> playerWW.getLovers().isEmpty())
                .collect(Collectors.toList());

        if (loversAvailable.size() < 2 && game.getConfig().getRoleCount(RoleBase.CUPID) +
                                          game.getConfig().getLoverCount(LoverBase.LOVER) > 0) {
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.lovers.lover.not_enough_players"));
            return;
        }


        if ((game.getConfig().getLoverCount(LoverBase.LOVER) == 0 && game.getConfig().getRoleCount(RoleBase.CUPID) * 2 >=
                                                                     game.getPlayersCount()) ||
            (game.getConfig().getLoverCount(LoverBase.LOVER) != 0 &&
             (game.getConfig().getRoleCount(RoleBase.CUPID) +
              game.getConfig().getLoverCount(LoverBase.LOVER)) * 2 >
             game.getPlayersCount())) {

            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.lovers.lover.polygamy"));
        }

        IPlayerWW playerWW1;
        IPlayerWW playerWW2;


        for (IPlayerWW cupidWW : game.getAlivePlayersWW()) {

            if (!cupidWW.getRole().isKey(RoleBase.CUPID)) {
                continue;
            }

            Cupid cupid = (Cupid) cupidWW.getRole();

            if (cupid.hasPower() ||
                cupid.getAffectedPlayers().size() < 2 ||
                !cupid.getAffectedPlayers().get(0).isState(StatePlayer.ALIVE) ||
                !cupid.getAffectedPlayers().get(1).isState(StatePlayer.ALIVE)) {

                if (loversAvailable.contains(cupidWW)) { // SI le cupidon est dans la liste des joeurs disponible pour le couple
                    loversAvailable.remove(cupidWW); //
                    playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
                    loversAvailable.remove(playerWW2);
                    playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
                    loversAvailable.add(playerWW2);
                    loversAvailable.add(cupidWW);
                } else {
                    playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
                    loversAvailable.remove(playerWW2);
                    playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
                    loversAvailable.add(playerWW2);
                }

                cupid.clearAffectedPlayer();
                cupid.addAffectedPlayer(playerWW2);
                cupid.addAffectedPlayer(playerWW1);
                cupid.setPower(false);
                Bukkit.getPluginManager().callEvent(new CupidLoversEvent(cupidWW, Sets.newHashSet(cupid.getAffectedPlayers())));
                cupidWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.cupid.designation_perform",
                        Formatter.format("&player1&", playerWW2.getName()),
                        Formatter.format("&player2&", playerWW1.getName()));

            } else {
                playerWW2 = cupid.getAffectedPlayers().get(0);
                playerWW1 = cupid.getAffectedPlayers().get(1);
            }

            loversAvailable.remove(playerWW2);
            loversAvailable.remove(playerWW1);

            if (game.getConfig().isConfigActive(ConfigBase.AMNESIAC_LOVERS)) {
                lovers.add(new AmnesiacLover(game, playerWW1, playerWW2));
            } else {
                lovers.add(new LoverImpl(game, cupid.getAffectedPlayers()));
            }
        }

        for (int i = 0; i < game.getConfig().getLoverCount(LoverBase.LOVER); i++) {

            playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
            loversAvailable.remove(playerWW2);
            playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
            loversAvailable.add(playerWW2);

            loversAvailable.remove(playerWW2);
            loversAvailable.remove(playerWW1);

            lovers.add(new LoverImpl(game, new ArrayList<>(Arrays.asList(playerWW2, playerWW1))));
        }
    }


    private void rangeLovers() {

        List<LoverImpl> loverImplAPIS = new ArrayList<>();
        List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
                .filter(playerWW -> !playerWW.getLovers().isEmpty())
                .filter(playerWW -> playerWW
                        .getLovers()
                        .stream().anyMatch(lover -> lover.getKey().equals(LoverBase.LOVER)))
                .collect(Collectors.toList());

        while (!loversAvailable.isEmpty()) {

            List<IPlayerWW> linkCouple = new ArrayList<>();
            linkCouple.add(loversAvailable.remove(0));

            for (int j = 0; j < linkCouple.size(); j++) {

                IPlayerWW playerWWLover = linkCouple.get(j);

                game.getPlayersWW().forEach(playerWW -> playerWW
                        .getLovers()
                        .stream()
                        .filter(iLover -> iLover.getKey().equals(LoverBase.LOVER))
                        .forEach(lover -> {
                            if (lover.getLovers().contains(playerWWLover)) {
                                if (!linkCouple.contains(playerWW)) {
                                    linkCouple.add(playerWW);
                                    loversAvailable.remove(playerWW);
                                }
                            }
                        }));
            }
            loverImplAPIS.add(new LoverImpl(game, linkCouple));
        }

        lovers.stream().filter(iLover -> iLover.isKey(LoverBase.LOVER)).forEach(iLover -> iLover.getLovers()
                .forEach(playerWW -> playerWW.getLovers()
                        .remove(iLover)));

        lovers.removeIf(iLover -> iLover.isKey(LoverBase.LOVER));
        lovers.addAll(loverImplAPIS);
        loverImplAPIS.forEach(LoverImpl::announceLovers);
    }


    @Override
    public List<? extends ILover> getLovers() {
        return lovers;
    }

    @Override
    public void removeLover(ILover lover) {
        this.lovers.remove(lover);
    }

    @Override
    public void addLover(ILover lover) {
        this.lovers.add(lover);
    }
}
