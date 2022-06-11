package interfaces;

import collection.Organization;
import common.User;
import interfaces.common.IDataController;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public interface IServer {
    void send(SocketAddress address, IClientPacket packet);

    ICollectionManager getCollectionManager();

    void setOrganization(Organization organization);

    Organization getOrganization();

    SocketAddress getAddress();

    Map<SocketAddress, User> getUsers();

    IDataController<String, User> getUserDataController();



}