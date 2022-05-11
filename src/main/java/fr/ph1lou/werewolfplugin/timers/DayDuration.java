package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;

@Timer(key = TimerBase.DAY_DURATION,
        defaultValue = 300,
        meetUpValue = 300)
public class DayDuration { }