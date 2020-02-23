package io.github.ph1lou.pluginlg.enumlg;


public enum RoleLG {
	
	COUPLE("§d§lCouple§r",null,true,"","","Vous tombez éperdumment amoureux de "),
	CUPIDON("§d§lCupidon§r", Camp.VILLAGE,true,"Vous désigner un couple au début de partie et vous pouvez faire le choix soit de gagner avec le village soit avec le couple","Vous devez désigner un couple §6/lg couple§r Vous avez ","Votre couple associé est constitué de "),
	LOUP_GAROU("§4§lLoup Garou§r",Camp.LG,true,"","",""),
	INFECT("§cInfect Père des Loups§r",Camp.LG,true,"Une fois dans la partie, vous pouvez décider de ressusciter un joueur mort des griffes des Loups Garous. Ce joueur deviendra alors un Loup-Garou, tout en gardant ses pouvoirs d'origine."," est mort pour l'infecter §6Cliquez ici §r(Vous avez 7sec)","Vous avez infecté "),
	LOUP_FEUTRE("§4§lLoup Feutré§r",Camp.LG,true,"Vous avez un rôle d'affichage différent chaque jour parmi ceux encore en vie pour tromper les Villageois","Votre Rôle d'affichage aujourd'hui est ",""),
	LOUP_PERFIDE("§5§lLoup Perfide§r",Camp.LG,true,"Contrairement aux autres loups, vous pouvez devenir invisible la nuit si vous enlévez votre armure entièrement. Vous pouvez voir les traces de particules de la Petite Fille en bleu et ceux des autres Loup Garou Perfide en Rouge. Vous Créez des particules derrière vous visibles par la Petite Fille.","",""),
	VILAIN_PETIT_LOUP("§6§lVilain Petit Loup§r",Camp.LG,true,"Vous avez la particularité d'avoir Speed I constamment","",""),
	VOLEUR("§9§lVoleur§r",Camp.NEUTRE,true,"En début de partie vous ne faites partie d'aucun camp jusqu'à ce que vous tuez un joueur, vous récupérez le rôle et le camp de ce joueur. Vous avez Résistance jusqu'à votre premier meurtre. Vous aurez 2min de speed et d'abso après votre premier kill. Vous ne pouvez voler qu'une fois.","","Vous devenez "),
	ENFANT_SAUVAGE("§7§lEnfant Sauvage§r",Camp.VILLAGE,true,"En début de partie vous choisissez un maïtre, s'il meurt vous passez dans le camp des Loups Garous","Vous devez désigner un maïtre §6/lg maitre §rSi votre maïtre meurt, vous rejoindrez le camp des Loups Garous. Vous avez ","Votre maitre est "),
	SORCIERE("§5§lSorcière§r",Camp.VILLAGE,true,"Vous avez le pouvoir de ressusciter un joueur mort une fois dans la partie"," est mort pour le ressusciter §6Cliquez ici §r(Vous avez 7sec)","Vous avez ressuscité "),
	PETITE_FILLE("§3§lPetite Fille§r",Camp.VILLAGE,true,"Pendant la nuit vous pouvez devenir invisible pour espionner les alentours en enlevant votre armure. Vous pouvez reperez le loup Garou Perfide quand vous êtes invisible par ces particules Rouges et les autres Petites Filles par des particules Bleues","",""),
	SALVATEUR("§9§lSalvateur§r",Camp.VILLAGE,false,"Chaque jour vous choisirez un oueur afin de le protéger. Il obtiendra l'effet Résistance et NoFall. §6/lg slv","Protégez un joueur pour la journée §6/lg slv§r vous avez ","Vous avez protégé "),
	RENARD("§6§lRenard§r",Camp.VILLAGE,false,"Vous pouvez flairer un joueur afin de connaitre son camp §6/lg flairer§r. Les joueurs devront être distant de moins de 20 blocs pour faire avancer la progression","Flairez un joueur, si vous etes déjà en train de flairer quelqu'un, il sera remplacer par le nouveau joueur et la progression sera remis à 0 §6/lg flairer","Vous commencez à flairer "),
	MONTREUR_OURS("§e§lMontreur d'Ours§r",Camp.VILLAGE,true,"Chaque matin, si un Loup se trouve dans un rayon de 50 blocs autour de vous, vous grognerez une fois par Loup Garou présent aux alentour et tous les joueurs le verront dans le chat global","",""),
	VOYANTE_BAVARDE("§5§lVoyante Bavarde§r",Camp.VILLAGE,false,"Vous pourrez connaïtre tous les jours le camp d'un joueur. Le camp sera affiché dans le chat global. Si malheureusement c'est un membre du Village vous perdrez 3 coeurs en plus jusqu'au matin. §6/lg voir","Découvrez le camp d'un joueur §6/lg voir§r vous avez ","Une Voyante Bavarde vient d'espionner un membre du camp "),
	VOYANTE("§5§lVoyante§r",Camp.VILLAGE,false,"Vous pourrez connaïtre tous les jours le camp d'un joueur. Si malheureusement c'est un membre du Village vous perdrez 3 coeurs en plus jusqu'au matin. §6/lg voir","Découvrez le camp d'un joueur §6/lg voir§r vous avez ","Vous venez d'espionner un membre du camp "),
	TRUBLION("§3§lTrublion§r",Camp.VILLAGE,true,"Vous avez la particularité de téléporter tous les joueurs quand vous mourrez. De plus vous pouvez téléporter aléatoirement un joueur une fois au cours de la partie §6/lg switch ","","Vous venez de trublionner "),
	SOEUR("§2§lSoeur§r",Camp.VILLAGE,true,"Vous disposez de l'effet Résistance lorsque vous êtes à moins de 20 blocs d'une de vos soeurs. De plus vous connaitrez systématiquement, lors de la mort d'une de vos soeur, son assassin","",""),
	ANCIEN("§7§lAncien§r",Camp.VILLAGE,true,"Vous pouvez vous ressusciter une fois au cours de la partie mais vous perdrez votre effet de Résistance. Si c'est un Villageois qui vous a tué vous perdrez aussi trois coeurs permanents","",""),
	CORBEAU("§b§lCorbeau§r",Camp.VILLAGE,false,"Son vote compte double. Une fois par jour, il peut maudire un joueur qui obtiendra Jump Boost II pendant un jour. Vous avez NoFall. §6/lg maudire","Maudissez un joueur pour la journée §6/lg maudire§r vous avez ","Vous venez de maudire "),
	VILLAGEOIS("§e§lVillageois§r",Camp.VILLAGE,true,"","",""),
	DETECTIVE("§6§lDétective§r",Camp.VILLAGE,false,"Vous pouvez tester 2 pseudos par jour. Vous saurez alors si ils sont dans le même camp, L'assassin, le voleur qui n'a pas volé, le Loup Garou Blanc et le loup amnésique non transformé sont dans le camp des Neutres §6/lg inspecter ","Enquêtez deux joueurs §6/lg inspecter§r vous avez ",""),
	MINEUR("§6§lMineur§r",Camp.VILLAGE,true,"Vous êtes simple Villageois (avec une pioche :))","",""),
	ANGE_GARDIEN("§5§lAnge Gardien§r",Camp.NEUTRE,true,"Vous êtes Ange Gardien vous devez protéger une personne choisie aléatoirement et gagner avec elle (ou seul). Si celui-ci meurt, vous repassez à 10 coeurs.","","Votre protégé est "),
	ANGE_DECHU("§5§lAnge Déchu§r",Camp.NEUTRE,true,"Vous êtes Ange Déchu vous devez gagner seul. Vous possédez 12 coeurs de vie. Une cible vous est assignée aléatoirement si vous la tuez, vous passerez à 15 coeurs jusqu'à la fin de la partie.","","Votre cible est "),
	LOUP_AMNESIQUE("§c§lLoup Amnesique§r",Camp.NEUTRE,true,"Si vous tuez un Villageois, vous passez dans le camp des Loups Garous, Vous ne pouvez pas gagner avec les Villageois","",""),
	LOUP_GAROU_BLANC("§c§lLoup Garou Blanc§r",Camp.NEUTRE,true,"Votre objectif est de gagner seule, vous êtes infiltré parmis les Loups Garous. Vous avez Force la nuit et 2min de speed et d'abso après chaque kill, ainsi que 15 coeurs permanents","",""),
	ASSASSIN("§6§lAssassin§r",Camp.NEUTRE,true,"Votre objectif est de gagner seule, vous êtes infiltré parmis les Villageois. Vous avez Force le jour.","","");
	
	private String appearance;
	private Camp camp;
	private Boolean power;
	private String description;
	private String poweruse;
	private String powerhasbeenuse;
	
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
