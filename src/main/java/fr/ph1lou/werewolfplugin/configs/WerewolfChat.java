package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;

@Configuration(key = ConfigBase.WEREWOLF_CHAT,
        defaultValue = true,
        meetUpValue = true,
        timers = @Timer(key = TimerBase.WEREWOLF_CHAT_DURATION,
                defaultValue = 30,
                meetUpValue = 30),
        configValues = @IntValue(key = WerewolfChat.CONFIG,
                defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.BOOK))
public class WerewolfChat {

    public static final String CONFIG = IntValueBase.WEREWOLF_CHAT;
}
