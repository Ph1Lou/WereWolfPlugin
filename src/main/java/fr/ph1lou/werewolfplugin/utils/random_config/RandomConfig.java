package fr.ph1lou.werewolfplugin.utils.random_config;

import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RandomConfig {

    private final Main main;

    public RandomConfig(Main main) {
        this.main = main;
    }

    public Map<String, Integer> createRandomConfig(int playerSize, Set<String> blackList, boolean multipleRole) {

        WereWolfAPI game = main.getWereWolfAPI();

        Set<RoleRegister> roles = main.getRegisterManager().getRolesRegister()
                .stream()
                .filter(roleRegister -> !blackList.contains(roleRegister.getKey()))
                .collect(Collectors.toSet());


        Map<String, Integer> composition = new HashMap<>();

        int werewolfWeight = 0;

        int numberWereWolf = game.getRandom().nextInt(2) + 1;
        if (playerSize / 3 > 6 + numberWereWolf && !multipleRole) {
            numberWereWolf = playerSize / 3 - 6;
        }

        composition.merge(RoleBase.WEREWOLF, numberWereWolf, Integer::sum);
        werewolfWeight += numberWereWolf;

        onComposition(playerSize, 3, werewolfWeight, roles, RoleAttribute.WEREWOLF, composition, multipleRole);

        onComposition(playerSize, 8, 0, roles, RoleAttribute.HYBRID, composition, multipleRole);

        onComposition(playerSize, 8, 0, roles, RoleAttribute.NEUTRAL, composition, multipleRole);

        onComposition(playerSize, 10, 0, roles, RoleAttribute.INFORMATION, composition, multipleRole);

        onComposition(playerSize, 3, 0, roles, RoleAttribute.VILLAGER, composition, multipleRole);

        while (composition.values().stream().mapToInt(value -> value).sum() < playerSize) {
            composition.merge(RoleBase.VILLAGER, 1, Integer::sum);
        }

        return composition;
    }


    private void onComposition(int playerSize, int proportion, int weight, Set<RoleRegister> pool, RoleAttribute RoleAttribute, Map<String, Integer> composition, boolean multipleRole) {


    }
}
