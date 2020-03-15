package io.github.ph1lou.pluginlg.enumlg;

public enum TimerLG {
	ROLE_DURATION(1200,"Révélation des Rôles (%s)"),
	LG_LIST(1800,"Révélation de la liste des Loups (%s)"),
	PVP(1800,"PVP (%s)"),
	VOTE_BEGIN(2400,"Début des Votes (%s)"),
	BORDER_BEGIN(3600,"Début de la Bordure (%s)"),
	DIGGING(4200,"Fin du Minage (%s)"),
	COUPLE_DURATION(240,"Durée pour le choix du Couple et révélation des couples (%s)"),
	MASTER_DURATION(240,"Durée pour le choix d'un maitre (%s)"),
	ANGE_DURATION(240,"Durée pour le choix de l'Ange (%s)"),
	CITIZEN_DURATION(60,"Durée pour le choix du Citoyen (%s)"),
	DAY_DURATION(300,"Durée du Jour/Nuit (%s)"),
	VOTE_DURATION(180,"Durée du Vote (%s)"),
	POWER_DURATION(240,"Durée d'utilisation des pouvoirs le Matin (%s)"),
	BORDER_DURATION(280,"Durée de réduction de 100 blocs de la bordure (%s)"),
	RENARD_SMELL_DURATION(120,"Durée du Flair du Renard (%s)");

	private final int value;
	private final String appearance;
	
	TimerLG(int value, String appearance) {
		this.value=value;
		this.appearance=appearance;
	}

	public int getValue() {
		return this.value;
	}

	public String getAppearance() {
		return this.appearance;
	}
}



