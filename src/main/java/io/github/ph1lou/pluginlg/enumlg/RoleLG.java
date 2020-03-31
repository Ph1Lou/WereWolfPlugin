package io.github.ph1lou.pluginlg.enumlg;


public enum RoleLG {
	
	COUPLE(null,true),
	CUPIDON( Camp.VILLAGE,true),
	LOUP_GAROU(Camp.LG,true),
	INFECT(Camp.LG,true),
	LOUP_FEUTRE(Camp.LG,true),
	LOUP_PERFIDE(Camp.LG,true),
	VILAIN_PETIT_LOUP(Camp.LG,true),
	VOLEUR(Camp.NEUTRAL,true),
	ENFANT_SAUVAGE(Camp.VILLAGE,true),
	SORCIERE(Camp.VILLAGE,true),
	PETITE_FILLE(Camp.VILLAGE,true),
	SALVATEUR(Camp.VILLAGE,false),
	RENARD(Camp.VILLAGE,false),
	MONTREUR_OURS(Camp.VILLAGE,true),
	VOYANTE_BAVARDE(Camp.VILLAGE,false),
	VOYANTE(Camp.VILLAGE,false),
	TRUBLION(Camp.VILLAGE,true),
	SOEUR(Camp.VILLAGE,true),
	ANCIEN(Camp.VILLAGE,true),
	CORBEAU(Camp.VILLAGE,false),
	VILLAGEOIS(Camp.VILLAGE,true),
	DETECTIVE(Camp.VILLAGE,false),
	MINEUR(Camp.VILLAGE,true),
	CITOYEN(Camp.VILLAGE,true),
	FRERE_SIAMOIS(Camp.VILLAGE,true),
	ANGE(Camp.NEUTRAL,true),
	ANGE_GARDIEN(Camp.NEUTRAL,true),
	ANGE_DECHU(Camp.NEUTRAL,true),
	LOUP_AMNESIQUE(Camp.NEUTRAL,true),
	LOUP_GAROU_BLANC(Camp.NEUTRAL,true),
	ASSASSIN(Camp.NEUTRAL,true),
	TUEUR_EN_SERIE(Camp.NEUTRAL,true);

	private final Camp camp;
	private final Boolean power;
	
	RoleLG(Camp camp, Boolean power) {
		this.camp=camp;
		this.power=power;
	}
	

	public Camp getCamp() {
		return this.camp;
	}


	public Boolean getPower() {
		return this.power;
	}
	
}
