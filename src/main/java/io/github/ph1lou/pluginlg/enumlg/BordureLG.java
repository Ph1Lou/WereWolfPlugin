package io.github.ph1lou.pluginlg.enumlg;

public enum BordureLG {
	borduremax(2000,"Taille Bordure Initial"),borduremin(300,"Taille Bordure Finale");

	private int value;
	private String appearance;
	
	BordureLG(int value, String appearance) {
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