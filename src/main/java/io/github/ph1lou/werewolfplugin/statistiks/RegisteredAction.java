package io.github.ph1lou.werewolfplugin.statistiks;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.PlayerWW;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegisteredAction {


    private final String event;
    @Nullable
    private final UUID uuid;
    @Nullable
    private final List<UUID> uuidS = new ArrayList<>();
    private final int timer;
    private String extraInfo;
    private int extraInt;

    public RegisteredAction(String event, @Nullable PlayerWW playerWW, Set<PlayerWW> uuidS, int timer) {
        this.event = event;
        if (playerWW == null) {
            this.uuid = null;
        } else this.uuid = playerWW.getUUID();
        if (uuidS != null) {
            this.uuidS.addAll(uuidS.stream().map(PlayerWW::getUUID).collect(Collectors.toList()));
        }
        this.timer = timer;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @Nullable PlayerWW targetWW, int timer) {
        this(event, playerWW, targetWW == null ? new HashSet<>() : Sets.newHashSet(targetWW), timer);
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, int timer) {
        this(event, playerWW, new HashSet<>(), timer);
    }

    public RegisteredAction(@NotNull String event, @NotNull Set<PlayerWW> playerWW, int timer) {
        this(event, null, playerWW, timer);
    }

    public RegisteredAction(@NotNull String event, @NotNull Set<PlayerWW> playerWW, int timer, @NotNull String extraInfo) {
        this(event, null, playerWW, timer, extraInfo);
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @NotNull Set<PlayerWW> playerWWS, int timer, @NotNull String extraInfo) {
        this(event, playerWW, playerWWS, timer);
        this.extraInfo = extraInfo;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, int timer, @NotNull String extraInfo) {
        this(event, playerWW, new HashSet<>(), timer);
        this.extraInfo = extraInfo;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, int timer, int extraInt) {
        this(event, playerWW, new HashSet<>(), timer);
        this.extraInt = extraInt;
    }

    public RegisteredAction(@NotNull String event, int timer, @NotNull String extraInfo) {
        this(event, (PlayerWW) null, timer, extraInfo);
    }

    public RegisteredAction(@NotNull String event, int timer, int extraInt) {
        this(event, null, timer, extraInt);
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @Nullable PlayerWW targetWW, int timer, @NotNull String extraInfo) {
        this(event, playerWW, Sets.newHashSet(targetWW), timer);
        this.extraInfo = extraInfo;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @Nullable PlayerWW targetWW, int timer, int extraInt) {
        this(event, playerWW, Sets.newHashSet(targetWW), timer);
        this.extraInt = extraInt;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @Nullable PlayerWW targetWW, int timer, @NotNull String extraInfo, int extraInt) {
        this(event, playerWW, targetWW, timer);
        this.extraInfo = extraInfo;
        this.extraInt = extraInt;
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @NotNull Set<PlayerWW> playerWWS, int timer, @NotNull String extraInfo, int extraInt) {
        this(event, playerWW, playerWWS, timer);
        this.extraInfo = extraInfo;
        this.extraInt = extraInt;
    }

    public RegisteredAction(@NotNull String event, int timer) {
        this(event, null, new HashSet<>(), timer);
    }

    public RegisteredAction(@NotNull String event, @Nullable PlayerWW playerWW, @NotNull Set<PlayerWW> playerWWS, int timer, int extraInt) {
        this(event, playerWW, playerWWS, timer);
        this.extraInt = extraInt;
    }


    public String getEvent() {
        return event;
    }

    public @Nullable UUID getUuid() {
        return uuid;
    }

    public @Nullable List<UUID> getUuidS() {
        return uuidS;
    }

    public int getTimer() {
        return timer;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public int getExtraInt() {
        return extraInt;
    }
}
