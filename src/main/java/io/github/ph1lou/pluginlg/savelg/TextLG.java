package io.github.ph1lou.pluginlg.savelg;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class TextLG {
	
	public final Map<RoleLG, String> translaterole = new HashMap<>();
	public final Map<RoleLG, String> description = new HashMap<>();
	public final Map<RoleLG, String> poweruse = new HashMap<>();
	public final Map<RoleLG, String> powerhasbeenuse = new HashMap<>();
	public final Map<ToolLG, String> translatetool = new HashMap<>();
	public final Map<TimerLG, String> translatetimer = new HashMap<>();
	public final Map<BorderLG, String> translatebordure = new HashMap<>();
	public final Map<ScenarioLG, String> translatescenario = new HashMap<>();

	public final String[][] DEFAULT= {
			
			
			{"§3§m-------§8infos§3§m-------","En attente de joueurs ","§3§m-------------------","Joueurs : §b%d/%d","§3§m-------§8host§3§m---------","§b%s"},
			{"§3§m-----§8role§3§m------",
			"Role §b%s",
			"§3§m-----§8infos§3§m-----",
			"Timer §b%s",
			"Jour §b%d",
			"Survivants §b%d",
			"Groupe de §b%d",
			"§3§m----§8bordure§3§m----",
			"Bordure §b%s",
			"§3§m-----§8host§3§m------",
			"§b%s"},
			{
					"§4§l[LG UHC] §6Loup Garou UHC",
					esthetique("§m", "§6","Bienvenue dans cette partie de Loup Garou"),
					esthetique("§m", "§2","Le plugin est à jour"),
					esthetique("§m", "§6","Victoire %s"),
					"Equipe %s",
					"§cDe la Mort",
					esthetique("§m", "§6","PVP activé"),
					esthetique("§m", "§6","La bordure commence à se déplacer"),
					esthetique("§m", "§6","Minage Désactivé"),
					esthetique("§m", "§6","En raison du nombre de joueurs, les Votes sont désactivés"),
			/*10*/	"§e%s était %s",
					esthetique("§6§m", "§e","Ours : %s"),
					esthetique("§6§m", "§4","Pas assez de personnes pour former un couple"),
					esthetique("§6§m", "§4","Vous ne pouvez plus utiliser votre pouvoir aujourd'hui"),
					esthetique("§2§m", "§6","C'est la nuit, enlevez votre armure pour devenir invisible"),
					"Victoire",
					esthetique("§m", "§6","C'est le Jour"),
					esthetique("§2§m", "§6","C'est l'heure de voter §6/lg vote§r Vous avez %s"),
					esthetique("§6§m", "§e","Vous redevenez visible"),
					esthetique("§6§m", "§e","Vous n'êtes plus maudit"),
			/*20*/	esthetique("§6§m", "§e","Vous n'avez plus la salvation"),
					esthetique("§6§m", "§4","Votre monde doit s'appeler world"),
					"§2Liste des Soeurs : %s",
					"§2Liste des Siamois: %s",
					"",
					"",
					"",
					"",
					esthetique("§6§m", "§4","%s est mort, il était %s"),
					esthetique("§6§m", "§4","%s est mort"),
			/*30*/	esthetique("§6§m", "§4","%s par amour, le rejoins dans sa tombe"),
					esthetique("§m", "§2","Vous venez de ressusciter"),
					esthetique("§m", "§e","Un Trublion vient de mourir, tous les joueurs sont téléportés aléatoirement"),
					"",
					"",
					"",
					esthetique("§6§m", "§e","Avant de mourir, la Voyante a fait apparaitre %s caches sur la map, une seule renferme le nom d'un Ennemi du Village"),
					"",
					esthetique("§6§m", "§4","Le jeu a déjà commencé"),
					"Progression %s/100",
			/*40*/	esthetique("§6§m", "§e","§2%s n'est pas dans le camp des Loups Garous"),
					esthetique("§6§m", "§e","§4%s est dans le camp des Loups Garous"),
					"",
					"Pour (re)voir la description §6/lg role",
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
					"",
			/*70*/	"",
					esthetique("§6§m", "§e","§2%s et %s sont dans le même camp"),
					esthetique("§6§m", "§e","§4%s et %s ne sont pas dans le même camp"),
					"",
					"",
					"",
					"§8Configuration Scenarios",
					"§8Configuration Stuff",
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
					"\n§6§lPouvoir : %b",
					"%s est %s",
					esthetique("§m", "§e","Vous venez d'être téléporté par un administrateur"),
					esthetique("§m", "§2","Un Citoyen a annulé le vote"),
					"§6Dépouillement ",
					"%s a voté pour %s",
					esthetique("§6§m", "§4","Vous n'êtes pas Vivant"),
					esthetique("§6§m", "§4","Vous n'êtes pas un Loup Garou"),
					esthetique("§6§m", "§4","La liste des Loups Garous n'est pas activée"),
			/*100*/	esthetique("§6§m", "§4","La liste des Loups Garous n'est pas encore dévoilée"),
					"§4Liste des Loups Garous :§r%s",
					"§6/lg timer §9Pour voir les timers\n§6/lg regles §9Pour voir les règles\n§6/lg compo §9Pour voir la compo\n§6/lg role §9Pour voir votre rôle\n§6/lg scenarios §9Pour voir les scenarios",
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
					"§4§lLOUP GAROU UHC",
					esthetique("§6§m", "§4","Vous ne pouvez pas changer les rôles une fois qu'ils sont annoncés"),
					esthetique("§m", "§e","Cliquez ici pour valider le stuff de départ"),
					esthetique("§m", "§e","Cliquez ici pour valider le loot à la mort"),
					esthetique("§m", "§e","Vous devenez invisible, remettez votre armure pour redevenir visible"),
			/*130*/esthetique("§m", "§e","En attente d'une potentielle resurrection"),
					esthetique("§6§m", "§4","Commande désactivée"),
					esthetique("§6§m", "§4","Joueur inconnu"),
					"§6[Message de %s] %s",
					"§2[Message envoyé à %s] ---> %s",
					"",
					"§b[Info] ",
					esthetique("§6§m", "§e","Respecter les limites, groupe de %d"),
					"§6Respectez la limite",
					"§4groupe de %s",
			/*140*/	esthetique("§6§m", "§4","Cette commande n'est pas accessible via la console"),
					esthetique("§6§m", "§4","Le joueur est déjà mort"),
					esthetique("§6§m", "§4","Le joueur est en ligne"),
					esthetique("§6§m", "§4","Enlevez un rôle dans la config pour compenser le joueur si besoin est"),
					esthetique("§6§m", "§4","Les rôles n'ont pas encore été attribué"),
					esthetique("§6§m", "§4","Vous ne pouvez pas voir le rôle du joueur si vous êtes encore dans la partie"),
					"§6En Couple Avec : %s",
					"§6Affecte : %s",
					"§6Tué par : %s",
					esthetique("§6§m", "§4","Le joueur n'est pas Mort"),
			/*150*/	esthetique("§m", "§e","Final Heal !"),
					esthetique("§m", "§2","Stuff de départ actualisé"),
					esthetique("§m", "§2","Stuff de mort actualisé"),
					"§6/adminlg config §9Pour configurer la partie\n§6/adminlg host §9Pour configurer l'host\n§6/adminlg start §9Pour lancer la partie\n§6/adminlg chat §9Pour activer/désactiver le chat\n§6/adminlg info §9Pour parler à tous les joueurs\n§6/adminlg groupe §9Pour faire respecter les groupes\n§6/adminlg fh §9Pour FinalHeal\n§6/adminlg inv §9Pour voir l'inventaire d'un joueur\n§6/adminlg killa §9Pour tuer un joueur offline\n§6/adminlg revive §9Pour ressusciter un joueur\n§6/adminlg role §9Pour voir le rôle d'un joueur\n§6/adminlg deco §9Pour voir les joueurs déco\n§6/adminlg setgroupe§9 Pour configurer les groupes\n§6/adminlg tpgroupe§9 Pour téléporter un groupes",
					esthetique("§6§m", "§e","%s a été ressuscité par un Administrateur"),
					esthetique("§6§m", "§4","Les morts ne peuvent pas voter"),
					esthetique("§6§m", "§4","Les Votes ne sont pas encore activé"),
					esthetique("§6§m", "§4","Les Votes sont désactivés"),
					esthetique("§6§m", "§4","Ce n'est plus l'heure du vote"),
					esthetique("§6§m", "§4","Vous avez déjà voté aujourd'hui"),
			/*160*/	esthetique("§6§m", "§4","Joueur non valide"),
					esthetique("§6§m", "§4","Ce joueur a déjà été voté"),
					esthetique("§6§m", "§e","Vous avez voté pour %s"),
					esthetique("§6§m", "§e","%s est celui qui a recueillit §9le plus de vote§r : §e%s. Il perd 5 coeurs"),
					"",
					esthetique("§m", "§2","Toutes les caches ont été trouvées"),
					"Les groupes passent à §6%s",
					esthetique("§6§m", "§e","%s s'est déconnecté il y a %s"),
					"§4Désactiver %s",
					"§2Activer %s",
			/*170*/	"Revenir au Menu",
					"Ecraser Configuration %s",
					"Charger Configuration %s",
					"Sauvegarder dans Configuration %s",
					"Configuration %s",
					"§8Option LG",
					"§8Configuration Rôles",
					"§8Configuration Timers",
					"§8Configuration Globale",
					"§8Configuration Bordure",
			/*180*/	"§8Sauvegarder Configuration",
					"Configuration DeathLoot",
					"Configuration Loot de Départ",
					"Tout mettre à zéro",
					"§6Plugin réalisé par %s",
					esthetique("§m", "§6","Une mise à jour du plugin est disponible"),
					esthetique("§6§m", "§4","Signalez les bugs en cliquant ici"),
					"§m§l%s§e était %s",
					"§e il a volé un(e) %s",
					esthetique("§6§m", "§e","Vous n'êtes pas %s"),
		/*190*/		esthetique("§6§m", "§e","Nombre de paramètres requis en entrée %d"),
					esthetique("§m", "§e","Aucun joueur n'a été voté plus d'une fois"),
					esthetique("§6§m", "§e","En raison du trop faible nombre de joueur par rapport au nombre de couples la polygamie est activée"),
					"§6§l%s a redémarré sa Box",
					"§a§l[%d/%d] %s rejoint le Village",
					"§c§l[%d/%d] %s quitte le Village",
					"§4§l%s n'a plus d'électricité dans son quartier",
					esthetique("§m", "§e","Il fait jour dans 30 secondes"),
					esthetique("§m", "§e","Validez le Stuff"),
					esthetique("§m", "§e","Stuff Actualisé"),
		/*200*/		"Cliquez pour configurer le Stuff ",
					"§6Neutre",
					esthetique("§m", "§e","Vous venez d'être téléporté par un trublion")
					
			}};


    public TextLG() {
		
	}

	
public void getTextTranslate(MainLG main, String filename) {
		
		
		TextLG textload=main.text;
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
		
		for(BorderLG bordure: BorderLG.values()) {
			
			if(textload.translatebordure.containsKey(bordure)) {
				this.translatebordure.put(bordure,textload.translatebordure.get(bordure));
			}
			else {
				this.translatebordure.put(bordure,bordure.getAppearance());
			}
		}
		for(ScenarioLG scenario:ScenarioLG.values()) {

		if(textload.translatescenario.containsKey(scenario)) {
			this.translatescenario.put(scenario,textload.translatescenario.get(scenario));
		}
		else {
			this.translatescenario.put(scenario,scenario.getAppearance());
		}
	}

	main.filelg.save(filetext, main.serialize.serializeText(main.text));
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
	
	private String esthetique(String colortrait, String colorentete, String message) {
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
