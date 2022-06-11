package common;

import interfaces.IDatabase;
import util.LoggerUtil;

import java.sql.*;

public class PostgreSQLDatabase implements IDatabase {
    private static final String URL = "jdbc:postgresql://%s:%d/%s" +
            "?autoReconnect=true" +
            "&useSSL=false" +
            "&useUnicode=true" +
            "&characterEncoding=utf-8";

    private final String host;
    private final int port;
    private final String database, username, password;
    private Connection connection;

    public PostgreSQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(String.format(URL, host, port, database),
                    username, password);
            LoggerUtil.positive("Соединение с базой данных успешно установлено.");
        } catch (SQLException ex) {
            LoggerUtil.negative("Соединение с базой данных не было установлено: отключение.");
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet result;
        Statement statement = this.connection.createStatement();
        if (query.contains("SELECT")) {
            result = statement.executeQuery(query);
        } else {
            statement.executeUpdate(query);
            result = statement.getGeneratedKeys();
        }
        return result;
    }

    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            LoggerUtil.info("Соединение с базой данных закрыто");
        } catch (SQLException ex) {
        }
    }

}
