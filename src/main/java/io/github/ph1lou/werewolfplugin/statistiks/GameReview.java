package io.github.ph1lou.werewolfplugin.statistiks;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameReview {

    private final UUID gameUUID;
    private final List<PlayerReview> players = new ArrayList<>();
    private final int playerSize;
    private final transient WereWolfAPI api;
    private final List<RegisteredAction> registeredActions = new ArrayList<>();
    private Set<UUID> winners;
    private String winnerCampKey;
    private int duration;
    private String name;


    public GameReview(Main main) {
        this.api = main.getWereWolfAPI();
        this.gameUUID = api.getGameUUID();
        this.playerSize = api.getScore().getPlayerSize();
    }

    public void end(String winnerCampKey, Set<PlayerWW> winners) {
        this.winnerCampKey = winnerCampKey;
        this.winners = winners.stream().map(PlayerWW::getUUID).collect(Collectors.toSet());
        this.name = api.getGameName();
        for (PlayerWW playerWW : api.getPlayerWW()) {
            PlayerReview playerReview = new PlayerReview(playerWW);
            players.add(playerReview);
        }
        this.duration = api.getScore().getTimer();
    }

    public void addRegisteredAction(RegisteredAction registeredAction) {
        this.registeredActions.add(registeredAction);
    }

    public UUID getGameUUID() {
        return gameUUID;
    }

    public Set<UUID> getWinners() {
        return winners;
    }

    public String getWinnerCampKey() {
        return winnerCampKey;
    }

    public List<PlayerReview> getPlayers() {
        return players;
    }

    public int getDuration() {
        return duration;
    }

    public WereWolfAPI getApi() {
        return api;
    }

    public String getName() {
        return name;
    }

    public List<RegisteredAction> getRegisteredActions() {
        return registeredActions;
    }

    public int getPlayerSize() {
        return playerSize;
    }

}
