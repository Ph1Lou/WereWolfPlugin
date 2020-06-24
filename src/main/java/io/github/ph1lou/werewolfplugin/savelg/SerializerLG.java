package io.github.ph1lou.werewolfplugin.savelg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ph1lou.pluginlgapi.ConfigWereWolfAPI;

public class SerializerLG {


	public static Gson gson(){
		return new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}

    public static String serialize(ConfigWereWolfAPI config) {
		return gson().toJson(config);
	}

	public static ConfigLG deserialize(String json) {
		return gson().fromJson(json, ConfigLG.class);
	}

}
