package solignomiki.localuuids.dbmanagers;

import net.minecraft.core.util.helper.UUIDHelper;

import java.util.List;

public interface DatabaseManager {
	String findPlayerUUID(String username);

	List<String> findPlayerUsernames(String uuid);

	void putPlayer(String username, String uuid);

	void removePlayerFromDatabaseByUsername(String username);

	void removePlayerFromDatabaseByUUID(String username);
}
