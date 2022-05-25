package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.GoldenCount;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Role(key = RoleBase.FRUIT_MERCHANT,
         category = Category.VILLAGER, 
         attributes = {RoleAttribute.VILLAGER, RoleAttribute.MINOR_INFORMATION},
         timers = {@Timer(key = TimerBase.FRUIT_MERCHANT_COOL_DOWN,
                 defaultValue = 1200, meetUpValue = 5 * 60)},
 configValues = {@IntValue(key = FruitMerchant.DISTANCE,
         defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.ORANGE_WOOL)})
public class FruitMerchant extends RoleVillage implements IAffectedPlayers, IPower {

    public static final String DISTANCE = "werewolf.role.fruit_merchant.distance";

    private boolean power = true;
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private final Map<IPlayerWW,Integer> goldenAppleNumber = new HashMap<>();

    public FruitMerchant(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.fruit_merchant.description",
                        Formatter.number(game.getConfig().getValue(DISTANCE)),
                        Formatter.timer(Utils
                                .conversion(game.getConfig()
                                        .getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN)/2))))
                .setPower(game.translate("werewolf.role.fruit_merchant.power"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
        this.goldenAppleNumber.put(iPlayerWW,Utils.countGoldenApple(iPlayerWW));
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
        this.goldenAppleNumber.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Override
    public void setPower(boolean power2) {
        this.power = power2;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public int getGoldenAppleNumber(IPlayerWW playerWW1) {
        return this.goldenAppleNumber.getOrDefault(playerWW1,0);
    }

    @EventHandler
    public void onDeath(FinalDeathEvent event){
        if(this.getPlayerWW().isState(StatePlayer.DEATH)){
            return;
        }

        if(!this.goldenAppleNumber.containsKey(event.getPlayerWW())){
            return;
        }

        FruitMerchantDeathEvent fruitMerchantDeathEvent = new FruitMerchantDeathEvent(this.getPlayerWW(),
                event.getPlayerWW(),new GoldenCount(this.goldenAppleNumber.get(event.getPlayerWW()),
                Utils.countGoldenApple(event.getPlayerWW())));

        Bukkit.getPluginManager().callEvent(fruitMerchantDeathEvent);

        if(fruitMerchantDeathEvent.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,"werewolf.role.fruit_merchant.info",
                Formatter.player(event.getPlayerWW().getName()),
                Formatter.number(fruitMerchantDeathEvent.getGoldenAppleCount().getOldCount()),
                Formatter.format("&number2&", fruitMerchantDeathEvent.getGoldenAppleCount().getNewCount()));

        this.removeAffectedPlayer(event.getPlayerWW());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAppleEat(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if(!playerWW.equals(this.getPlayerWW())){
            return;
        }

        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
            if (game.getRandom().nextInt(4) == 0) {
                playerWW.addPlayerHealth(2);
            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event){

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if(!event.getItem().getType().isEdible()){
            return;
        }

        player.setFoodLevel(player.getFoodLevel()+1);
    }
}
