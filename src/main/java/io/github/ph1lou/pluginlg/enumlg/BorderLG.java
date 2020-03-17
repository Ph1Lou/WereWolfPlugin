package io.github.ph1lou.pluginlg.enumlg;

public enum BorderLG {
	BORDER_MAX(2000,"Taille Bordure Actuelle (§3%s§r)"),
	BORDER_MIN(300,"Taille Bordure Finale (§3%s§r)");

	private final int value;
	private final String appearance;
	
	BorderLG(int value, String appearance) {
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