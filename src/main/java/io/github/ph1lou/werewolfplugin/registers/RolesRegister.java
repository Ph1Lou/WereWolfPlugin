package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.RandomCompositionAttribute;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.roles.neutrals.AmnesicWerewolf;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Angel;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Assassin;
import io.github.ph1lou.werewolfplugin.roles.neutrals.FallenAngel;
import io.github.ph1lou.werewolfplugin.roles.neutrals.FlutePlayer;
import io.github.ph1lou.werewolfplugin.roles.neutrals.GuardianAngel;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Imitator;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Rival;
import io.github.ph1lou.werewolfplugin.roles.neutrals.SerialKiller;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Succubus;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Thief;
import io.github.ph1lou.werewolfplugin.roles.neutrals.WhiteWereWolf;
import io.github.ph1lou.werewolfplugin.roles.villagers.BearTrainer;
import io.github.ph1lou.werewolfplugin.roles.villagers.ChattySeer;
import io.github.ph1lou.werewolfplugin.roles.villagers.Citizen;
import io.github.ph1lou.werewolfplugin.roles.villagers.Comedian;
import io.github.ph1lou.werewolfplugin.roles.villagers.Cupid;
import io.github.ph1lou.werewolfplugin.roles.villagers.Detective;
import io.github.ph1lou.werewolfplugin.roles.villagers.Elder;
import io.github.ph1lou.werewolfplugin.roles.villagers.Fox;
import io.github.ph1lou.werewolfplugin.roles.villagers.Guard;
import io.github.ph1lou.werewolfplugin.roles.villagers.Librarian;
import io.github.ph1lou.werewolfplugin.roles.villagers.LittleGirl;
import io.github.ph1lou.werewolfplugin.roles.villagers.Oracle;
import io.github.ph1lou.werewolfplugin.roles.villagers.Priestess;
import io.github.ph1lou.werewolfplugin.roles.villagers.Protector;
import io.github.ph1lou.werewolfplugin.roles.villagers.Raven;
import io.github.ph1lou.werewolfplugin.roles.villagers.Seer;
import io.github.ph1lou.werewolfplugin.roles.villagers.Shaman;
import io.github.ph1lou.werewolfplugin.roles.villagers.SiameseTwin;
import io.github.ph1lou.werewolfplugin.roles.villagers.Sister;
import io.github.ph1lou.werewolfplugin.roles.villagers.Stud;
import io.github.ph1lou.werewolfplugin.roles.villagers.Trapper;
import io.github.ph1lou.werewolfplugin.roles.villagers.Troublemaker;
import io.github.ph1lou.werewolfplugin.roles.villagers.VillageIdiot;
import io.github.ph1lou.werewolfplugin.roles.villagers.Villager;
import io.github.ph1lou.werewolfplugin.roles.villagers.WildChild;
import io.github.ph1lou.werewolfplugin.roles.villagers.Witch;
import io.github.ph1lou.werewolfplugin.roles.villagers.WolfDog;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.AlphaWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.AvengerWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.FalsifierWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.GrimyWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.InfectFatherOfTheWolves;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.MischievousWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.MysticalWereWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.NaughtyLittleWolf;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.WereWolf;

import java.util.ArrayList;
import java.util.List;

public class RolesRegister {

    public static List<RoleRegister> registerRoles() {

        List<RoleRegister> rolesRegister = new ArrayList<>();

        try {

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WEREWOLF.getKey(), WereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FALSIFIER_WEREWOLF.getKey(), FalsifierWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.GUARD.getKey(), Guard.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.NAUGHTY_LITTLE_WOLF.getKey(), NaughtyLittleWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.INFECT.getKey(), InfectFatherOfTheWolves.class)
                            .addCategory(Category.WEREWOLF)
                            .setWeight(1.5f));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.MISCHIEVOUS_WEREWOLF.getKey(), MischievousWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.MYSTICAL_WEREWOLF.getKey(), MysticalWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CUPID.getKey(), Cupid.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WITCH.getKey(), Witch.class)
                            .addCategory(Category.VILLAGER)
                            .addConfig(Witch::config));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ELDER.getKey(), Elder.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FOX.getKey(), Fox.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(Fox::config1)
                            .addConfig(Fox::config2));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.LITTLE_GIRL.getKey(), LittleGirl.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CITIZEN.getKey(), Citizen.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.COMEDIAN.getKey(), Comedian.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SISTER.getKey(), Sister.class)
                            .addCategory(Category.VILLAGER)
                            .setRequireDouble()
                            .addConfig(Sister::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.STUD.getKey(), Stud.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SIAMESE_TWIN.getKey(), SiameseTwin.class)
                            .addCategory(Category.VILLAGER)
                            .setRequireDouble());

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.RAVEN.getKey(), Raven.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.PROTECTOR.getKey(), Protector.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.TRAPPER.getKey(), Trapper.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.TROUBLEMAKER.getKey(), Troublemaker.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.BEAR_TRAINER.getKey(), BearTrainer.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(BearTrainer::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SEER.getKey(), Seer.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(Seer::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CHATTY_SEER.getKey(), ChattySeer.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.DETECTIVE.getKey(), Detective.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(Detective::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WILD_CHILD.getKey(), WildChild.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SUCCUBUS.getKey(), Succubus.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(Succubus::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ANGEL.getKey(), Angel.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(Angel::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FALLEN_ANGEL.getKey(), FallenAngel.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.GUARDIAN_ANGEL.getKey(), GuardianAngel.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ASSASSIN.getKey(), Assassin.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SERIAL_KILLER.getKey(), SerialKiller.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.AMNESIAC_WEREWOLF.getKey(), AmnesicWerewolf.class)
                            .addCategory(Category.NEUTRAL)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WHITE_WEREWOLF.getKey(), WhiteWereWolf.class)
                            .addCategory(Category.NEUTRAL)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.THIEF.getKey(), Thief.class)
                            .addCategory(Category.NEUTRAL)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FLUTE_PLAYER.getKey(), FlutePlayer.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(FlutePlayer::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.LIBRARIAN.getKey(), Librarian.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.VILLAGER.getKey(),
                            Villager.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WOLF_DOG.getKey(),
                            WolfDog.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.RIVAL.getKey(), Rival.class)
                            .addCategory(Category.NEUTRAL)
                            .setRequireAnotherRole(RolesBase.CUPID.getKey()));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.VILLAGE_IDIOT.getKey(), VillageIdiot.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.IMITATOR.getKey(), Imitator.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.GRIMY_WEREWOLF.getKey(), GrimyWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.PRIESTESS.getKey(), Priestess.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(Priestess::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.AVENGER_WEREWOLF.getKey(), AvengerWereWolf.class)
                            .addCategory(Category.WEREWOLF)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.WEREWOLF)
                            .addConfig(AvengerWereWolf::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SHAMAN.getKey(), Shaman.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ALPHA_WEREWOLF.getKey(), AlphaWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.ORACLE.getKey(), Oracle.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(Oracle::config));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return rolesRegister;

    }

}
