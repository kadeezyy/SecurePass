import collection.Organization;
import common.User;
import dataController.UserDataController;
import interfaces.IClientPacket;
import interfaces.IServer;
import interfaces.IServerPacket;
import manager.CollectionManager;
import util.LoggerUtil;
import util.SerializationUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements IServer {
    private DatagramChannel channel;
    CollectionManager collectionManager;
    public Organization organization;
    SocketAddress address;
    List<String> commandHistory = new ArrayList<>();
    final ExecutorService readingService = Executors.newFixedThreadPool(2),
            handlingService = Executors.newCachedThreadPool();


    private UserDataController userDataController;
    final Map<SocketAddress, User> users = new HashMap<>();


    public Server(int port) {
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            LoggerUtil.positive("Сервер был успешно запущен. Порт: " + port);
        } catch (IOException ex) {
            LoggerUtil.negative("Не удалось запустить сервер. Отключение");
            System.exit(0);
        }
    }

    public void send(SocketAddress address, IClientPacket packet) {
        this.address = address;
        new Thread(() -> {
            try {
                channel.send(ByteBuffer.wrap(SerializationUtil.serialize(packet)), address);
            } catch (IOException ex) {
                LoggerUtil.negative("Не удалось отправить данные клиенту " + ex.getMessage());
            }
        }).start();
    }

    public void start() {
        ByteBuffer buffer = ByteBuffer.allocate(100000);
        while (true) {
            SocketAddress clientAddress = null;
            while (clientAddress == null) {
                try {
                    clientAddress = channel.receive(buffer);
                } catch (IOException ex) {
                }
            }
            SocketAddress finalClientAddress = clientAddress;
            readingService.execute(() -> {
                try {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.limit()];
                    buffer.get(bytes);
                    buffer.clear();

                    IServerPacket packet = (IServerPacket) SerializationUtil.deserialize(bytes);
                    handlingService.execute(() -> packet.handleOnServer(this, finalClientAddress));
                } catch (IOException | ClassNotFoundException ex) {
                    LoggerUtil.negative("Некорректные данные с клиента ");
                }
            });

        }
    }

    public Map<SocketAddress, User> getUsers() {
        return users;
    }

    public UserDataController getUserDataController() {
        return userDataController;
    }

    public void setUserDataController(UserDataController userDataController) {
        this.userDataController = userDataController;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }


    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public Organization getOrganization() {
        return organization;
    }

    public SocketAddress getAddress() {
        return address;
    }

}
