package io.github.ph1lou.pluginlg.savelg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ph1lou.pluginlg.TextLG;

public class SerializerLG {



	private Gson gson;

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

	public String serializetexte(TextLG config) {
		return this.gson.toJson(config);
	}


	public ConfigLG deserialize(String json) {
		return this.gson.fromJson(json, ConfigLG.class);
	}

	public TextLG deserializetext(String json) {
		return this.gson.fromJson(json, TextLG.class);
	}


}
