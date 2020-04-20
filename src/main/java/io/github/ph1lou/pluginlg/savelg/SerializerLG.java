package io.github.ph1lou.pluginlg.savelg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ph1lou.pluginlg.PlayerLG;

import java.util.Map;

public class SerializerLG {

	private final Gson gson;

	public SerializerLG() {

		this.gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}

    public String serialize(ConfigLG config) {
		return this.gson.toJson(config);
	}
	public String serialize(TextLG config) {
		return this.gson.toJson(config);
	}
	public String serialize(Map<String, PlayerLG> plg) {return this.gson.toJson(plg);}

	public ConfigLG deserialize(String json) {
		return this.gson.fromJson(json, ConfigLG.class);
	}

	public TextLG deserializeText(String json) {
		return this.gson.fromJson(json, TextLG.class);
	}


}
