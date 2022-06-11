package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import sun.rmi.runtime.Log;
import util.LoggerUtil;

public class ShowCommand implements IServerCommand {

    public String getName() {
        return "show";
    }

    public String getDescription() {
        return "Вывести элементы коллекции";
    }

    public MessagePacket execute(IServer server, User user, Object[] args) {
        if (server.getCollectionManager().getCollection().isEmpty()) {
            return new MessagePacket(LoggerUtil.negativeAsString("Коллекция пустая"));
        }

        MessagePacket packet = new MessagePacket(LoggerUtil.info("Элементы коллекции:"));
        int i = 1;
        for (Organization organization : server.getCollectionManager().getCollection()) {
            packet.addLines(
                    LoggerUtil.info(String.format("Элемент №%s ",i++)),
                    organization.toString()
            );
//            server.send(server.getAddress(), new MessagePacket(String.format("Элемент №%s ",i++), organization.toString()));
        }
        return packet;

    }
}