package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;

@Configuration(key = ConfigBase.VOTE,
        appearInMenu = false,
        defaultValue = true)
public class Vote {
}
