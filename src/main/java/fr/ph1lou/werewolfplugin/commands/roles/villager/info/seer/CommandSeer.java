package fr.ph1lou.werewolfplugin.commands.roles.villager.info.seer;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RoleCommand(key = "werewolf.roles.seer.command", roleKeys = RoleBase.SEER,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandSeer implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole seer = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        IRole role1 = playerWW1.getRole();

        String camp = Camp.VILLAGER.getKey();

        if (role1.isDisplayCamp(Camp.NEUTRAL.getKey())) {
            camp = Camp.NEUTRAL.getKey();
        } else if (role1.isDisplayCamp(Camp.WEREWOLF.getKey())) {
            camp = Camp.WEREWOLF.getKey();
        }

        SeerEvent seerEvent = new SeerEvent(playerWW, playerWW1, camp);
        ((IPower) seer).setPower(false);
        Bukkit.getPluginManager().callEvent(seerEvent);

        if (seerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) seer).addAffectedPlayer(playerWW1);

        String targetRole = role1.getDisplayRole();

        String firstRole = targetRole;
        String secondRole = this.pickDecoy(game, targetRole);

        if (game.getRandom().nextBoolean()) {
            String tmp = firstRole;
            firstRole = secondRole;
            secondRole = tmp;
        }

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.seer.see_perform",
                Formatter.format("&role1&", game.translate(firstRole)),
                Formatter.format("&role2&", game.translate(secondRole)));

        if (game.getConfig().isConfigActive(ConfigBase.CHATTY_SEER)) {
            Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.chatty_seer.see_perform",
                    Formatter.format("&role1&", game.translate(firstRole)),
                    Formatter.format("&role2&", game.translate(secondRole))));
        }
    }

    private String pickDecoy(WereWolfAPI game, String targetRole) {

        GetWereWolfAPI api = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
        if (api == null) {
            return RoleBase.VILLAGER;
        }
        IRegisterManager register = api.getRegisterManager();

        Optional<Category> targetCategory = register.getCategory(targetRole);
        boolean needVillage = !targetCategory.isPresent() || targetCategory.get() != Category.VILLAGER;

        List<String> pool = new ArrayList<>();
        for (Wrapper<IRole, Role> wrapper : register.getRolesRegister()) {
            Role roleAnn = wrapper.getMetaDatas();
            String key = roleAnn.key();
            if (game.getConfig().getRoleCount(key) <= 0) continue;
            if (key.equals(targetRole)) continue;
            if (needVillage && roleAnn.category() != Category.VILLAGER) continue;
            pool.add(key);
        }

        if (pool.isEmpty()) {
            return RoleBase.VILLAGER;
        }

        return pool.get(game.getRandom().nextInt(pool.size()));
    }
}
