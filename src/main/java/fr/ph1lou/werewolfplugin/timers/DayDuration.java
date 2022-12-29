package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.DAY_DURATION,
        defaultValue = 5 * 60,
        meetUpValue = 3 * 60)
public class DayDuration { }