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

    public Map<String, Integer> createRandomConfig(int playerSize, Set<String> blackList) {

        WereWolfAPI game = main.getWereWolfAPI();

        Set<RoleRegister> roles = main.getRegisterManager().getRolesRegister()
                .stream()
                .filter(roleRegister -> !blackList.contains(roleRegister.getKey()))
                .collect(Collectors.toSet());
        List<RoleRegister> villager = roles
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(RandomCompositionAttribute.VILLAGER))
                .collect(Collectors.toList());
        List<RoleRegister> werewolf = roles
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(RandomCompositionAttribute.WEREWOLF))
                .collect(Collectors.toList());
        List<RoleRegister> information = roles
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(RandomCompositionAttribute.INFORMATION))
                .collect(Collectors.toList());
        List<RoleRegister> hybrid = roles
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(RandomCompositionAttribute.HYBRID))
                .collect(Collectors.toList());
        List<RoleRegister> neutral = roles
                .stream()
                .filter(roleRegister -> roleRegister.getRandomCompositionAttribute()
                        .equals(RandomCompositionAttribute.NEUTRAL))
                .collect(Collectors.toList());

        Map<String, Integer> composition = new HashMap<>();

        int werewolfWeight = 0;
        int numberWereWolf = game.getRandom().nextInt(3) + 1;

        while (playerSize / 3 > werewolfWeight && numberWereWolf > 0) {
            werewolfWeight += 1;
            numberWereWolf--;
            composition.merge(RolesBase.WEREWOLF.getKey(), 1, Integer::sum);
        }

        while (playerSize / 3 > werewolfWeight) {
            int n = (int) Math.floor(game.getRandom().nextFloat() * werewolf.size());
            werewolfWeight += werewolf.get(n).getWeight();
            composition.merge(werewolf.get(n).getKey(), 1, Integer::sum);
        }

        int hybridWeight = 0;

        while (playerSize / 12 > hybridWeight) {
            int n = (int) Math.floor(game.getRandom().nextFloat() * hybrid.size());
            hybridWeight += hybrid.get(n).getWeight();
            composition.merge(hybrid.get(n).getKey(), 1, Integer::sum);
        }

        int neutralWeight = 0;

        while (playerSize / 9 > neutralWeight) {
            int n = (int) Math.floor(game.getRandom().nextFloat() * neutral.size());
            neutralWeight += neutral.get(n).getWeight();
            composition.merge(neutral.get(n).getKey(), 1, Integer::sum);
        }

        int infoWeight = 0;

        while (playerSize / 10 > infoWeight) {
            int n = (int) Math.floor(game.getRandom().nextFloat() * information.size());
            infoWeight += information.get(n).getWeight();
            composition.merge(information.get(n).getKey(), 1, Integer::sum);
        }

        int villagerWeight = 0;

        while (playerSize > villagerWeight && composition.values().stream().mapToInt(value -> value).sum() < playerSize) {
            int n = (int) Math.floor(game.getRandom().nextFloat() * villager.size());
            villagerWeight += villager.get(n).getWeight();
            composition.merge(villager.get(n).getKey(), 1, Integer::sum);
            if (composition.values().stream().mapToInt(value -> value).sum() < playerSize) {
                if (!composition.containsKey(villager.get(n).getKey()) && (villager.get(n).getKey().equals(RolesBase.SISTER.getKey()) || villager.get(n).getKey().equals(RolesBase.SIAMESE_TWIN.getKey()))) {
                    composition.merge(villager.get(n).getKey(), 1, Integer::sum);
                    villagerWeight += villager.get(n).getWeight();
                }
            } else {
                villagerWeight -= villager.get(n).getWeight();
                composition.merge(villager.get(n).getKey(), -1, Integer::sum);
            }

        }

        while (composition.values().stream().mapToInt(value -> value).sum() < playerSize) {
            composition.merge(RolesBase.VILLAGER.getKey(), 1, Integer::sum);
        }

        return composition;
    }
}
