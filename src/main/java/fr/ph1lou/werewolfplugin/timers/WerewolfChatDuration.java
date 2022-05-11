package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.WEREWOLF_CHAT_DURATION,
        defaultValue = 30,
        meetUpValue = 30)
public class WerewolfChatDuration {
}
