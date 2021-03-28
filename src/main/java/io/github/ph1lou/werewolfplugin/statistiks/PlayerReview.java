package io.github.ph1lou.werewolfplugin.statistiks;


import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.LoverType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerReview {

    private final UUID uuid;
    private final String role;
    private final int deathTime;
    private final List<UUID> killers;
    private final int nbKill;
    private final boolean infected;
    private final boolean solitary;
    private final String name;
    private UUID amnesiacLover = null;
    private List<UUID> lovers = new ArrayList<>();
    private UUID cursedLover = null;

    public PlayerReview(PlayerWW playerWW) {

        this.uuid = playerWW.getUUID();
        this.role = playerWW.getRole().getDeathRole();
        for (LoverAPI loverAPI : playerWW.getLovers()) {

            List<PlayerWW> lovers = new ArrayList<>(loverAPI.getLovers());
            lovers.remove(playerWW);

            if (!lovers.isEmpty()) {

                if (loverAPI.isKey(LoverType.AMNESIAC_LOVER.getKey())) {
                    this.amnesiacLover = lovers.get(0).getUUID();
                } else if (loverAPI.isKey(LoverType.CURSED_LOVER.getKey())) {
                    this.cursedLover = lovers.get(0).getUUID();
                } else this.lovers = lovers.stream().map(PlayerWW::getUUID).collect(Collectors.toList());
            }
        }

        this.deathTime = playerWW.getDeathTime();
        this.killers = playerWW.getKillers().stream().filter(Objects::nonNull).map(PlayerWW::getUUID).collect(Collectors.toList());
        this.nbKill = playerWW.getNbKill();
        this.solitary = playerWW.getRole().isSolitary();
        this.infected = playerWW.getRole().getInfected();
        this.name = playerWW.getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return role;
    }

    public UUID getAmnesiacLover() {
        return amnesiacLover;
    }

    public List<UUID> getLovers() {
        return lovers;
    }

    public UUID getCursedLover() {
        return cursedLover;
    }

    public int getDeathTime() {
        return deathTime;
    }

    public List<UUID> getKillers() {
        return killers;
    }

    public int getNbKill() {
        return nbKill;
    }

    public boolean isInfected() {
        return infected;
    }

    public boolean isSolitary() {
        return solitary;
    }
}
