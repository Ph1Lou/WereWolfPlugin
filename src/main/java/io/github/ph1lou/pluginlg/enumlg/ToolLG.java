package io.github.ph1lou.pluginlg.enumlg;

public enum ToolLG {

	VICTORY_NEUTRAL(false,"Les Neutres d'un même Rôle peuvent gagner en Equipe"),
	VICTORY_COUPLE(true,"Les Couples ne peuvent gagner qu'en Couple"),
	CHAT(true,"Chat"),
	SHOW_ROLE_TO_DEATH(true,"Affiche le Rôle à la Mort"),
	COMPO_VISIBLE(true,"Composition Visible"),
	VOTE(true,"Vote"),
	LG_LIST(true,"Liste des LG"),
	EVENT_VOYANTE_DEATH(true,"Event à la mort d'une Voyante"),
	AUTO_REZ_WITCH(false,"la Sorcière peut s'autorez"),
	AUTO_REZ_INFECT(false,"L'infect peut s'infecter"),
	POLYGAMY(false,"Polygamie"),
	COMPASS_MIDDLE(false,"La boussole pointe vers le centre"),
	AUTO_GROUP(true,"Groupe Auto");

	private final Boolean value;
	private final String appearance;
	
	ToolLG(Boolean value, String appearance) {
		this.value=value;
		this.appearance=appearance;
	}
	
	public Boolean getValue() {
		return this.value;
	}

	public String getAppearance() {
		return this.appearance;
	}
	
	
}


