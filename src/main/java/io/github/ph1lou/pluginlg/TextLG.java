package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.BordureLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class TextLG {
	
	public Map<RoleLG, String> translaterole = new HashMap<>();
	public Map<RoleLG, String> description = new HashMap<>();
	public Map<RoleLG, String> poweruse = new HashMap<>();
	public Map<RoleLG, String> powerhasbeenuse = new HashMap<>();
	public Map<ToolLG, String> translatetool = new HashMap<>();
	public Map<TimerLG, String> translatetimer = new HashMap<>();
	public Map<BordureLG, String> translatebordure = new HashMap<>();

	public String[][] DEFAULT= {
			
			
			{"","§9","","§6En attente de joueurs ","","§eJoueurs : ","","","",""},
			{"§9","","§eTimer ","§aJour ","§cSurvivants ","§6Groupe de ","§bBordure ","§eCentre ","§6Y ",""},
			
			{
					"§4§l[LG UHC] §6Loup Garou UHC",
					esthetique("§m", "§6","Bienvenue dans cette partie de Loup Garou"),
					esthetique("§m", "§2","Le plugin est à jour"),
					"Victoire ",
					"Equipe ",
					"§cDe la Mort",
					esthetique("§m", "§6","PVP activé"),
					esthetique("§m", "§6","La bordure commence à se déplacer"),
					esthetique("§m", "§6","Minage Désactivé"),
					esthetique("§m", "§6","Il reste moins de 10 joueurs, les Votes sont désactivés"),
			/*10*/	"",
					"\n§5Si l'un de vous meurt, l'autre le rejoindra par amour, de plus vous partagez 4 coeurs avec votre amant",
					esthetique("§m", "§6","Pas assez de personnes pour former un couple"),
					esthetique("§6§m", "§4","Vous ne pouvez plus utiliser votre pouvoir aujourd'hui"),
					esthetique("§2§m", "§6","C'est la nuit, enlever votre armure pour devenir invisible"),
					"",
					esthetique("§m", "§6","C'est le Jour"),
					"C'est l'heure de voter §6/lg vote§r Vous avez ",
					esthetique("§6§m", "§e","Vous redevenez visible"),
					esthetique("§6§m", "§e","Vous n'êtes plus maudit"),
			/*20*/	esthetique("§6§m", "§e","Vous n'avez plus la salvation"),
					esthetique("§6§m", "§4","Votre monde doit s'appeler world"),
					"",
					"",
					"",
					"",
					"",
					"",
					" est mort, il était ",
					" est mort",
			/*30*/	" par amour, le rejoins dans sa tombe",
					esthetique("§m", "§2","Vous venez de ressusciter"),
					esthetique("§m", "§e","Un Trublion vient de mourir, tous les joueurs sont téléportés aléatoirement"),
					" a été tué par ",
					esthetique("§m", "§2","Vous avez tuez votre cible, vous gagnez 3 coeurs"),
					esthetique("§6§m", "§4","Votre cible a été tuez, vous perdez 2 coeurs"),
					"Avant de mourir, la Voyante a fait apparaitre ",
					" caches sur la map, une seule renferme le nom d'un Ennemi du Village",
					esthetique("§6§m", "§4","Le jeu a déjà commencé"),
					"Progression ",
			/*40*/	" n'est pas dans le camp des §4Loups Garous",
					" est dans le camp des §4Loups Garous",
					"Vous êtes ",
					"\nPour plus d'informations §6/lg role",
					"",
					"",
					"",
					"",
					"",
					"",
			/*50*/	esthetique("§m", "§6","un nouveau joueur a rejoint la meute §6/lg lg"),
					esthetique("§m", "§6","Vous passez dans le camp des §4Loups Garous §6/lg lg"),
					esthetique("§m", "§6","Vous distinguez maintenant les Loups Garous §6/lg lg"),
					esthetique("§6§m", "§4","La Composition est cachée"),
					esthetique("§6§m", "§4","La commande prend un joueur en entrée"),
					esthetique("§m", "§2","Vous venez d'être maudit par le corbeau. Vous avez Jump Boost"),
					"",
					"",
					"",
					"",
			/*60*/	"",
					esthetique("§m", "§2","Le Salvateur vous protège pour la journée, vous avez Résistance et Nofall"),
					"",
					"",
					"",
					"",
					"",
					esthetique("§6§m", "§4","Vous n'êtes pas dans la partie"),
					esthetique("§6§m", "§4","La partie est déjà finie ou les rôles n'ont pas encore été annoncés"),
					"Vous êtes dans le camp des §4Loups Garous §r, votre but ? décimer le §eVillage§r. Vous avez Force la nuit et 2min de speed et d'abso après chaque kill",
			/*70*/	"Vous êtes dans le camp des §eVillageois§r. Votre but est d'éliminer les §4Loups Garous§r et autres dangers du §eVillage§r",
					" sont dans le même camp",
					" ne sont pas dans le même camp",
					"",
					"",
					"",
					"",
					"",
					"",
					"",
			/*80*/	"",
					"",
					"",
					"",
					"",
					"",
					"",
					"",
					"",
					"",
			/*90*/	"",
					"",
					"",
					"",
					esthetique("§m", "§2","Un Citoyen a annulé le vote"),
					"§6Dépouillement ",
					" a voté pour ",
					esthetique("§6§m", "§4","Vous n'êtes pas Vivant"),
					esthetique("§6§m", "§4","Vous n'êtes pas un Loup Garou"),
					esthetique("§6§m", "§4","La liste des Loups Garous n'est pas activée"),
			/*100*/	esthetique("§6§m", "§4","La liste des Loups Garous n'est pas encore dévoilée"),
					"§4Liste des Loups Garous :§r ",
					"§6/lg timer §9Pour voir les timers\n§6/lg regles §9Pour voir les règles\n§6/lg compo §9Pour voir la compo\n§6/lg role §9Pour voir votre rôle",
					esthetique("§6§m", "§4","Votre pouvoir ne peut pas encore être utilisé ou a déja été utilisé"),
					esthetique("§6§m", "§4","Vous devez sélectionner deux joueurs différents"),
					esthetique("§6§m", "§4","Vous ne pouvez pas vous choisir vous même"),
					esthetique("§6§m", "§4","Au moins un joueur n'existe pas, s'est déconnecté ou est déjà mort"),
					esthetique("§6§m", "§4","Le joueur a déja été affecté par votre pouvoir au dernier tour"),
					esthetique("§6§m", "§4","Le joueur n'est pas dans le couloir de la mort"),
					esthetique("§6§m", "§4","Le joueur ne peut pas (plus) être infecté"),
			/*110*/	esthetique("§6§m", "§4","Le joueur ne peut pas encore être ressuscité"),
					esthetique("§6§m", "§4","Vous devez être à moins de 20 blocs de votre cible"),
					esthetique("§6§m", "§4","Vous n'avez plus assez de vie pour espionner un joueur"),
					esthetique("§6§m", "§4","Vous avez espionner un membre du Village vous perdez 3 coeurs jusqu'au prochain matin"),
					esthetique("§6§m", "§4","Au moins un de ces joueurs a déjà été enquêté"),
					"",
					esthetique("§6§m", "§4","Seul un Administrateur à accès à cette commande"),
					"",
					esthetique("§m", "§2","Host Configuré"),
					esthetique("§6§m", "§4","La partie n'est pas finie"),	
			/*120*/	esthetique("§6§m", "§4","Les effectifs ne sont pas suffisant pour lancer la partie avec cette configuration."),
					esthetique("§m", "§e","Plus d'infos sur la partie §6/lg h"),
					esthetique("§m", "§2","Le Chat est ON"),
					esthetique("§6§m", "§4","Le Chat est OFF"),
					esthetique("§m", "§e","C'est la Nuit"),
					"§4§l[LG UHC] Loup Garou",
					esthetique("§6§m", "§4","Vous ne pouvez pas changer les rôles une fois qu'ils sont annoncés"),
					esthetique("§m", "§e","Cliquez ici pour valider le stuff de départ"),
					esthetique("§m", "§e","Cliquez ici pour valider le loot à la mort"),
					esthetique("§m", "§e","Vous devenez invisible, remettez votre armure pour redevenir visible"),
			/*130*/esthetique("§m", "§e","En attente d'une potentielle resurrection"),
					esthetique("§6§m", "§4","Commande désactivée"),
					esthetique("§6§m", "§4","Joueur inconnu"),
					"§6[Message de ",
					"§2[Message envoyé à ",
					"",
					"§b[Info] ",
					"Respecter les limites, groupe de ",
					"§6Respectez la limite",
					"§4groupe de ",
			/*140*/	esthetique("§6§m", "§4","Cette commande n'est pas accessible via la console"),
					esthetique("§6§m", "§4","Le joueur est déjà mort"),
					esthetique("§6§m", "§4","Le joueur est en ligne"),
					esthetique("§6§m", "§4","Enlevez un rôle dans la config pour compenser le joueur si besoin est"),
					esthetique("§6§m", "§4","Les rôles n'ont pas encore été attribué"),
					esthetique("§6§m", "§4","Vous ne pouvez pas voir le rôle du joueur si vous êtes encore dans la partie"),
					"§6En Couple Avec : ",
					"§6Affecte : ",
					"§6Tué par : ",
					esthetique("§6§m", "§4","Le joueur n'est pas Mort"),
			/*150*/	esthetique("§m", "§e","Final Heal !"),
					esthetique("§m", "§2","Stuff de départ actualisé"),
					esthetique("§m", "§2","Stuff de mort actualisé"),
					"§6/adminlg config §9Pour configurer la partie\n§6/adminlg host §9Pour configurer l'host\n§6/adminlg start §9Pour lancer la partie\n§6/adminlg chat §9Pour activer/désactiver le chat\n§6/adminlg info §9Pour parler à tous les joueurs\n§6/adminlg groupe §9Pour faire respecter les groupes\n§6/adminlg fh §9Pour FinalHeal\n§6/adminlg inv §9Pour voir l'inventaire d'un joueur\n§6/adminlg killa §9Pour tuer un joueur offline\n§6/adminlg revive §9Pour ressusciter un joueur\n§6/adminlg role §9Pour voir le rôle d'un joueur\n§6/adminlg deco §9Pour voir les joueurs déco\n§6/adminlg setgroupe§9 Pour configurer les groupes",
					" a été ressuscité par un Administrateur",
					esthetique("§6§m", "§4","Les morts ne peuvent pas voter"),
					esthetique("§6§m", "§4","Les Votes ne sont pas encore activé"),
					esthetique("§6§m", "§4","Les Votes sont désactivés"),
					esthetique("§6§m", "§4","Ce n'est plus l'heure du vote"),
					esthetique("§6§m", "§4","Vous avez déjà voté aujourd'hui"),
			/*160*/	esthetique("§6§m", "§4","Joueur non valide"),
					esthetique("§6§m", "§4","Ce joueur a déjà été voté"),
					"Vous avez voté pour ",
					" est celui qui a recueillit §9le plus de vote§r : §e",
					". Il perd 5 coeurs",
					esthetique("§m", "§2","Toutes les caches ont été trouvées"),
					"Les groupes passent à §6",
					" s'est déco il y a ",
					"§4Désactiver ",
					"§2Activer ",
			/*170*/	"Revenir au Menu",
					"Ecraser Configuration ",
					"Charger Configuration ",
					"Sauvegarder dans Configuration ",
					"Configuration ",
					"§8Option LG",
					"§8Configuration Rôles",
					"§8Configuration Timers",
					"§8Configuration Globale",
					"§8Configuration Bordure",
			/*180*/	"§8Sauvegarder Configuration",
					"Configuration DeathLoot",
					"Configuration Loot de Départ",
					"Tout mettre à zéro",
					"§6Plugin réalisé par ",
					esthetique("§m", "§6","Une mise à jour du plugin est disponible"),
					esthetique("§6§m", "§4","Signalez les bugs en cliquant ici"),
					"§e était ",
					"§e il a volé un(e) ",
					"Vous n'êtes pas ",
		/*190*/		"Nombre de paramètres requis en entrée ",
					esthetique("§m", "§e","Aucun joueur n'a été voté plus d'une fois"),
					esthetique("§6§m", "§e","En raison du trop faible nombre de joueur par rapport au nombre de couples la polygamie est activée"),
					" a redémarré sa Box",
					" rejoint le Village",
					" quitte le Village",
					" n'a plus d'électricité dans son quartier",
					esthetique("§m", "§e","Il fait jour dans 30 secondes"),
					esthetique("§m", "§e","Validez le Stuff"),
					esthetique("§m", "§e","Stuff Actualisé"),
		/*200*/		"Cliquez pour configurer le Stuff ",
					"§6Neutre",
					esthetique("§m", "§e","Vous venez d'être téléporté par un trublion")
					
			}};
			
	
	public TextLG() {	
		
	}

	
public void getTexttranslate(MainLG main, String filename) {
		
		
		TextLG textload=main.texte;
		File filetext = new File(main.getDataFolder(), filename);
		
		if(filetext.exists()) {
			
			textload =main.serialize.deserializeText(main.filelg.loadContent(filetext));
			
			for(int i=0;i<textload.getDefault()[0].length;i++) {
				if(i<this.getDefault()[0].length) {
					if(!textload.getDefault()[0][i].equals("")){
						this.getDefault()[0][i]=textload.getDefault()[0][i];
					}
				}
			}	
			for(int i=0;i<textload.getDefault()[1].length;i++) {
				if(i<this.getDefault()[1].length) {
					if(!textload.getDefault()[1][i].equals("")){
						this.getDefault()[1][i]=textload.getDefault()[1][i];
					}
				}
			}	
			for(int i=0;i<textload.getDefault()[2].length;i++) {
				if(i<this.getDefault()[2].length) {
					if(!textload.getDefault()[2][i].equals("")){
						this.getDefault()[2][i]=textload.getDefault()[2][i];
					}
				}
			}	
		}
		
		
		for(RoleLG role:RoleLG.values()) {
	
			if(textload.translaterole.containsKey(role)) {
				this.translaterole.put(role,textload.translaterole.get(role));
			}
			else {
				this.translaterole.put(role,role.getAppearance());
			}
			if(textload.description.containsKey(role)) {
				this.description.put(role,textload.description.get(role));
			}
			else {
				this.description.put(role,role.getDescription());
			}
			if(textload.poweruse.containsKey(role)) {
				this.poweruse.put(role,textload.poweruse.get(role));
			}
			else {
				this.poweruse.put(role,role.getPowerUse());
			}
			if(textload.powerhasbeenuse.containsKey(role)) {
				this.powerhasbeenuse.put(role,textload.powerhasbeenuse.get(role));
			}
			else {
				this.powerhasbeenuse.put(role,role.getPowerHasBeenUse());
			}
		}
		
		for(ToolLG tool:ToolLG.values()) {
			if(textload.translatetool.containsKey(tool)) {
				this.translatetool.put(tool,textload.translatetool.get(tool));
			}
			else {
				this.translatetool.put(tool,tool.getAppearance());
			}
		}
		
		for(TimerLG timer:TimerLG.values()) {
			if(textload.translatetimer.containsKey(timer)) {
				this.translatetimer.put(timer,textload.translatetimer.get(timer));
			}
			else {
				this.translatetimer.put(timer,timer.getAppearance());
			}
		}	
		
		for(BordureLG bordure:BordureLG.values()) {
			
			if(textload.translatebordure.containsKey(bordure)) {
				this.translatebordure.put(bordure,textload.translatebordure.get(bordure));
			}
			else {
				this.translatebordure.put(bordure,bordure.getAppearance());
			}
		}	
		
		
		main.filelg.save(filetext, main.serialize.serializeText(main.texte));
	}
	
	public String getText(int i) {
		return (this.DEFAULT[2][i]);
	}
	
	public String[] getScoreBoard(int i) {
		return (this.DEFAULT[i]);
	}
	
	public String[][] getDefault() {
		return (this.DEFAULT);
	}
	
	public String esthetique(String colortrait, String colorentete, String message) {
		StringBuilder vide= new StringBuilder();
		message=colorentete+"§l[LG UHC]§r "+message;
		int taille=64-message.length();
		
		for(int i=0;i<taille/2;i++) {
			vide.append(" ");
		}
		message=vide+message;
		
		return(colortrait+"§l-+-----------------------------------------+-\n§f"+message+"\n§r"+colortrait+"§l-+-----------------------------------------+-");
	}
}
