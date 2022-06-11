package dataController;

import common.User;
import interfaces.IDatabase;
import interfaces.common.IDataController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDataController implements IDataController<String, User> {
    private static final String TABLE_NAME = "users",
            USERNAME_COLUMN = "username",
            PASSWORD_HASH_COLUMN = "password_hash";
    private final IDatabase database;

    public UserDataController(IDatabase database) {
        this.database = database;
        try {
            database.executeQuery("CREATE TABLE IF NOT EXISTS %s (" +
                            "%s varchar(16) PRIMARY KEY," +
                            "%s varchar(128)" +
                            ")",
                    TABLE_NAME, USERNAME_COLUMN, PASSWORD_HASH_COLUMN).close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            ResultSet results = database.executeQuery("SELECT * FROM %s", TABLE_NAME);
            while (results.next()) {
                users.add(construct(results));
            }
            results.close();
        } catch (SQLException ignored) {}
        return users;
    }

    @Override
    public User getByKey(String username) {
        try {
            ResultSet result = database.executeQuery("SELECT * FROM %s WHERE %s='%s'",
                    TABLE_NAME, USERNAME_COLUMN, username);
            result.next();
            User user = construct(result);
            result.close();
            return user;
        } catch (SQLException ex) {
            return null;
        }
    }

    @Override
    public boolean insert(User user) {
        try {
            database.executeQuery("INSERT INTO %s VALUES ('%s', '%s')",
                    TABLE_NAME, user.getName(), user.getPasswordHash()).close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        try {
            database.executeQuery("UPDATE %s SET %s='%s' WHERE %s='%s'",
                    TABLE_NAME, PASSWORD_HASH_COLUMN, user.getPasswordHash(),
                    USERNAME_COLUMN, user.getName()).close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void delete(User user) {
        try {
            database.executeQuery("DELETE FROM %s WHERE %s='%s'",
                    TABLE_NAME, USERNAME_COLUMN, user.getName()).close();
        } catch (SQLException ignored) {}
    }

    @Override
    public boolean exists(String username) {
        boolean exists;
        try {
            ResultSet result = database.executeQuery("SELECT * FROM %s WHERE %s='%s'",
                    TABLE_NAME, USERNAME_COLUMN, username);
            exists = result.next();
            result.close();
        } catch (SQLException ex) {
            exists = false;
        }
        return exists;
    }

    @Override
    public User construct(ResultSet resultSet) {
        try {
            return new User(
                    resultSet.getString(USERNAME_COLUMN),
                    resultSet.getString(PASSWORD_HASH_COLUMN)
            );
        } catch (SQLException ex) {
            return null;
        }
    }
}
