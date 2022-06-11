package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.Set;
import java.util.stream.Collectors;


public class ClearCommand implements IServerCommand {
    public String getName() {
        return "clear";
    }

    public String getDescription() {
        return "Очистить коллекцию";
    }


    public MessagePacket execute(IServer server, User user, Object[] args) {
        Set<Organization> set = server.getCollectionManager().getCollection().stream().filter(
                organization -> organization.isOwner(user)).collect(Collectors.toSet());
        set.forEach(server.getCollectionManager().getDataController()::delete);
        set.forEach(server.getCollectionManager().getCollection()::remove);
        if (set.size() == 0) {
            return new MessagePacket(LoggerUtil.info("В коллекции не было доступных Вам элементов"));
        }

//        server.getCollectionManager().getCollection().forEach((organization ->
//                server.getCollectionManager().getDataController().delete(organization)));
//        server.getCollectionManager().getCollection().clear();
        return new MessagePacket(LoggerUtil.positiveAsString("Коллекция очищена"));
    }
}