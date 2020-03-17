package io.github.ph1lou.pluginlg.enumlg;

public enum TimerLG {
	ROLE_DURATION(1200,"Révélation des Rôles (§3%s§r)"),
	LG_LIST(1800,"Révélation de la liste des Loups (§3%s§r)"),
	PVP(1500,"PVP (§3%s§r)"),
	VOTE_BEGIN(2400,"Début des Votes (§3%s§r)"),
	BORDER_BEGIN(3600,"Début de la Bordure (§3%s§r)"),
	DIGGING(4200,"Fin du Minage (§3%s§r)"),
	COUPLE_DURATION(240,"Durée pour le choix du Couple et révélation des couples (§3%s§r)"),
	MASTER_DURATION(240,"Durée pour le choix d'un maitre (§3%s§r)"),
	ANGE_DURATION(240,"Durée pour le choix de l'Ange (§3%s§r)"),
	CITIZEN_DURATION(60,"Durée pour le choix du Citoyen (§3%s§r)"),
	DAY_DURATION(300,"Durée du Jour/Nuit (§3%s§r)"),
	VOTE_DURATION(180,"Durée du Vote (§3%s§r)"),
	POWER_DURATION(240,"Durée d'utilisation des pouvoirs le Matin (§3%s§r)"),
	BORDER_DURATION(280,"Durée de réduction de 100 blocs de la bordure (§3%s§r)"),
	RENARD_SMELL_DURATION(120,"Durée du Flair du Renard (§3%s§r)");

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



