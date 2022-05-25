package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;

@Configuration(key = ConfigBase.WEREWOLF_CHAT,
        defaultValue = true,
        meetUpValue = true)
public class WerewolfChat {
}
