package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;

@Configuration(key = ConfigBase.CHAT,
        defaultValue = true,
        meetUpValue = true,
        appearInMenu = false)
public class Chat {
}