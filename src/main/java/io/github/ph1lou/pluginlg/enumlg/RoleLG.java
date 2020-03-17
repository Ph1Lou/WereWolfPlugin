package io.github.ph1lou.pluginlg.enumlg;


public enum RoleLG {
	
	COUPLE("Couple",null,true,"§6LOUP-GAROU §7➽§d Vous tombez eperdumment amoureux de %s.\nSi l'un meurt, vous le rejoindrez par amour","",""),
	CUPIDON("Cupidon", Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Cupidon§9. Votre objectif est d’éliminer les Loups-Garous.\nVous avez la possibilité de mettre deux joueurs en couple avec la commande §3/lg couple§9.\n Vous pouvez gagner avec le Couple ou avec le Village.\n","Vous devez désigner un couple §6/lg couple§r Vous avez %s","Votre couple associé est constitué de %s et %s"),
	LOUP_GAROU("Loup Garou",Camp.LG,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Loup-Garou§9. Votre objectif est de tuer les Villageois. \nPour ce faire, vous disposez des effets Force (la nuit) et Vision Nocturne. Après chaque kill vous aurez l'effet Vitesse et 2 coeur d'absorption pendant 2 minutes. \nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.","",""),
	INFECT("Infect Père des Loups",Camp.LG,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Infect Père des Loups Garous§9. Votre objectif est de tuer les Villageois.\nPour ce faire,vous disposez des effets Force (la nuit) et Vision Nocturne. Vous pouvez ressusciter un joueur de votre choix tué par les Loups-Garous. Ce joueur rejoindra votre Camp, tout en gardant ses pouvoirs d’origines. Après chaque kill vous aurez l'effet Vitesse et 2 coeurs d’absorption pendant 2 minutes. \nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.\n","%s est mort pour l'infecter §6Cliquez ici §r(Vous avez 7sec)","Vous avez infecté %s"),
	LOUP_FEUTRE("Loup Feutré",Camp.LG,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Loup Feutre§9. Votre objectif est de tuer les Villageois.\nVous avez un rôle d’affichage différent chaque jour parmi ceux encore en vie pour tromper le village. Vous disposez des effets Force (la nuit) et Vision Nocturne. Après chaque kill, vous aurez l'effet Vitesse et 2 coeurs d’absorption pendant 2 minutes. \nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.","Votre Rôle d'affichage aujourd'hui est %s",""),
	LOUP_PERFIDE("Loup Perfide",Camp.LG,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Loup Perfide§9. Votre objectif est de tuer les Villageois. \nContrairement aux Loups Garous, vous disposez d’un effet d’invisibilité et de faiblesse la nuit lorsque vous enlevez votre armure.\nVous laissez des particules rouges derrière vous et vous pouvez repérer les Petites Filles par leurs particules bleues. Après chaque kill vous aurez l'effet Vitesse et 2 coeurs d’absorption pendant 2 minutes.\nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.","",""),
	VILAIN_PETIT_LOUP("Vilain Petit Loup",Camp.LG,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Vilain Petit Loup§9. Votre objectif est de tuer les Villageois. \nPour ce faire, vous disposez des effets Force (la nuit), Vision Nocturne et Vitesse constamment. Après chaque kill vous gagnez 2 coeurs d’absorption pendant 2 minutes. \nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.","",""),
	VOLEUR("Voleur",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Voleur§9. En début de partie vous ne faites partie d'aucun  camp jusqu'à ce que vous tuez un joueur. \nVous récupérez son rôle et devrez gagner avec son Camp. Vous possédez l’effet Résistance avant d'avoir fait votre premier meurtre et l'effet Vitesse et 2 coeurs d’absorptions 2 min après.","","Vous devenez %s"),
	ENFANT_SAUVAGE("Enfant Sauvage",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Enfant Sauvage§9. Votre objectif est d’éliminer les Loups-Garous. \nEn début de partie, vous choisissez un maître avec §1/lg maitre§9. Si celui ci meurt, vous devenez un Loup-Garou.","Vous devez désigner un maïtre §6/lg maitre §rSi votre maïtre meurt, vous rejoindrez le camp des Loups Garous. Vous avez %s","Votre maitre est %s"),
	SORCIERE("Sorcière",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Sorcière§9. Votre objectif est d’éliminer les Loups-Garous.\nPour cela, vous recevez 3 potions de Soins, 3 potions de Dégats et 1 potion de Régénération. Une fois dans la partie, vous pouvez ressusciter un joueur (ou vous même si l'option est activé).","%s est mort pour le ressusciter §6Cliquez ici §r(Vous avez 7sec)","Vous avez ressuscité %s"),
	PETITE_FILLE("Petite Fille",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Petite-Fille§9. Vous objectif est d’éliminer les Loups-Garous. /nDurant la nuit, vous pouvez devenir invisible en enlevant votre armure. Mais attention, vous laissez des particules bleues derrière vous visible par le Loup Perfide. \nHeureusement vous pouvez repérer les Loups-Perfide par leurs particules rouges.","",""),
	SALVATEUR("Salvateur",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Salvateur§9. Votre objectif est d’éliminer les Loups-Garous. \nVous recevrez 3 potions de Heals. Chaque jour, vous pouvez décider de protéger un joueur avec §3/lg slv§9, il recevra les effets Résistance et Chute Amortie","Protégez un joueur pour la journée §6/lg slv§r vous avez %s","Vous avez protégé %s"),
	RENARD("Renard",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Renard§9. Votre objectif est d’éliminer les Loups-Garous. \nVous avez Vitesse constamment. Vous pouvez flairer les joueurs avec §3/lg flairer§9 afin de savoir si ils sont Loup-Garou. Les joueurs devront se situer à moins de 20 blocs de vous. La durée du flair dépend des paramètres de la partie.","Flairez un joueur, si vous etes déjà en train de flairer quelqu'un, il sera remplacer par le nouveau joueur et la progression sera remis à 0 §6/lg flairer","Vous commencez à flairer %s"),
	MONTREUR_OURS("Montreur d'Ours",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Montreur d’ours§9. Votre objectif est d’éliminer les Loups-Garous. \nVous avez la particularité de grogner le début du jour si un Loup-Garou se trouve à 50 blocs de vous. Un message sera envoyé dans le chat global.","Ours : %s","Grrr "),
	VOYANTE_BAVARDE("Voyante Bavarde",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Voyante Bavarde§9. Votre objectif est d’éliminer les Loups-Garous. \nVous obtenez 4 obsidiennes et 4 bibliothèques. Le Jour, Vous pouvez connaître le camp d'un joueur avec §3/lg voir§9. \nCependant si c'est un Villageois, vous perdrez 3 coeurs. \nLe camp sera affiché dans le chat global.","Découvrez le camp d'un joueur §6/lg voir§r vous avez %s","Une Voyante Bavarde vient d'espionner un membre du camp %s"),
	VOYANTE("Voyante",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Voyante§9. Votre objectif est d’éliminer les Loups-Garous\nVous obtenez 4 obsidiennes et 4 bibliothèques. Le Jour, Vous pouvez connaître le camp d'un joueur avec §3/lg voir§9.\nCependant si c'est un Villageois, vous perdrez 3 coeurs.","Découvrez le camp d'un joueur §6/lg voir§r vous avez %s","Vous venez d'espionner un membre du camp %s"),
	TRUBLION("Trublion",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Trublion§9. Votre objectif est d’éliminer les Loups-Garous.\nUne fois dans la partie, vous pouvez téléporter un joueur aléatoirement sur la map avec §3/lg switch§9. Lorsque vous mourrez tous les joueurs sont téléportées aux quatres coins de la carte.","","Vous venez de trublionner %s"),
	SOEUR("Soeur",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Soeur§9. Votre objectif est d’éliminer les Loups-Garous. \nVous possédez un effet de Résistance lorsque vous vous trouvez à moins de 20 blocs d'une de vos soeurs. Lorsqu'une soeur meurt vous connaîtrez son assassin.","","%s a été tué par %s"),
	ANCIEN("Ancien",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽ §9Vous êtes §1Ancien§9. Votre objectif est d’éliminer les Loups-Garous.\nPour cela, vous possédez l’effet Résistance et une seconde vie.\nVous perdrez votre Résistance à votre première mort, mais attention, si c’est un Villageois qui vous tue, vous perdrez également 1/3 de votre vie.","",""),
	CORBEAU("Corbeau",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Corbeau§9. Votre objectif est d’éliminer les Loups-Garous.\nVous possédez l'effet Chute Amortie et votre vote compte double.\nVous pouvez maudire un jouer avec la commande §3/lg maudire§9, il obtiendra un effet de Jump Boost II pendant une journée.","Maudissez un joueur pour la journée §6/lg maudire§r vous avez %s","Vous venez de maudire %s"),
	VILLAGEOIS("Villageois",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Villageois§9. Votre objectif est d’éliminer les Loups-Garous.\nVous n'avez aucun pouvoir","",""),
	DETECTIVE("Détective",Camp.VILLAGE,false,"§6LOUP-GAROU §7➽§9 Vous êtes §1Détective§9. Votre objectif est d’éliminer les Loups-Garous. \nChaque jour, vous pouvez tester 2 joueurs avec §3/lg inspecter§9 afin de savoir s'ils appartiennent au même camp. \nAttention, L'Assassin, le Voleur qui n'a pas volé, le Loup-Garou-Blanc et le Loup Amnésique non transformé appartiennent au camp des Neutres.","Enquêtez deux joueurs §6/lg inspecter§r vous avez %s",""),
	MINEUR("Mineur",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Mineur§9. Votre objectif est d’éliminer les Loups-Garous.\nVous possédez une pioche en diamant et l'effet Célérité.","",""),
	CITOYEN("Citoyen",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Citoyen§9. Votre objectif est d’éliminer les Loups-Garous.\nVous pourrez une fois dans le partie décider de sois voir les Votes §3/lg cancelvote§9, sois les annuler §3/lg depouiller§9.","Vous pouvez annuler le vote §6/lg cancelvote§r ou dépouiller le vote §6/lg depouiller§r vous avez %s","Vote annulé, vous avez sauvé %s"),
	FRERE_SIAMOIS("Frère Siamois",Camp.VILLAGE,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Frères Siamois§9. Votre objectif est d’éliminer les Loups-Garous. \nVous avez été séparé de votre frère à la naissance, cependant vous avez gardé un lien très fort avec lui. En plus de disposez de 3 coeurs supplémentaires, vos coeurs au dela du cinquième sont liées.","",""),
	ANGE("Ange§r",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Ange§9. Votre objectif est de gagner seul. \nSi vous choisissez §1Ange Gardien§9, vous devrez protéger une personne choisie aléatoirement et gagner avec elle (ou seul). Si celle-ci meurt, vous repasserez à 10 coeurs. Si vous choisissez §1Ange Déchu§9, une cible vous sera assignée aléatoirement si vous la tuez, vous passerez à 15 coeurs jusqu'à la fin de la partie.","Vous pouvez décider d'être sois Ange Déchu §6/lg dechu§r sois Ange Gardien §6/lg gardien§r vous avez %s","Vous venez de choisir %s"),
	ANGE_GARDIEN("Ange Gardien",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Ange Gardien§9. Vous devez protéger une personne choisie aléatoirement et gagner avec elle (ou seul). Si celle-ci meurt, vous repassez à 10 coeurs.","Votre protégé a été tué, vous perdez 2 coeurs","Votre protégé est %s"),
	ANGE_DECHU("Ange Déchu",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Ange Déchu§9. Vous devez gagner seul. \nVous possédez 2 coeurs supplémentaires. Une cible vous est assignée aléatoirement si vous la tuez, vous gagnerez trois coeurs supplémentaires jusqu'à la fin de la partie.","Vous avez tué votre cible, vous gagnez 3 coeurs","Votre cible est %s"),
	LOUP_AMNESIQUE("Loup Amnesique",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Loup-Garou Amnésique§9. Votre objectif est de tuer les Villageois. \nPour ce faire, vous disposez des effets Force (la nuit) et Vision Nocturne. A chaque  kill, vous gagnez Vitesse et 2 coeurs d’absorption pendant 2 minutes. Cependant, vous n'avez pas accès à la liste des Loups et les autres Loups ne pourront pas vous voir dans leur liste tant que vous n’avez pas tuer un Villageois.\nAttention vous n'êtes pas dans la camp des Loups-Garous tant que vous n'avez pas tué un Villageois","",""),
	LOUP_GAROU_BLANC("Loup Garou Blanc",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Loup-Garou Blanc§9. Votre objectif est de gagner seul en étant infiltré parmi les Loups-Garous.\nPour ce faire, vous disposez des effets Force (la nuit) et de Vision Nocturne, ainsi que 5 coeurs supplémentaires. Après chaque kill, vous gagnez Vitesse et 2 coeurs d’absorption pendant 1 minute.\nFaites §3/lg lg§9 pour voir la liste des Loups-Garous.","",""),
	ASSASSIN("Assassin",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9 Vous êtes §1Assassin§9. Votre objectif est de gagner seul.\nPour ce faire, vous disposez de 3 livres : livre Sharpness III, livre Protection III et livre Power III. \nVous disposez aussi de l’effet Force (le jour) et vous êtes infiltré dans le camp des Villageois.","",""),
	TUEUR_EN_SERIE("Tueur en série",Camp.NEUTRAL,true,"§6LOUP-GAROU §7➽§9  Vous êtes §1Tueur en Série§9. Votre objectif est de gagner seul.\nPour ce faire, vous possédez 3 livres qui sont : livre Sharpness III, livre Protection III et livre Power III.\nLorsque vous faites un kill, vous gagnez un coeur supplémentaire.","","");

	private final String appearance;
	private final Camp camp;
	private final Boolean power;
	private final String description;
	private final String poweruse;
	private final String powerhasbeenuse;
	
	RoleLG(String appearance, Camp camp, Boolean power, String description, String poweruse, String powerhasbeenuse) {
		this.appearance=appearance;
		this.camp=camp;
		this.power=power;
		this.description=description;
		this.poweruse=poweruse;
		this.powerhasbeenuse=powerhasbeenuse;
	}
	
	public String getAppearance() {
		return this.appearance;
	}

	public Camp getCamp() {
		return this.camp;
	}


	public Boolean getPower() {
		return this.power;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPowerUse() {
		return this.poweruse;
	}

	public String getPowerHasBeenUse() {
		return this.powerhasbeenuse;
	}
}
