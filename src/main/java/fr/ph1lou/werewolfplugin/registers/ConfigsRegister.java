package fr.ph1lou.werewolfplugin.registers;

import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.listeners.configs.LoneWolf;
import fr.ph1lou.werewolfplugin.listeners.configs.RedNameTag;
import fr.ph1lou.werewolfplugin.listeners.configs.ShowDeathCategoryRole;
import fr.ph1lou.werewolfplugin.listeners.configs.ShowDeathRole;
import fr.ph1lou.werewolfplugin.listeners.configs.VictoryLovers;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;

import java.util.ArrayList;
import java.util.List;

public class ConfigsRegister {

    public static List<ConfigRegister> registerConfigs(Main main) {

        List<ConfigRegister> configsRegister = new ArrayList<>();

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.VICTORY_LOVERS.getKey())
                        .addConfig(new VictoryLovers(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.HIDE_SCENARIOS.getKey()));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.HIDE_EVENTS.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.CHAT.getKey())
                        .setDefaultValue()
                        .unSetAppearInMenu());

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.SHOW_ROLE_TO_DEATH.getKey())
                        .setDefaultValue()
                        .addIncompatibleConfig(ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH.getKey())
                        .addConfig(new ShowDeathRole(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH.getKey())
                        .addIncompatibleConfig(ConfigBase.SHOW_ROLE_TO_DEATH.getKey())
                        .addConfig(new ShowDeathCategoryRole(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.VOTE.getKey())
                        .setDefaultValue()
                        .unSetAppearInMenu());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.HIDE_COMPOSITION.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.RED_NAME_TAG.getKey())
                        .setDefaultValue()
                        .addConfig(new RedNameTag(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.WEREWOLF_CHAT.getKey())
                        .setDefaultValue());

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.DOUBLE_TROLL.getKey())
                        .unSetAppearInMenu());

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.LONE_WOLF.getKey())
                        .addConfig(new LoneWolf(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigBase.PROXIMITY_CHAT.getKey()));


        return configsRegister;
    }



}
