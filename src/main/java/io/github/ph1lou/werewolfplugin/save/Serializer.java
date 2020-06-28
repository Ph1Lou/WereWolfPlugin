package io.github.ph1lou.werewolfplugin.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;

public class Serializer {


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

	public static Config deserialize(String json) {
		return gson().fromJson(json, Config.class);
	}

}
