package io.github.ph1lou.werewolfplugin.utils.random_config;

import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RandomCompositionAttribute;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.Main;

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

        composition.merge(RolesBase.WEREWOLF.getKey(), numberWereWolf, Integer::sum);
        werewolfWeight += numberWereWolf;

        onComposition(playerSize, 3, werewolfWeight, roles, RandomCompositionAttribute.WEREWOLF, composition, multipleRole);

        onComposition(playerSize, 8, 0, roles, RandomCompositionAttribute.HYBRID, composition, multipleRole);

        onComposition(playerSize, 8, 0, roles, RandomCompositionAttribute.NEUTRAL, composition, multipleRole);

        onComposition(playerSize, 10, 0, roles, RandomCompositionAttribute.INFORMATION, composition, multipleRole);

        onComposition(playerSize, 3, 0, roles, RandomCompositionAttribute.VILLAGER, composition, multipleRole);

        while (composition.values().stream().mapToInt(value -> value).sum() < playerSize) {
            composition.merge(RolesBase.VILLAGER.getKey(), 1, Integer::sum);
        }

        return composition;
    }


    private void onComposition(int playerSize, int proportion, int weight, Set<RoleRegister> pool, RandomCompositionAttribute randomCompositionAttribute, Map<String, Integer> composition, boolean multipleRole) {

        List<RoleRegister> poolAttribute = pool
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(randomCompositionAttribute))
                .collect(Collectors.toList());

        while (playerSize > composition.values().stream().mapToInt(value -> value).sum() && playerSize / proportion > weight && !poolAttribute.isEmpty()) {

            int n = (int) Math.floor(main.getWereWolfAPI().getRandom().nextFloat() * poolAttribute.size());

            int number = poolAttribute.get(n)
                    .isRequireDouble() ? playerSize > composition.values().stream().mapToInt(value -> value).sum() + 1 ? 2 : 0 : 1;

            weight += poolAttribute.get(n).getWeight() * number;
            composition.merge(poolAttribute.get(n).getKey(), number, Integer::sum);
            if (poolAttribute.get(n).getRequireRole().isPresent() && composition.get(poolAttribute.get(n).getRequireRole().get()) == 0) {
                if (playerSize > composition.values().stream().mapToInt(value -> value).sum()) {
                    composition.merge(poolAttribute.get(n).getRequireRole().get(), 1, Integer::sum);
                } else {
                    composition.remove(poolAttribute.get(n).getKey());
                }
            }

            if (!multipleRole) {
                poolAttribute.remove(n);
            }
        }
    }
}
