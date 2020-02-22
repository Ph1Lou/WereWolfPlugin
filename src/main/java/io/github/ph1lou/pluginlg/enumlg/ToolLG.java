package io.github.ph1lou.pluginlg.enumlg;

public enum ToolLG {
	victoireneutreequipe(false,"Les Neutres d'un même Rôle peuvent gagner en Equipe"),
	victoirecoupleonly(true,"Les Couples ne peuvent gagner qu'en Couple"),
	xpboost(true,"Boost d'XP"),
	chat(true,"Chat"),
	apple(true,"Plus de Pomme"),
	rodless(true,"Rodless"),
	horseless(true,"Horseless"),
	fireless(true,"Fireless"),
	cutclean(true,"Cutclean"),
	diamondlimit(true,"Limite de Diamants"),
	show_role_to_death(true,"Affiche le Rôle à la Mort"),
	compo_visible(true,"Composition Visible"),
	vote(true,"Vote"),
	distance_middle(true,"Distance au Centre"),
	lg_liste(true,"Liste des LG"),
	compass_target_last_death(true,"La boussole pointe sur le lieu de la derniere mort"),
	event_voyante_death(true,"Event à la mort d'une Voyante"),
	autorezsorciere(false,"la Sorcière peut s'autorez"),
	autorezinfect(false,"L'infect peut s'infecter"),
	nofall(false,"Nofall"),
	snowball(false,"Boules de Neige"),
	poison(false,"Poison"),
	barre_couple(true,"Barre de Vie Couple en Commun"),
	polygamie(true,"Polygamie"),
	fast_smelting(true,"FastMelting"),
	hastey_boys(true,"HasteyBoys");

	private Boolean value;
	private String appearance;
	
	private ToolLG(Boolean value, String appearance) {
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


