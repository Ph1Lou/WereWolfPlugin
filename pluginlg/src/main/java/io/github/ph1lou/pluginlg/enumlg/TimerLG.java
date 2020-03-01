package io.github.ph1lou.pluginlg.enumlg;

public enum TimerLG {
	ROLE_DURATION(1200,"Révélation des Rôles"),
	LG_LIST(1800,"Révélation de la liste des Loups"),
	PVP(2100,"PVP"),
	VOTE_BEGIN(2400,"Début des Votes"),
	BORDER_BEGIN(3600,"Début de la Bordure"),
	DIGGING(4200,"Fin du Minage"),
	COUPLE_DURATION(240,"Durée pour le choix du Couple et révélation des couples"),
	MASTER_DURATION(240,"Durée pour le choix d'un maitre"),
	ANGE_DURATION(240,"Durée pour le choix de l'Ange"),
	CITIZEN_DURATION(60,"Durée pour le choix du Citoyen"),
	DAY_DURATION(300,"Durée du Jour/Nuit"),
	VOTE_DURATION(180,"Durée du Vote"),
	POWER_DURATION(240,"Durée d'utilisation des pouvoirs le Matin"),
	RENARD_SMELL_DURATION(180,"Durée du Flair du Renard");

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



