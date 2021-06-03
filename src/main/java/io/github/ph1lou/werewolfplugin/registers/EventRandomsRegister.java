package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.RandomEvent;
import io.github.ph1lou.werewolfapi.registers.RandomEventRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Amnesic;
import io.github.ph1lou.werewolfplugin.listeners.random_events.BearingRitual;
import io.github.ph1lou.werewolfplugin.listeners.random_events.DrunkenWereWolf;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Exposed;
import io.github.ph1lou.werewolfplugin.listeners.random_events.GodMiracle;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Infection;
import io.github.ph1lou.werewolfplugin.listeners.random_events.LootBox;
import io.github.ph1lou.werewolfplugin.listeners.random_events.PoorlyGroomedBear;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Putrefaction;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Swap;
import io.github.ph1lou.werewolfplugin.listeners.random_events.Triple;

import java.util.ArrayList;
import java.util.List;

public class EventRandomsRegister {

    public static List<RandomEventRegister> registerRandomEvents(Main main) {

        List<RandomEventRegister> eventRandomsRegister = new ArrayList<>();

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.EXPOSED.getKey(), new Exposed(main))
                        .setLoreKey("werewolf.random_events.exposed.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.TRIPLE.getKey(), new Triple(main))
                        .setLoreKey("werewolf.random_events.triple.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.BEARING_RITUAL.getKey(), new BearingRitual(main))
                        .setLoreKey("werewolf.random_events.bearing_ritual.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.PUTREFACTION.getKey(), new Putrefaction(main))
                        .setLoreKey("werewolf.random_events.putrefaction.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.GOD_MIRACLE.getKey(), new GodMiracle(main))
                        .setLoreKey("werewolf.random_events.god_miracle.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.SWAP.getKey(), new Swap(main))
                        .setLoreKey("werewolf.random_events.swap.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.LOOT_BOX.getKey(), new LootBox(main))
                        .setLoreKey("werewolf.random_events.loot_box.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.INFECTION.getKey(), new Infection(main))
                        .setLoreKey("werewolf.random_events.infection.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.DRUNKEN_WEREWOLF.getKey(), new DrunkenWereWolf(main))
                        .setLoreKey("werewolf.random_events.drunken_werewolf.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.AMNESIC.getKey(), new Amnesic(main))
                        .setLoreKey("werewolf.random_events.amnesic.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.POORLY_GROOMED_BEAR.getKey(), new PoorlyGroomedBear(main))
                        .setLoreKey("werewolf.random_events.poorly_groomed_bear.description")
                        .setDefaultValue(1));

        return eventRandomsRegister;
    }

}
