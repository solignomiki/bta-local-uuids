package solignomiki.localuuids.db;

import com.google.common.io.Files;
import com.google.gson.Gson;
import solignomiki.localuuids.LocalUUIDs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
	private final File dbFile;
	public static final Gson GSON = new Gson();
	private UUIDMap UUIDMap = new UUIDMap();

	public DatabaseManager(String path, String filename){
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
			if (UUIDMap == null) {
				UUIDMap = new UUIDMap();
			}
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

	public void addPlayer(String username, String uuid) {
		UUIDMap.add(username, uuid);
	}

	public void removePlayerFromDatabase(String username) {
		UUIDMap.remove(username);
	}

	public class UUIDMap {
		private Map<String, String> players = new HashMap<>();

		public void add(String username, String uuid) {
			players.put(username, uuid);
		}

		public void remove(String username) {
			players.remove(username);
		}

		public String getUuid(String username) {
			return players.get(username);
		}

		public String getUsername(String uuid) {
			for (Map.Entry<String, String> entry : players.entrySet()) {
				if (entry.getValue().equals(uuid)) {
					return entry.getKey();
				}
			}
			return null;
		}
	}
}
