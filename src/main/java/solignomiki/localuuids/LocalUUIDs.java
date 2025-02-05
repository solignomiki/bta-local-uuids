package solignomiki.localuuids;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solignomiki.localuuids.commands.GetUUIDByUsernameCommand;
import solignomiki.localuuids.commands.TieUsernameToUUIDCommand;
import solignomiki.localuuids.dbmanagers.DatabaseManager;
import solignomiki.localuuids.dbmanagers.JsonDatabaseManager;
import solignomiki.localuuids.dbmanagers.SQLiteDatabaseManager;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class LocalUUIDs implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "localuuids";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DatabaseManager DB_MANAGER;
	public static final TomlConfigHandler CONFIG;
	public static final DB DB_IN_USE;

	public enum DB {
		SQLITE,
		JSON
	}

	static {
		Toml toml = new Toml();
		toml.addEntry("DB", "Can be JSON or SQLITE", "SQLITE");
		toml.addEntry("DBPath","./db/");
		toml.addEntry("DBFilename", "uuids");

		CONFIG = new TomlConfigHandler(MOD_ID, toml);
		String usedDb = CONFIG.getString("DB");
		switch (DB.valueOf(usedDb)) {
			case SQLITE:
				DB_IN_USE = DB.SQLITE;
				break;
			case JSON:
				DB_IN_USE = DB.JSON;
				break;
			default:
				LOGGER.error("WTF is this DB?");
				throw new IllegalArgumentException("WTF is this DB?");
		}
		switch (DB_IN_USE) {
			case SQLITE:
				DB_MANAGER = new SQLiteDatabaseManager();
				break;
			case JSON:
				DB_MANAGER = new JsonDatabaseManager(CONFIG.getString("DBPath"), CONFIG.getString("DBFilename"));
				break;
			default:
				DB_MANAGER = new SQLiteDatabaseManager();
				break;
		}
	}


    @Override
    public void onInitialize() {
		LOGGER.info("LocalUUIDs initialization");
		CommandManager.registerServerCommand(new GetUUIDByUsernameCommand());
		CommandManager.registerServerCommand(new TieUsernameToUUIDCommand());
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
