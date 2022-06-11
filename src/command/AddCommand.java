package command;

import collection.Organization;

import common.User;
import interfaces.*;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.Collections;
import java.util.List;


public class AddCommand implements IServerCommand {

    public String getName() {
        return "add";
    }

    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Организация");
    }

    public MessagePacket execute(IServer server, User user, Object[] args) {
        Organization organization = (Organization) args[0];
        organization.setOwner(user);
        if (server.getCollectionManager().getDataController().insert(organization)) {
            server.getCollectionManager().getCollection().add(organization);
            return new MessagePacket(LoggerUtil.positiveAsString("Элемент был добавлен в коллекцию"));
        }

        return new MessagePacket(LoggerUtil.negativeAsString("Не удалось добавить в коллекцию"));
    }
}