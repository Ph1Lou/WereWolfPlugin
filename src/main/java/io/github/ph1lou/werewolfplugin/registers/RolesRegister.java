package io.github.ph1lou.werewolfplugin.registers;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.RandomCompositionAttribute;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
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
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
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
                            .addConfig(game -> {

                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(Material.STICK)
                                                .setLore(game.translate(config.isWitchAutoResurrection() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                .setDisplayName(game.translate("werewolf.role.witch.auto_rez_witch"))
                                                .build(), e -> {
                                            config.setWitchAutoResurrection(!config.isWitchAutoResurrection());

                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setLore(game.translate(config.isWitchAutoResurrection() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                    .build());

                                        });
                            }));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ELDER.getKey(), Elder.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FOX.getKey(), Fox.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(UniversalMaterial.CARROT.getType())
                                                .setLore(lore)
                                                .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                                                        config.getUseOfFlair()))
                                                .build(), e -> {
                                            if (e.isLeftClick()) {
                                                config.setUseOfFlair(config.getUseOfFlair() + 1);
                                            } else if (config.getUseOfFlair() > 0) {
                                                config.setUseOfFlair(config.getUseOfFlair() - 1);
                                            }


                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                                                            config.getUseOfFlair()))
                                                    .build());

                                        });
                            })
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((
                                        new ItemBuilder(UniversalMaterial.ORANGE_WOOL.getStack())
                                                .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                                                        config.getDistanceFox()))
                                                .setLore(lore).build()), e -> {

                                    if (e.isLeftClick()) {
                                        config.setDistanceFox((config.getDistanceFox() + 5));
                                    } else if (config.getDistanceFox() - 5 > 0) {
                                        config.setDistanceFox(config.getDistanceFox() - 5);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                                                    config.getDistanceFox()))
                                            .build());

                                });
                            }));

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
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((
                                        new ItemBuilder(UniversalMaterial.GRAY_WOOL.getStack())
                                                .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                                                        config.getDistanceSister()))
                                                .setLore(lore).build()), e -> {

                                    if (e.isLeftClick()) {
                                        config.setDistanceSister((config.getDistanceSister() + 2));
                                    } else if (config.getDistanceSister() - 2 > 0) {
                                        config.setDistanceSister(config.getDistanceSister() - 2);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                                                    config.getDistanceSister()))
                                            .build());

                                });
                            }));

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
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((
                                        new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                                                .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                                                        config.getDistanceBearTrainer()))
                                                .setLore(lore).build()), e -> {
                                    if (e.isLeftClick()) {
                                        config.setDistanceBearTrainer((config.getDistanceBearTrainer() + 5));
                                    } else if (config.getDistanceBearTrainer() - 5 > 0) {
                                        config.setDistanceBearTrainer(config.getDistanceBearTrainer() - 5);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                                                    config.getDistanceBearTrainer()))
                                            .build());

                                });
                            }));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SEER.getKey(), Seer.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION)
                            .addConfig(game -> {

                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(Material.GOLD_BLOCK)
                                                .setLore(game.translate(config.isSeerEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                .setDisplayName(game.translate("werewolf.role.seer.seer_every_other_day"))
                                                .build(), e -> {
                                            config.setSeerEveryOtherDay(!config.isSeerEveryOtherDay());

                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setLore(game.translate(config.isSeerEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                    .build());

                                        });
                            }));

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
                            .addConfig(game -> {

                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(UniversalMaterial.LEAD.getType())
                                                .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                .setDisplayName(game.translate("werewolf.role.detective.detective_every_other_day"))
                                                .build(), e -> {
                                            config.setDetectiveEveryOtherDay(!config.isDetectiveEveryOtherDay());

                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                    .build());

                                        });
                            }));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WILD_CHILD.getKey(), WildChild.class)
                            .addCategory(Category.VILLAGER)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.HYBRID));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SUCCUBUS.getKey(), Succubus.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((new ItemBuilder(
                                        UniversalMaterial.PURPLE_WOOL.getStack())
                                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                                                config.getDistanceSuccubus()))
                                        .setLore(lore).build()), e -> {
                                    if (e.isLeftClick()) {
                                        config.setDistanceSuccubus((config.getDistanceSuccubus() + 5));
                                    } else if (config.getDistanceSuccubus() - 5 > 0) {
                                        config.setDistanceSuccubus(config.getDistanceSuccubus() - 5);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                                                    config.getDistanceSuccubus()))
                                            .build());

                                });
                            }));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ANGEL.getKey(), Angel.class)
                            .addCategory(Category.NEUTRAL)
                            .addConfig(game -> {
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(Material.MELON)
                                                .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                .setDisplayName(game.translate("werewolf.role.angel.sweet_angel"))
                                                .build(), e -> {
                                            config.setDetectiveEveryOtherDay(!config.isDetectiveEveryOtherDay());

                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                    .build());

                                        });
                            }));

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
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((new ItemBuilder(
                                        UniversalMaterial.LIGHT_BLUE_WOOL.getStack())
                                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.flute_player",
                                                config.getDistanceFlutePlayer()))
                                        .setLore(lore).build()), e -> {
                                    if (e.isLeftClick()) {
                                        config.setDistanceFlutePlayer((config.getDistanceFlutePlayer() + 2));
                                    } else if (config.getDistanceFlutePlayer() - 2 > 0) {
                                        config.setDistanceFlutePlayer(config.getDistanceFlutePlayer() - 2);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.flute_player",
                                                    config.getDistanceFlutePlayer()))
                                            .build());

                                });
                            }));

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
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((
                                        new ItemBuilder(UniversalMaterial.BLUE_WOOL.getStack())
                                                .setDisplayName(game.translate("werewolf.menu.advanced_tool.priestess",
                                                        config.getDistancePriestess()))
                                                .setLore(lore).build()), e -> {

                                    if (e.isLeftClick()) {
                                        config.setDistancePriestess((config.getDistancePriestess() + 2));
                                    } else if (config.getDistancePriestess() - 2 > 0) {
                                        config.setDistancePriestess(config.getDistancePriestess() - 2);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.priestess",
                                                    config.getDistancePriestess()))
                                            .build());

                                });
                            }));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.AVENGER_WEREWOLF.getKey(), AvengerWereWolf.class)
                            .addCategory(Category.WEREWOLF)
                            .setRandomCompositionAttribute(RandomCompositionAttribute.WEREWOLF)
                            .addConfig(game -> {

                                List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                                        game.translate("werewolf.menu.right"));
                                IConfiguration config = game.getConfig();

                                return ClickableItem.of((new ItemBuilder(
                                        UniversalMaterial.RED_WOOL.getStack())
                                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.avenger_werewolf",
                                                config.getDistanceAvengerWerewolf()))
                                        .setLore(lore).build()), e -> {
                                    if (e.isLeftClick()) {
                                        config.setDistanceAvengerWerewolf((config.getDistanceAvengerWerewolf() + 2));
                                    } else if (config.getDistanceAvengerWerewolf() - 2 > 0) {
                                        config.setDistanceAvengerWerewolf(config.getDistanceAvengerWerewolf() - 2);
                                    }


                                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                            .setLore(lore)
                                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.avenger_werewolf",
                                                    config.getDistanceAvengerWerewolf()))
                                            .build());

                                });
                            }));

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
                            .addConfig(game -> {

                                IConfiguration config = game.getConfig();

                                return ClickableItem.of(
                                        new ItemBuilder(Material.DIAMOND_BLOCK)
                                                .setLore(game.translate(config.isOracleEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                .setDisplayName(game.translate("werewolf.role.oracle.oracle_every_other_day"))
                                                .build(), e -> {
                                            config.setOracleEveryOtherDay(!config.isOracleEveryOtherDay());

                                            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                                    .setLore(game.translate(config.isOracleEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                                                    .build());

                                        });
                            }));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return rolesRegister;

    }

}
