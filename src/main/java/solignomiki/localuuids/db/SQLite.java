package solignomiki.localuuids.db;

import solignomiki.localuuids.LocalUUIDs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {
	private static Connection connection = null;

	private static String url;

	private SQLite(String path, String filename) {
		url = "jdbc:sqlite:" + path + filename + ".db";
		try
		{
			connection = DriverManager.getConnection(url);
			connection.setAutoCommit(false);
			LocalUUIDs.LOGGER.info("SQLite connection successful");
			LocalUUIDs.LOGGER.info("Loaded db: {}", url);

		} catch (SQLException e)
		{
			LocalUUIDs.LOGGER.info("Failed connection to SQLite db");
		}
	}

	public static Connection getConnect(String path, String filename)
	{
		if (connection == null)
		{
			new SQLite(path, filename);
		}

		return connection;
	}
}
