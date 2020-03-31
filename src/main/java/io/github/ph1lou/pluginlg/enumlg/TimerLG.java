package io.github.ph1lou.pluginlg.enumlg;

public enum TimerLG {
	INVULNERABILITY(30),
	ROLE_DURATION(1200),
	LG_LIST(1800),
	PVP(1500),
	VOTE_BEGIN(2400),
	BORDER_BEGIN(3600),
	DIGGING(4200),
	COUPLE_DURATION(240),
	MASTER_DURATION(240),
	ANGE_DURATION(240),
	CITIZEN_DURATION(60),
	DAY_DURATION(300),
	VOTE_DURATION(180),
	POWER_DURATION(240),
	BORDER_DURATION(280),
	RENARD_SMELL_DURATION(120);

	private final int value;
	
	TimerLG(int value) {
		this.value=value;
	}

	public int getValue() {
		return this.value;
	}

}



