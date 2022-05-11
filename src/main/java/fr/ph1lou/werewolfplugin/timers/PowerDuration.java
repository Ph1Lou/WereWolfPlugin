package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.POWER_DURATION,
        defaultValue = 240,
        meetUpValue = 240)
public class PowerDuration {
}
