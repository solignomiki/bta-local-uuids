package solignomiki.localuuids.dbmanagers;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.util.helper.UUIDHelper;
import solignomiki.localuuids.LocalUUIDs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonDatabaseManager implements DatabaseManager {
	private final File dbFile;
	public static final Gson GSON = new Gson();
	private UUIDMap UUIDMap = new UUIDMap();

	public JsonDatabaseManager(String path, String filename){
		dbFile = new File(path + filename + ".json");
		reloadDb();
	}

	public void reloadDb(){
		if (!dbFile.exists()) {
			saveDb();
		}
		try {
			BufferedReader bufferedReader = Files.newReader(dbFile, StandardCharsets.UTF_8);
			UUIDMap = GSON.fromJson(bufferedReader, UUIDMap.class);
		} catch (Exception e) {
			LocalUUIDs.LOGGER.error(e.getMessage());
		}
	}

	public void saveDb() {
		try {
			if (!dbFile.exists()) {
				dbFile.getParentFile().mkdirs();
				dbFile.createNewFile();
			}
			BufferedWriter bufferedWriter = Files.newWriter(dbFile, StandardCharsets.UTF_8);
			String json = GSON.toJson(UUIDMap);
			bufferedWriter.write(json);
			bufferedWriter.close();
		} catch (Exception e) {
			LocalUUIDs.LOGGER.error(e.getMessage());
		}
	}

	public String findPlayerUUID(String username) {
		return UUIDMap.getUuid(username);
	}

	public String findPlayerUsername(String uuid) {
		return UUIDMap.getUsername(uuid);
	}

	public void putPlayer(String username, String uuid) {
		if (!UUIDHelper.isUUID(uuid)) {
			throw new IllegalArgumentException();
		}
		UUIDMap.put(username, uuid);
	}

	public void removePlayerFromDatabase(String username) {
		UUIDMap.remove(username);
	}

	public class UUIDMap {
		@SerializedName("players")
		public Map<String, String> players = new HashMap<>();

		public void put(String username, String uuid) {
			players.put(username.toLowerCase(), uuid);
		}

		public void remove(String username) {
			players.remove(username.toLowerCase());
		}

		public String getUuid(String username) {
			return players.get(username.toLowerCase());
		}

		public String getUsername(String uuid) {
			for (Map.Entry<String, String> entry : players.entrySet()) {
				if (entry.getValue().equalsIgnoreCase(uuid)) {
					return entry.getKey();
				}
			}
			return null;
		}
	}
}
