package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.LoverAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Villager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PlayerLG implements PlayerWW {

    private StatePlayer state = StatePlayer.ALIVE;
    private Roles role;
    private final List<LoverAPI> lovers = new ArrayList<>();
    private final UUID uuid;
    private final List<PlayerWW> killer = new ArrayList<>();
    private final List<ItemStack> itemsDeath = new ArrayList<>();
    private transient Location spawn;
    private int deathTime = 0;
    private int disconnectedTime = 0;
    private int lostHeart = 0;
    private int kill = 0;
    private boolean kit = false;
    private boolean thief = false;
    private String name;


    public PlayerLG(Main main, Player player) {
        this.spawn = player.getWorld().getSpawnLocation();
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.role = new Villager(main, this,
                RolesBase.VILLAGER.getKey());
    }


	@Override
	public void setItemDeath(ItemStack[] itemsDeath) {
		this.itemsDeath.addAll(Arrays.asList(itemsDeath));
	}

	@Override
	public List<ItemStack> getItemDeath() {
		return this.itemsDeath;
	}

	@Override
	public void clearItemDeath() {
		this.itemsDeath.clear();
	}

	@Override
	public void setState(StatePlayer state) {
		this.state=state;
	}

	@Override
	public boolean isState(StatePlayer state) {
		return(this.state==state);
	}

	@Override
	public void addOneKill() {
		this.kill +=1;
	}

	@Override
	public int getNbKill() {
		return(this.kill);
	}

	@Override
	public void addKLostHeart(int k) {
		this.lostHeart +=k;
	}

	@Override
	public int getLostHeart() {
		return(this.lostHeart);
	}


    @Override
    public void setKit(boolean kit) {
        this.kit = kit;
    }

    @Override
    public boolean hasKit() {
        return (this.kit);
    }


	@Override
	public void setRole(Roles role) {
		this.role = role;
	}

	@Override
	public Roles getRole() {
		return (this.role);
	}

    @Override
    public boolean isKey(String role) {
        return (role.equals(this.role.getKey()));
    }

	@Override
	public void setSpawn(Location spawn) {
		this.spawn=spawn;
	}

	@Override
	public Location getSpawn() {
		return(this.spawn);
	}

    @Override
    public List<LoverAPI> getLovers() {
        return (this.lovers);
    }

    @Override
    public void addKiller(PlayerWW killerUUID) {
        this.killer.add(killerUUID);
    }

    @Override
    public List<? extends PlayerWW> getKillers() {
        return (this.killer);
    }

	@Override
	public void setDeathTime(Integer deathTime) {
		this.deathTime =deathTime;
	}

	@Override
	public int getDeathTime() {
		return(this.deathTime);
	}


	@Override
    public void clearLostHeart() {
        this.lostHeart = 0;
    }

    @Nullable
    @Override
    public PlayerWW getLastKiller() {
        return this.killer.isEmpty() ? null : this.killer.get(this.killer.size() - 1);
    }

    @Override
    public boolean isThief() {
        return (this.thief);
    }

    @Override
    public void setThief(boolean thief) {
        this.thief = thief;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public @NotNull StatePlayer getState() {
        return state;
    }

    @Override
    public int getDisconnectedTime() {
        return disconnectedTime;
    }

    @Override
    public void setDisconnectedTime(int disconnectedTime) {
        this.disconnectedTime = disconnectedTime;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }


}

