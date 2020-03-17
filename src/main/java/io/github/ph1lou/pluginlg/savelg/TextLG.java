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
			
			
			{"§3§m------↠§8infos§3§m↞------",
					"En attente de joueurs ",
					"§3§m-------------------",
					"Joueurs : §b%d/%d",
					"§3§m------↠§8host§3§m↞--------",
					"§b%s"},
			{"§3§m----↠§8role§3§m↞-----",
			"Role §b%s",
			"§3§m----↠§8infos§3§m↞----",
			"Timer §b%s",
			"Jour §b%d",
			"Survivants §b%d",
			"Groupe de §b%d",
			"§3§m---↠§8bordure§3§m↞---",
					"Bordure §b%s",
					"Taille §b%d",
			"§3§m----↠§8host§3§m↞-----",
			"§b%s"},
			{
					"",
					esthetique("§m", "§6","Bienvenue dans cette partie de Loup Garou"),
					"Le plugin LG UHC est à jour",
					esthetique("§m", "§6","Victoire %s"),
					"Equipe %s",
					"§cDe la Mort",
					esthetique("§m", "§6","PVP activé"),
					esthetique("§m", "§6","La bordure commence à se déplacer"),
					esthetique("§m", "§6","Minage Désactivé"),
					esthetique("§m", "§6","En raison du nombre de joueurs, les Votes sont désactivés"),
			/*10*/	"§e%s était %s",
					"",
					"Pas assez de personnes pour former un couple",
					"Vous ne pouvez plus utiliser votre pouvoir aujourd'hui",
					"C'est la nuit, enlevez votre armure pour devenir invisible",
					"Victoire %s",
					esthetique("§m", "§6","C'est le Jour"),
					esthetique("§2§m", "§6","C'est l'heure de voter §6/lg vote§r Vous avez %s"),
					"Vous redevenez visible",
					"Vous n'êtes plus maudit",
			/*20*/	"Vous n'avez plus la salvation",
					"Votre monde doit s'appeler world",
					"§2Liste des Soeurs : %s",
					"§2Liste des Siamois: %s",
					"§3§m------↠§8compo§3§m↞------",
					"§3§m-----↠§8page %d/%d§3§m↞-----",
					"§3§m------↠§8score§3§m↞------",
					"",
					esthetique("§6§m", "§4","%s est mort, il était %s"),
					esthetique("§6§m", "§4","%s est mort"),
			/*30*/	"§d%s par amour, le rejoins dans sa tombe",
					"Vous venez de ressusciter",
					"Un Trublion vient de mourir, tous les joueurs sont téléportés aléatoirement",
					"",
					"",
					"",
					"Avant de mourir, la Voyante a fait apparaitre %s caches sur la map, une seule renferme le nom d'un Ennemi du Village",
					"",
					"Le jeu a déjà commencé",
					"Progression %s/100",
			/*40*/	"§2%s n'est pas dans le camp des Loups Garous",
					"§4%s est dans le camp des Loups Garous",
					"",
					"Pour (re)voir la description §6/lg role",
					"",
					"Mort",
					"Spectateur",
					"",
					"",
					"",
			/*50*/	esthetique("§m", "§6","un nouveau joueur a rejoint la meute §6/lg lg"),
					"Vous passez dans le camp des §4Loups Garous §6/lg lg",
					"Vous distinguez maintenant les §4Loups Garous §6/lg lg",
					"La Composition est cachée",
					"La commande prend un joueur en entrée",
					"Vous venez d'être maudit par le corbeau. Vous avez Jump Boost",
					"",
					"",
					"",
					"",
			/*60*/	"",
					"Le Salvateur vous protège pour la journée, vous avez Résistance et Nofall",
					"",
					"",
					"",
					"",
					"",
					"Vous n'êtes pas dans la partie",
					"La partie est déjà finie ou les rôles n'ont pas encore été annoncés",
					"",
			/*70*/	"",
					"§2%s et %s sont dans le même camp",
					"§4%s et %s ne sont pas dans le même camp",
					"",
					"",
					"",
					"§8Configuration Scenarios",
					"§8Configuration Stuff",
					"§7Centre §3%d §7à§3 %d §7blocs | Y §3%d",
					"",
			/*80*/	"§2ON",
					"§2PVE",
					"Bon toutou",
					"Charger Stuff Role normal",
					"Charger Stuff MeetUp",
					"Charger Stuff Depart Chill",
					"Clear le Stuff de Départ et de Mort",
					"Clique-Gauche>>\nClique-Droit>>-\nShift-Clique>>Stuff",
					"%s (§3%s§r)",
					"",
			/*90*/	"",
					"\n§6§lPouvoir : %b",
					"%s est %s",
					"Vous venez d'être téléporté par un administrateur",
					esthetique("§m", "§2","Un Citoyen a annulé le vote"),
					"§6Dépouillement ",
					"%s a voté pour %s",
					"Vous n'êtes pas Vivant",
					"Vous n'êtes pas un Loup Garou",
					"La visualisation de la liste des Loups Garous n'est pas activée",
			/*100*/	"La liste des Loups Garous n'est pas encore dévoilée",
					"§4Liste des Loups Garous :§r%s",
					"§6/lg timer §9Pour voir les timers\n§6/lg regles §9Pour voir les règles\n§6/lg role §9Pour voir votre rôle\n§6/lg scenarios §9Pour voir les scenarios\n§6/lg compo §9Pour voir la compo",
					"Votre pouvoir ne peut pas encore être utilisé ou a déja été utilisé",
					"Vous devez sélectionner deux joueurs différents",
					"Vous ne pouvez pas vous choisir vous même",
					"Au moins un joueur n'existe pas, s'est déconnecté ou est déjà mort",
					"Le joueur a déja été affecté par votre pouvoir au dernier tour",
					"Le joueur n'est pas dans le couloir de la mort",
					"Le joueur ne peut pas (plus) être infecté",
			/*110*/	"",
					"Vous devez être à moins de 20 blocs de votre cible",
					"Vous n'avez plus assez de vie pour espionner un joueur",
					"Vous venez d'espionner un membre du Village vous perdez 3 coeurs jusqu'au prochain matin",
					"Au moins un de ces joueurs a déjà été enquêté",
					"",
					"Seul un Administrateur à accès à cette commande",
					"",
					"Host Configuré",
					"La partie n'est pas finie",
			/*120*/	"Les effectifs ne sont pas suffisant pour lancer la partie avec cette configuration.",
					"Plus d'infos sur la partie §6/lg h",
					"Le Chat est ON",
					"Le Chat est OFF",
					esthetique("§m", "§e","C'est la Nuit"),
					"§4§lLOUP GAROU UHC",
					"Vous ne pouvez pas changer les rôles une fois qu'ils sont annoncés",
					esthetique("§m", "§e","Cliquez ici pour valider le stuff de départ"),
					esthetique("§m", "§e","Cliquez ici pour valider le loot à la mort"),
					"Vous devenez invisible, remettez votre armure pour redevenir visible",
			/*130*/"En attente d'une potentielle resurrection",
					"Commande désactivée",
					"Joueur inconnu",
					"§6[Message de %s] %s",
					"§2[Message envoyé à %s] ---> %s",
					"",
					"§b[Info] ",
					esthetique("§6§m", "§e","Respecter les limites, groupe de %d"),
					"§6Respectez la limite",
					"§4groupe de %s",
			/*140*/	"Cette commande n'est pas accessible via la console",
					"Le joueur est déjà mort",
					"Le joueur est en ligne",
					"Enlevez un rôle dans la config pour compenser le joueur si besoin est",
					"Les rôles n'ont pas encore été attribué",
					"Vous ne pouvez pas voir le rôle du joueur si vous êtes encore dans la partie",
					"§6En Couple Avec : %s",
					"§6Affecte : %s",
					"§6Tué par : %s",
					"Le joueur n'est pas Mort",
			/*150*/	"Final Heal !",
					"Stuff de départ actualisé",
					"Stuff de mort actualisé",
					"§6/adminlg config §9Pour configurer la partie\n§6/adminlg host §9Pour configurer l'host\n§6/adminlg start §9Pour lancer la partie\n§6/adminlg chat §9Pour activer/désactiver le chat\n§6/adminlg info §9Pour parler à tous les joueurs\n§6/adminlg groupe §9Pour faire respecter les groupes\n§6/adminlg fh §9Pour FinalHeal\n§6/adminlg inv §9Pour voir l'inventaire d'un joueur\n§6/adminlg killa §9Pour tuer un joueur offline\n§6/adminlg revive §9Pour ressusciter un joueur\n§6/adminlg role §9Pour voir le rôle d'un joueur\n§6/adminlg deco §9Pour voir les joueurs déco\n§6/adminlg setgroupe§9 Pour configurer les groupes\n§6/adminlg tpgroupe§9 Pour téléporter un groupes",
					esthetique("§6§m", "§e","%s a été ressuscité par un Administrateur"),
					"Les morts ne peuvent pas voter",
					"Les Votes ne sont pas encore activé",
					"Les Votes sont désactivés",
					"Ce n'est plus l'heure du vote",
					"Vous avez déjà voté aujourd'hui",
			/*160*/	"",
					"Ce joueur a déjà été voté",
					"Vous avez voté pour %s",
					esthetique("§6§m", "§e","%s est celui qui a recueillit §9le plus de vote§r : §e%s§r. Il perd 5 coeurs"),
					"",
					"Toutes les caches ont été trouvées",
					"Les groupes passent à §6%s",
					"%s s'est déconnecté il y a %s",
					"§4Désactiver §r%s",
					"§2Activer §r%s",
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
					"§7Plugin réalisé par §3%s",
					esthetique("§m", "§6","Une mise à jour du plugin est disponible"),
					esthetique("§6§m", "§4","Signalez les bugs en cliquant ici"),
					"§m§l%s§e était %s",
					"§e il a volé un(e) %s",
					"Vous n'êtes pas %s",
		/*190*/		"Nombre de paramètres requis en entrée %d",
					"Aucun joueur n'a été voté plus d'une fois",
					"En raison du trop faible nombre de joueur par rapport au nombre de couples la polygamie est activée",
					"§6§l%s a redémarré sa Box",
					"§a§l[%d/%d] %s rejoint le Village",
					"§c§l[%d/%d] %s quitte le Village",
					"§4§l%s n'a plus d'électricité dans son quartier",
					"Il fait jour dans 30 secondes",
					esthetique("§m", "§e","Validez le Stuff"),
					"Stuff Actualisé",
		/*200*/		"Cliquez pour configurer le Stuff ",
					"§6Neutre",
					"Vous venez d'être téléporté par un trublion"
					
			}};


    public TextLG() {
		
	}

	
public void getTextTranslate(MainLG main, String filename) {
		
		
		TextLG text_load=main.text;
		File file_text = new File(main.getDataFolder(), filename);
		
		if(file_text.exists()) {
			
			text_load =main.serialize.deserializeText(main.filelg.loadContent(file_text));
			
			for(int i=0;i<text_load.getDefault()[0].length;i++) {
				if(i<this.getDefault()[0].length) {
					if(text_load.getDefault()[0][i].length()!=0){
						this.getDefault()[0][i]=text_load.getDefault()[0][i];
					}
				}
			}	
			for(int i=0;i<text_load.getDefault()[1].length;i++) {
				if(i<this.getDefault()[1].length) {
					if(text_load.getDefault()[1][i].length()!=0){
						this.getDefault()[1][i]=text_load.getDefault()[1][i];
					}
				}
			}	
			for(int i=0;i<text_load.getDefault()[2].length;i++) {
				if(i<this.getDefault()[2].length) {
					if(text_load.getDefault()[2][i].length()!=0){
						this.getDefault()[2][i]=text_load.getDefault()[2][i];
					}
				}
			}	
		}
		for(RoleLG role:RoleLG.values()) {
			this.translaterole.put(role,text_load.translaterole.getOrDefault(role,role.getAppearance()));
			this.description.put(role,text_load.description.getOrDefault(role,role.getDescription()));
			this.poweruse.put(role,text_load.poweruse.getOrDefault(role,role.getPowerUse()));
			this.powerhasbeenuse.put(role,text_load.powerhasbeenuse.getOrDefault(role,role.getPowerHasBeenUse()));
		}
		for(ToolLG tool:ToolLG.values()) {
			this.translatetool.put(tool,text_load.translatetool.getOrDefault(tool,tool.getAppearance()));
		}
		for(TimerLG timer:TimerLG.values()) {
			this.translatetimer.put(timer,text_load.translatetimer.getOrDefault(timer,timer.getAppearance()));
		}
		for(BorderLG border: BorderLG.values()) {
			this.translatebordure.put(border,text_load.translatebordure.getOrDefault(border,border.getAppearance()));
		}
		for(ScenarioLG scenario:ScenarioLG.values()) {
			this.translatescenario.put(scenario,text_load.translatescenario.getOrDefault(scenario,scenario.getAppearance()));
		}

	main.filelg.save(file_text, main.serialize.serializeText(main.text));
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
