package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.avenger_werewolf.DeathAvengerListEvent;
import fr.ph1lou.werewolfapi.events.roles.avenger_werewolf.RegisterAvengerListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Role(key = RoleBase.AVENGER_WEREWOLF, 
          category = Category.WEREWOLF, 
          attributes = RoleAttribute.WEREWOLF,
        configValues = {@IntValue(key = IntValueBase.AVENGER_WEREWOLF_DISTANCE,
        defaultValue = 10, meetUpValue = 10, step = 2, item = UniversalMaterial.RED_WOOL)})
public class AvengerWereWolf extends RoleWereWolf implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public AvengerWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.avenger_werewolf.description",
                                Formatter.number(game.getConfig()
                                        .getValue(IntValueBase.AVENGER_WEREWOLF_DISTANCE))))
                .setPower(game.translate("werewolf.roles.avenger_werewolf.power"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.affectedPlayers.contains(event.getPlayerWW())) {
            return;
        }

        DeathAvengerListEvent event1 = new DeathAvengerListEvent(this.getPlayerWW(), event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }


        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN , "werewolf.roles.avenger_werewolf.remove",
                Formatter.player(event.getPlayerWW().getName()));
        this.getPlayerWW().addPlayerMaxHealth(2);
    }

    @Override
    public void second() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Bukkit.getOnlinePlayers()
                .stream()
                .map(player1 -> game.getPlayerWW(player1.getUniqueId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(playerWW -> {
                    Location playerLocation = this.getPlayerWW().getLocation();
                    if (playerLocation.getWorld() == playerWW.getLocation().getWorld()) {
                        return playerLocation.distance(playerWW.getLocation()) < game.getConfig()
                                .getValue(IntValueBase.AVENGER_WEREWOLF_DISTANCE);
                    }
                    return false;
                })
                .forEach(playerWW -> {
                    if (!this.affectedPlayers.contains(playerWW)) {
                        RegisterAvengerListEvent event1 = new RegisterAvengerListEvent(this.getPlayerWW(), playerWW);

                        Bukkit.getPluginManager().callEvent(event1);

                        if (event1.isCancelled()) {
                            return;
                        }

                        this.affectedPlayers.add(playerWW);
                        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.avenger_werewolf.add",
                                Formatter.player(playerWW.getName()));
                    }
                });
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().removePlayerMaxHealth(6);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayers.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayers.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return new ArrayList<>(this.affectedPlayers);
    }

   

    @Override
    public Aura getDefaultAura() {
        return this.getPlayerWW().getMaxHealth() >= 20 ? Aura.DARK : Aura.NEUTRAL;
    }
}
