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
			"SELECT uuid FROM players WHERE username = ? COLLATE NOCASE"
		)) {
			preparedStatement.setString(1, username.toLowerCase());
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
			"SELECT username FROM players WHERE uuid = ? COLLATE NOCASE"
		)) {
			preparedStatement.setString(1, uuid.toLowerCase());
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					usernames.add(rs.getString("username").toLowerCase());
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
			"SELECT uuid FROM players WHERE username = ? COLLATE NOCASE"
		)) {
			preparedStatement.setString(1, username.toLowerCase());
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
				preparedStatement.setString(1, username.toLowerCase());
				preparedStatement.setString(2, uuid.toLowerCase());
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
				"UPDATE players SET uuid = ? WHERE username = ? COLLATE NOCASE"
			)) {
				preparedStatement.setString(1, uuid.toLowerCase());
				preparedStatement.setString(2, username.toLowerCase());
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

	public void removePlayerFromDatabaseByUsername(String username) {
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"DELETE FROM players WHERE username = ? COLLATE NOCASE"
		)) {
			preparedStatement.setString(1, username.toLowerCase());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected < 1) {
				throw new IllegalArgumentException();
			}
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while removing player by username in BD!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}
	}

	public void removePlayerFromDatabaseByUUID(String uuid) {
		try (PreparedStatement preparedStatement = SQLiteConnection.prepareStatement(
			"DELETE FROM players WHERE uuid = ? COLLATE NOCASE"
		)) {
			preparedStatement.setString(1, uuid.toLowerCase());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected < 1) {
				throw new IllegalArgumentException();
			}
			SQLiteConnection.commit();
		} catch (SQLException e) {
			LocalUUIDs.LOGGER.error("SQL exception erupted while removing player by uuid in BD!");
			try {
				SQLiteConnection.rollback();
			} catch (SQLException ex) {
				LocalUUIDs.LOGGER.error("Rollback failed: {}", ex.getMessage());
			}
		}
	}
}
