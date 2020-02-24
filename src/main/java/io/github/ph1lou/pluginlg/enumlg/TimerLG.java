package io.github.ph1lou.pluginlg.enumlg;

public enum TimerLG {
	role(1200,"Révélation des Rôles"),
	lg_liste(1800,"Révélation de la liste des Loups"),
	pvp(2100,"PVP"),
	vote_begin(2400,"Début des Votes"),
	beginning_border(3600,"Début de la Bordure"),
	minage(4200,"Fin du Minage"),
	couple_duration(240,"Durée pour le choix du Couple et révélation des couples"),
	maitre_duration(240,"Durée pour le choix d'un maitre"),
	ange_duration(240,"Durée pour le choix de l'Ange"),
	citoyen_duration(60,"Durée pour le choix du Citoyen"),
	day_duration(300,"Durée du Jour/Nuit"),
	vote_duration(180,"Durée du Vote"),
	use_power(240,"Durée d'utilisation des pouvoirs le Matin"),
	flair_renard(240,"Durée du Flair du Renard");

	private int value;
	private String appearance;
	
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



