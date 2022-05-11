package fr.ph1lou.werewolfplugin.timers;


import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.VOTE_DURATION,
        defaultValue = 180,
        meetUpValue = 180)
public class VoteDuration {
}
