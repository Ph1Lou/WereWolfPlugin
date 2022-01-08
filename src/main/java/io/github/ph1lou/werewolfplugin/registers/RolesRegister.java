package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.RandomCompositionAttribute;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.roles.neutrals.*;
import io.github.ph1lou.werewolfplugin.roles.villagers.*;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.*;

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
                            RolesBase.HOWLING_WEREWOLF.getKey(), HowlingWerewolfWerewolf.class)
                            .addCategory(Category.WEREWOLF)
                            .addConfig(HowlingWerewolfWerewolf::config));

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
                            RolesBase.TWIN.getKey(), Twin.class)
                            .addCategory(Category.VILLAGER)
                            .setRequireDouble()
                            .addConfig(Twin::config));

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
                            RolesBase.ANALYST.getKey(), Analyst.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

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
                            RolesBase.CHARMER.getKey(), Charmer.class)
                            .addCategory(Category.NEUTRAL));

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
                            RolesBase.WILL_O_THE_WISP.getKey(), WillOTheWisp.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(WillOTheWisp::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.BIG_BAD_WEREWOLF.getKey(), BigBadWerewolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FEARFUL_WEREWOLF.getKey(), FearFulWerewolf.class)
                            .addCategory(Category.WEREWOLF)
                            .addConfig(FearFulWerewolf::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.HERMIT.getKey(), Hermit.class)
                            .addCategory(Category.VILLAGER)
                            .addConfig(Hermit::config));

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

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.SERVITOR.getKey(), Servitor.class)
                            .addCategory(Category.VILLAGER));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return rolesRegister;

    }

}
