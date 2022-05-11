package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;

@Timer(key = TimerBase.VOTE_BEGIN,
        defaultValue = 2400,
        meetUpValue = 2400,
       decrement = true,
       onZero = VoteBeginEvent.class)
public class VoteBegin {
}
