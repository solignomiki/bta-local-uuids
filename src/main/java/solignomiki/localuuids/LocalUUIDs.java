package solignomiki.localuuids;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solignomiki.localuuids.db.DatabaseManager;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.Properties;


public class LocalUUIDs implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "localuuids";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DatabaseManager DB_MANAGER;
	public static final ConfigHandler CONFIG;

	static {
		Properties props = new Properties();
		props.setProperty("DBPath","./db/");
		props.setProperty("DBFilename", "uuids");
		CONFIG = new ConfigHandler(MOD_ID, props);
		DB_MANAGER = new DatabaseManager(CONFIG.getString("DBPath"), CONFIG.getString("DBFilename"));
	}

    @Override
    public void onInitialize() {
        LOGGER.info("LocalUUIDs initialized.");
    }

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}


}
