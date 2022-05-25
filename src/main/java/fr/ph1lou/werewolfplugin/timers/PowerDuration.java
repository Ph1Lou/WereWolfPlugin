package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.POWER_DURATION,
        defaultValue = 4 * 60,
        meetUpValue = 3 * 60)
public class PowerDuration {
}
