package me.chaounne.coland.db;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLManager {

    private static Connection connection;

    public static void connect(FileConfiguration config) throws SQLException {
        try {
            // Charger le driver MySQL relocalisé
            Class.forName("me.chaounne.coland.libs.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Le driver MySQL est introuvable dans le JAR.", e);
        }

        // Récupérer les informations depuis la config
        String host = config.getString("database.host", "localhost");
        int port = config.getInt("database.port", 3306);
        String database = config.getString("database.database", "minecraft");
        String username = config.getString("database.username", "root");
        String password = config.getString("database.password", "");
        String sslMode = config.getString("database.ssl-mode", "DISABLED");
        String timezone = config.getString("database.timezone", "UTC");

        // URL pour le driver MySQL
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?sslMode=" + sslMode +
                "&serverTimezone=" + timezone +
                "&allowPublicKeyRetrieval=true";

        connection = DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public static void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}