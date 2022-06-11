import common.PostgreSQLDatabase;
import common.SSHTunnel;
import dataController.UserDataController;
import interfaces.IDatabase;
import manager.CollectionManager;
import util.EnvVariablesUtil;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(EnvVariablesUtil.getPort());
        IDatabase database = initDatabase();
        database.connect();
        UserDataController userDataController = new UserDataController(database);
        CollectionManager collectionManager = new CollectionManager(database, userDataController);
        collectionManager.loadCollection();
        Runtime.getRuntime().addShutdownHook(new Thread(database::disconnect));
        server.setCollectionManager(collectionManager);
        server.setUserDataController(userDataController);
        server.start();
    }
    private static IDatabase initDatabase() {
        String user = "s341474";
        String password = "drj262";
        SSHTunnel tunnel = new SSHTunnel("se.ifmo.ru", user, password, 2222, "pg", 8594, 5432);
        return new PostgreSQLDatabase("localhost", tunnel.create(), "studs", user, password);
    }
}