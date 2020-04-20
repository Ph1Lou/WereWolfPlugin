package io.github.ph1lou.pluginlg.enumlg;

public enum ToolLG {

	VICTORY_NEUTRAL(false),
	VICTORY_COUPLE(true),
	CHAT(true),
	SHOW_ROLE_TO_DEATH(true),
	COMPO_VISIBLE(true),
	VOTE(true),
	LG_LIST(true),
	EVENT_SEER_DEATH(true),
	AUTO_REZ_WITCH(false),
	AUTO_REZ_INFECT(false),
	POLYGAMY(false),
	COMPASS_MIDDLE(false),
	SEER_EVERY_OTHER_DAY(false),
	RED_NAME_TAG(true),
	DON_LOVERS(false),
	AUTO_GROUP(true);

	private final Boolean value;

	ToolLG(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return this.value;
	}

}


