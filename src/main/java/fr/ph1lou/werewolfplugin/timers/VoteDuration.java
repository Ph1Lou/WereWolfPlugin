package fr.ph1lou.werewolfplugin.timers;


import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.VOTE_DURATION,
        defaultValue = 60 * 3,
        meetUpValue = 60 * 1)
public class VoteDuration {
}
