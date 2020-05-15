package io.github.ph1lou.pluginlg.classesroles;


import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Roles {

    RoleLG getRoleEnum();

    void setCamp(Camp camp);

    boolean isCamp(Camp camp);

    Camp getCamp();

    String getDescription();

    String getDisplay();

    UUID getPlayerUUID();

    void setPlayerUUID(UUID uuid);

    void stolen(UUID uuid);

    void recoverPower(Player player);

    void recoverPotionEffect(Player player);

    boolean isWereWolf();

}
