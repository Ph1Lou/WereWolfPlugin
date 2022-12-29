package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayWillComeEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Role(key = RoleBase.MISCHIEVOUS_WEREWOLF,
                 category = Category.WEREWOLF,
                 attributes = RoleAttribute.WEREWOLF)
public class MischievousWereWolf extends RoleWereWolf implements IInvisible {

    private boolean invisible = false;

    public MischievousWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler
    public void onNight(NightEvent event) {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.little_girl.remove_armor");
    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isInvisible()) {
            return;
        }

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles instanceof IInvisible)
                .map(roles -> (IInvisible) roles)
                .filter(IInvisible::isInvisible)
                .map(invisibleState -> {
                    if (((IRole) invisibleState).isWereWolf()) {
                        return new Pair<>(Material.REDSTONE_BLOCK,
                                ((IRole) invisibleState).getPlayerUUID());
                    } else return new Pair<>(Material.LAPIS_BLOCK,
                            ((IRole) invisibleState).getPlayerUUID());
                })
                .map(objects -> new Pair<>(objects.getValue0(),
                        Bukkit.getPlayer(objects.getValue1())))
                .filter(objects -> objects.getValue1() != null)
                .map(objects -> new Pair<>(objects.getValue0(),
                        objects.getValue1().getLocation()))
                .forEach(objects -> player.playEffect(objects.getValue1(),
                        Effect.STEP_SOUND, objects.getValue0()));
    }

    @EventHandler
    public void onDay(DayEvent event) {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }


        if (isInvisible()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,this.getKey(),0));
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(new InvisibleEvent(
                    this.getPlayerWW(),
                    false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.YELLOW , "werewolf.roles.little_girl.visible");
        }
    }

    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }


        this.getPlayerWW().sendMessageWithKey(
                Prefix.ORANGE , "werewolf.roles.little_girl.soon_to_be_day");
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mischievous_werewolf.description"))
                .setItems(game.translate("werewolf.roles.mischievous_werewolf.items"))
                .setEffects(game.translate("werewolf.roles.mischievous_werewolf.effect"))
                .build();
    }



    @Override
    public void recoverPower() {

    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!uuid.equals(getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!game.isState(StateGame.GAME)) return;

        if (!game.isDay(Day.NIGHT)) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null &&
                inventory.getItem(37) == null &&
                inventory.getItem(38) == null &&
                inventory.getItem(39) == null) {
            if (!isInvisible()) {
                if (!isAbilityEnabled()) {
                    getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.ability_disabled");
                    return;
                }
                player.sendMessage(game.translate(
                        Prefix.GREEN , "werewolf.roles.little_girl.remove_armor_perform"));
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INVISIBILITY,this.getKey()));
                this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,RoleBase.WEREWOLF,0));

                setInvisible(true);
                Bukkit.getPluginManager().callEvent(
                        new InvisibleEvent(this.getPlayerWW(), true));
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
            }
        } else if (isInvisible()) {
            player.sendMessage(game.translate(Prefix.YELLOW , "werewolf.roles.little_girl.visible"));
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,RoleBase.WEREWOLF));

            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,this.getKey(),0));

            setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @Override
    public void disableAbilitiesRole() {

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if (isInvisible()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.roles.little_girl.ability_disabled");
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,RoleBase.WEREWOLF,0));

            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,this.getKey(),0));

            setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }
}
