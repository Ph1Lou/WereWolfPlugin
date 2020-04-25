package io.github.ph1lou.pluginlg.savelg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ph1lou.pluginlg.game.PlayerLG;

import java.util.Map;

public class SerializerLG {


	public static Gson gson(){
		return new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}

    public static String serialize(ConfigLG config) {
		return gson().toJson(config);
	}
	public static String serialize(TextLG config) {
		return gson().toJson(config);
	}
	public static String serialize(Map<String, PlayerLG> plg) {return gson().toJson(plg);}

	public static ConfigLG deserialize(String json) {
		return gson().fromJson(json, ConfigLG.class);
	}

	public static TextLG deserializeText(String json) {
		return gson().fromJson(json, TextLG.class);
	}


}
