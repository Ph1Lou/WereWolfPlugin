package fr.ph1lou.werewolfplugin.registers;

import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RandomCompositionAttribute;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfplugin.roles.neutrals.AmnesicWerewolf;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import fr.ph1lou.werewolfplugin.roles.neutrals.Assassin;
import fr.ph1lou.werewolfplugin.roles.neutrals.Barbarian;
import fr.ph1lou.werewolfplugin.roles.neutrals.Charmer;
import fr.ph1lou.werewolfplugin.roles.neutrals.FallenAngel;
import fr.ph1lou.werewolfplugin.roles.neutrals.FlutePlayer;
import fr.ph1lou.werewolfplugin.roles.neutrals.GuardianAngel;
import fr.ph1lou.werewolfplugin.roles.neutrals.Imitator;
import fr.ph1lou.werewolfplugin.roles.neutrals.Necromancer;
import fr.ph1lou.werewolfplugin.roles.neutrals.Rival;
import fr.ph1lou.werewolfplugin.roles.neutrals.Scammer;
import fr.ph1lou.werewolfplugin.roles.neutrals.SerialKiller;
import fr.ph1lou.werewolfplugin.roles.neutrals.Succubus;
import fr.ph1lou.werewolfplugin.roles.neutrals.Thief;
import fr.ph1lou.werewolfplugin.roles.neutrals.Thug;
import fr.ph1lou.werewolfplugin.roles.neutrals.WhiteWereWolf;
import fr.ph1lou.werewolfplugin.roles.neutrals.WillOTheWisp;
import fr.ph1lou.werewolfplugin.roles.villagers.Analyst;
import fr.ph1lou.werewolfplugin.roles.villagers.BearTrainer;
import fr.ph1lou.werewolfplugin.roles.villagers.ChattySeer;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import fr.ph1lou.werewolfplugin.roles.villagers.Comedian;
import fr.ph1lou.werewolfplugin.roles.villagers.Cupid;
import fr.ph1lou.werewolfplugin.roles.villagers.Detective;
import fr.ph1lou.werewolfplugin.roles.villagers.DevotedServant;
import fr.ph1lou.werewolfplugin.roles.villagers.Druid;
import fr.ph1lou.werewolfplugin.roles.villagers.Elder;
import fr.ph1lou.werewolfplugin.roles.villagers.Fox;
import fr.ph1lou.werewolfplugin.roles.villagers.FruitMerchant;
import fr.ph1lou.werewolfplugin.roles.villagers.Guard;
import fr.ph1lou.werewolfplugin.roles.villagers.Hermit;
import fr.ph1lou.werewolfplugin.roles.villagers.Librarian;
import fr.ph1lou.werewolfplugin.roles.villagers.LittleGirl;
import fr.ph1lou.werewolfplugin.roles.villagers.Occultist;
import fr.ph1lou.werewolfplugin.roles.villagers.Oracle;
import fr.ph1lou.werewolfplugin.roles.villagers.Priestess;
import fr.ph1lou.werewolfplugin.roles.villagers.Protector;
import fr.ph1lou.werewolfplugin.roles.villagers.Raven;
import fr.ph1lou.werewolfplugin.roles.villagers.Seer;
import fr.ph1lou.werewolfplugin.roles.villagers.Servitor;
import fr.ph1lou.werewolfplugin.roles.villagers.Shaman;
import fr.ph1lou.werewolfplugin.roles.villagers.SiameseTwin;
import fr.ph1lou.werewolfplugin.roles.villagers.Sister;
import fr.ph1lou.werewolfplugin.roles.villagers.Stud;
import fr.ph1lou.werewolfplugin.roles.villagers.Trapper;
import fr.ph1lou.werewolfplugin.roles.villagers.Troublemaker;
import fr.ph1lou.werewolfplugin.roles.villagers.Twin;
import fr.ph1lou.werewolfplugin.roles.villagers.VillageIdiot;
import fr.ph1lou.werewolfplugin.roles.villagers.Villager;
import fr.ph1lou.werewolfplugin.roles.villagers.WildChild;
import fr.ph1lou.werewolfplugin.roles.villagers.WiseElder;
import fr.ph1lou.werewolfplugin.roles.villagers.Witch;
import fr.ph1lou.werewolfplugin.roles.villagers.WolfDog;
import fr.ph1lou.werewolfplugin.roles.villagers.Gravedigger;
import fr.ph1lou.werewolfplugin.roles.villagers.Hunter;
import fr.ph1lou.werewolfplugin.roles.werewolfs.AlphaWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.AvengerWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.BigBadWerewolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.FalsifierWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.FearFulWerewolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.GrimyWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.HowlingWerewolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.InfectFatherOfTheWolves;
import fr.ph1lou.werewolfplugin.roles.werewolfs.MischievousWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.MysticalWereWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.NaughtyLittleWolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.TenebrousWerewolf;
import fr.ph1lou.werewolfplugin.roles.werewolfs.WereWolf;


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
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.HOWLING_WEREWOLF.getKey(), HowlingWerewolf.class)
                            .addCategory(Category.WEREWOLF));

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
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CHATTY_SEER.getKey(), ChattySeer.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.DETECTIVE.getKey(), Detective.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

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
                            .addCategory(Category.NEUTRAL));


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
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.WISE_ELDER.getKey(), WiseElder.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(WiseElder::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.TENEBROUS_WEREWOLF.getKey(), TenebrousWerewolf.class)
                            .addCategory(Category.WEREWOLF)
                            .addConfig(TenebrousWerewolf::configDistance));

          rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.SERVITOR.getKey(), Servitor.class)
                            .addCategory(Category.VILLAGER)
                            .addConfig(Servitor::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.SCAMMER.getKey(), Scammer.class)
                            .addCategory(Category.NEUTRAL)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID)
                            .addConfig(Scammer::config));


            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.FRUIT_MERCHANT.getKey(), FruitMerchant.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.MINOR_INFORMATION)
                            .addConfig(FruitMerchant::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name", RolesBase.DRUID.getKey(), Druid.class)
                            .addCategory(Category.VILLAGER)
                            .addConfig(Druid::config));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.OCCULTIST.getKey(), Occultist.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister

                    .add(new RoleRegister("werewolf.name",
                            RolesBase.HUNTER.getKey(), Hunter.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.GRAVEDIGGER.getKey(), Gravedigger.class)
                            .addCategory(Category.VILLAGER).setRandomCompositionAttribute(RandomCompositionAttribute.MINOR_INFORMATION));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.THUG.getKey(), Thug.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(Thug::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.BARBARIAN.getKey(), Barbarian.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(Barbarian::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.NECROMANCER.getKey(), Necromancer.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(Necromancer::config));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.DEVOTED_SERVANT.getKey(), DevotedServant.class)
                            .addCategory(Category.VILLAGER));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return rolesRegister;

    }

}
