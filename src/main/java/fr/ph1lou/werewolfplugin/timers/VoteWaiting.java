package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.VOTE_WAITING,
        defaultValue = 60,
        meetUpValue = 60)
public class VoteWaiting {
}
