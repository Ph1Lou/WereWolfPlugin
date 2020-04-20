package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLG {
    public final Map<TimerLG, Integer> timerValues = new HashMap<>();
    public final Map<BorderLG, Integer> borderValues = new HashMap<>();
    public final Map<ToolLG, Boolean> configValues = new HashMap<>();
    public final Map<RoleLG, Integer> roleCount = new HashMap<>();
    public final Map<ScenarioLG, Boolean> scenarioValues = new HashMap<>();

    private int strengthRate = 130;
    private int resistanceRate = 20;
    private int appleRate = 10;
    private int flintRate = 10;
    private int pearlRate = 30;
    private int xpBoost = 500;
    private int playerRequiredBeforeVotingEnds = 10;
    private int diamondLimit = 17;
    private int limitProtectionIron = 3;
    private int limitProtectionDiamond = 2;
    private int limitSharpnessDiamond = 3;
    private int limitSharpnessIron = 4;
    private int limitPowerBow = 3;
    private int limitPunch = 1;
    private int limitKnockBack = 1;
    private int useOfFlair = 4;
    private int goldenAppleParticles = 1;
    private int distanceBearTrainer = 50;
    private int distanceFox = 20;
    private boolean trollSV = false;

    public void getConfig(MainLG main, String configName) {

        ConfigLG config_load = this;

        File file = new File(main.getDataFolder() + File.separator + "configs" + File.separator, configName + ".json");

        if (file.exists()) {
            config_load = main.serialize.deserialize(main.filelg.loadContent(file));
            this.setDiamondLimit(config_load.getDiamondLimit());
            this.setStrengthRate(config_load.getStrengthRate());
            this.setPlayerRequiredVoteEnd(config_load.getPlayerRequiredVoteEnd());
            this.setAppleRate(config_load.getAppleRate());
            this.setFlintRate(config_load.getFlintRate());
            this.setPearlRate(config_load.getPearlRate());
            this.setXpBoost(config_load.getXpBoost());
            this.setLimitPowerBow(config_load.getLimitPowerBow());
            this.setLimitSharpnessIron(config_load.getLimitSharpnessIron());
            this.setLimitSharpnessDiamond(config_load.getLimitSharpnessDiamond());
            this.setLimitProtectionDiamond(config_load.getLimitProtectionDiamond());
            this.setLimitProtectionIron(config_load.getLimitProtectionIron());
            this.setLimitKnockBack(config_load.getLimitKnockBack());
            this.setLimitPunch(config_load.getLimitPunch());
            this.setUseOfFlair(config_load.getUseOfFlair());
            this.setGoldenAppleParticles(config_load.getGoldenAppleParticles());
            this.setDistanceBearTrainer(config_load.getDistanceBearTrainer());
            this.setDistanceFox(config_load.getDistanceFox());
            this.setResistanceRate(config_load.getResistanceRate());
        }
		for(RoleLG role:RoleLG.values()) {
            this.roleCount.put(role, config_load.roleCount.getOrDefault(role, 0));
        }
		
		for(ToolLG tool:ToolLG.values()) {
            this.configValues.put(tool, config_load.configValues.getOrDefault(tool, tool.getValue()));
        }
		
		for(TimerLG timer:TimerLG.values()) {
            this.timerValues.put(timer, config_load.timerValues.getOrDefault(timer, timer.getValue()));
        }
		
		for(BorderLG border: BorderLG.values()) {
            this.borderValues.put(border, config_load.borderValues.getOrDefault(border, border.getValue()));
        }

		for(ScenarioLG scenarios:ScenarioLG.values()) {
            this.scenarioValues.put(scenarios, config_load.scenarioValues.getOrDefault(scenarios, scenarios.getValue()));
        }

		main.filelg.save(file, main.serialize.serialize(this));
		
		main.score.setRole(0);
		
		for (RoleLG role:RoleLG.values()) {
            if (role.getCamp() != null) {
                main.score.setRole(main.score.getRole() + this.roleCount.get(role));
            }
        }
	}

	public int getDiamondLimit() {
        return this.diamondLimit;
    }

	public void setDiamondLimit(int diamond_limit) {
        this.diamondLimit = diamond_limit;
    }

	public int getStrengthRate() {
        return this.strengthRate;
    }

	public void setStrengthRate(int strength_rate) {
        this.strengthRate = strength_rate;
    }

    public int getPlayerRequiredVoteEnd() {
        return this.playerRequiredBeforeVotingEnds;
    }

    public void setPlayerRequiredVoteEnd(int player_required_before_voting_ends) {
        this.playerRequiredBeforeVotingEnds = player_required_before_voting_ends;
    }

    public int getPearlRate() {
        return pearlRate;
    }

    public void setPearlRate(int pearlRate) {
        this.pearlRate = pearlRate;
    }

    public int getFlintRate() {
        return flintRate;
    }

    public void setFlintRate(int flintRate) {
        this.flintRate = flintRate;
    }

    public int getAppleRate() {
        return appleRate;
    }

    public void setAppleRate(int appleRate) {
        this.appleRate = appleRate;
    }

    public int getXpBoost() {
        return xpBoost;
    }

    public void setXpBoost(int xpBoost) {
        this.xpBoost = xpBoost;
    }

    public int getLimitProtectionIron() {
        return limitProtectionIron;
    }

    public void setLimitProtectionIron(int limitProtectionIron) {
        this.limitProtectionIron = limitProtectionIron;
    }

	public int getLimitProtectionDiamond() {
		return limitProtectionDiamond;
	}

	public void setLimitProtectionDiamond(int limitProtectionDiamond) {
		this.limitProtectionDiamond = limitProtectionDiamond;
	}

	public int getLimitSharpnessDiamond() {
		return limitSharpnessDiamond;
	}

	public void setLimitSharpnessDiamond(int limitSharpnessDiamond) {
		this.limitSharpnessDiamond = limitSharpnessDiamond;
	}

	public int getLimitSharpnessIron() {
		return limitSharpnessIron;
	}

	public void setLimitSharpnessIron(int limitSharpnessIron) {
		this.limitSharpnessIron = limitSharpnessIron;
	}

	public int getLimitPowerBow() {
		return limitPowerBow;
	}

	public void setLimitPowerBow(int limitPowerBow) {
		this.limitPowerBow = limitPowerBow;
	}

	public int getLimitKnockBack() {
		return limitKnockBack;
	}

	public void setLimitKnockBack(int limitKnockBack) {
		this.limitKnockBack = limitKnockBack;
	}

	public int getLimitPunch() {
		return limitPunch;
	}

	public void setLimitPunch(int limitPunch) {
		this.limitPunch = limitPunch;
	}

	public int getUseOfFlair() {
		return useOfFlair;
	}

	public void setUseOfFlair(int useOfFlair) {
        this.useOfFlair = useOfFlair;
    }

    public int getGoldenAppleParticles() {
        return goldenAppleParticles;
    }

    public void setGoldenAppleParticles(int goldenAppleParticles) {
        this.goldenAppleParticles = goldenAppleParticles;
    }

    public int getDistanceBearTrainer() {
        return distanceBearTrainer;
    }

    public void setDistanceBearTrainer(int distanceBearTrainer) {
        this.distanceBearTrainer = distanceBearTrainer;
    }

    public int getDistanceFox() {
        return distanceFox;
    }

    public void setDistanceFox(int distanceFox) {
        this.distanceFox = distanceFox;
    }

    public int getResistanceRate() {
        return resistanceRate;
    }

    public void setResistanceRate(int resistanceRate) {
        this.resistanceRate = resistanceRate;
    }

    public boolean isTrollSV() {
        return trollSV;
    }

    public void setTrollSV(boolean trollSV) {
        this.trollSV = trollSV;
    }
}
