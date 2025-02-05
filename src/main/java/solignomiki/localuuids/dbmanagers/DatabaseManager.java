package solignomiki.localuuids.dbmanagers;

import net.minecraft.core.util.helper.UUIDHelper;

public interface DatabaseManager {
	String findPlayerUUID(String username);

	void putPlayer(String username, String uuid);

	void removePlayerFromDatabase(String username);
}
