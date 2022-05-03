package fr.ph1lou.werewolfplugin.registers;

import fr.ph1lou.werewolfplugin.listeners.random_events.Amnesic;
import fr.ph1lou.werewolfplugin.listeners.random_events.BearingRitual;
import fr.ph1lou.werewolfplugin.listeners.random_events.DrunkenWereWolf;
import fr.ph1lou.werewolfplugin.listeners.random_events.Exposed;
import fr.ph1lou.werewolfplugin.listeners.random_events.GodMiracle;
import fr.ph1lou.werewolfplugin.listeners.random_events.Infection;
import fr.ph1lou.werewolfplugin.listeners.random_events.LootBox;
import fr.ph1lou.werewolfplugin.listeners.random_events.Putrefaction;
import fr.ph1lou.werewolfplugin.listeners.random_events.MisanthropeSister;
import fr.ph1lou.werewolfplugin.listeners.random_events.Rumors;
import fr.ph1lou.werewolfplugin.listeners.random_events.Swap;
import fr.ph1lou.werewolfplugin.listeners.random_events.Triple;
import fr.ph1lou.werewolfapi.enums.RandomEvent;
import fr.ph1lou.werewolfapi.registers.impl.RandomEventRegister;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.listeners.random_events.Discord;

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
                        RandomEvent.DISCORD.getKey(), new Discord(main))
                        .setLoreKey("werewolf.random_events.discord.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.SISTER_MISANTHROPE.getKey(), new MisanthropeSister(main))
                        .setLoreKey("werewolf.random_events.sister_misanthrope.description")
                        .setDefaultValue(1));

        eventRandomsRegister
                .add(new RandomEventRegister("werewolf.name",
                        RandomEvent.RUMORS.getKey(), new Rumors(main))
                        .setLoreKey("werewolf.random_events.rumors.description")
                        .setDefaultValue(1));


        return eventRandomsRegister;
    }

}
