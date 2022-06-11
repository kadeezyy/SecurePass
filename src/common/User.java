package common;

public class User {
    final String name, password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return password;
    }
}
