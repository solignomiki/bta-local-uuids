package solignomiki.localuuids.dbmanagers;

import net.minecraft.core.util.helper.UUIDHelper;
import solignomiki.localuuids.LocalUUIDs;
import solignomiki.localuuids.db.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseManager implements DatabaseManager {
	private final static Connection SQLiteConnection = SQLite.getConnect(
		LocalUUIDs.CONFIG.getString("DBPath"),
		LocalUUIDs.CONFIG.getString("DBFilename")
	);

	public SQLiteDatabaseManager() {
		try (Statement statement = SQLiteConnection.createStatement()) {
			statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS players (" +
					"	username TEXT PRIMARY KEY," +
					"	uuid TEXT" +
					")"
			);
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while initializing DB structure!");
			try {
				SQLiteConnection.rollback();
				throw new RuntimeException(e.getMessage());
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}
	}

	public String findPlayerUUID(String username) {
		String uuid = null;
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"SELECT uuid FROM players WHERE username = ?"
		)) {
			preparedStatement.setString(1, username);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				if (rs.next()) {
					uuid = rs.getString("uuid");
				}
			}
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while finding player's UUID!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}
		return uuid;
	}

	public List<String> findPlayerUsernames(String uuid) {
		List<String> usernames = new ArrayList<>();
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"SELECT username FROM players WHERE uuid = ?"
		)) {
			preparedStatement.setString(1, uuid);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					usernames.add(rs.getString("username"));
				}
			}
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while finding player's username!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}

		return usernames;
	}

	public void putPlayer(String username, String uuid) {
		if (!UUIDHelper.isUUID(uuid)) {
			throw new IllegalArgumentException();
		}

		boolean found = false;
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"SELECT uuid FROM players WHERE username = ?"
		)) {
			preparedStatement.setString(1, username);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				if (rs.next()) {
					found = true;
				}
			}
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while finding player to put!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}

		if (!found) {
			try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
				"INSERT INTO players(username, uuid) VALUES(?, ?)",
				Statement.RETURN_GENERATED_KEYS
			)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, uuid);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				LocalUUIDs.LOGGER.error("SQL exception erupted while putting player in BD!");
				try {
					SQLiteConnection.rollback();
				} catch (SQLException ex) {
					LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
				}
			}
		} else {
			try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
				"UPDATE players SET uuid = ? WHERE username = ?"
			)) {
				preparedStatement.setString(1, uuid);
				preparedStatement.setString(2, username);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				LocalUUIDs.LOGGER.error("SQL exception erupted while updating player in BD!");
				try {
					SQLiteConnection.rollback();
				} catch (SQLException ex) {
					LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
				}
			}
		}
		try {
			SQLiteConnection.commit();
		} catch (SQLException exception) {
			LocalUUIDs.LOGGER.error("Commit putting failed: {}", exception.getMessage());
		}
	}

	public void removePlayerFromDatabase(String username) {
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"DELETE FROM players WHERE username = ?"
		)) {
			preparedStatement.setString(1, username);
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while removing player in BD!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}
	}
}
