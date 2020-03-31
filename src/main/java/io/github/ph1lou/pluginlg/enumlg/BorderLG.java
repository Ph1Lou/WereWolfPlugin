package io.github.ph1lou.pluginlg.enumlg;

public enum BorderLG {
	BORDER_MAX(2000),
	BORDER_MIN(300);

	private final int value;

	BorderLG(int value) {
		this.value=value;

	}
	public int getValue() {
		return this.value;
	}


}