package command;

import collection.Organization;
import com.sun.org.apache.xpath.internal.operations.Or;
import common.Command;
import common.InputRequester;
import common.InputUtil;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.*;


public class UpdateByIdCommand implements IServerCommand {

    public String getName() {
        return "update_id";
    }

    public String getDescription() {
        return "Обновить значение элемента коллекции по заданному ID";
    }

    public List<String> getParameters() {
        return Arrays.asList("Идентификатор", "Организация");
    }

    public MessagePacket execute(IServer server, User user, Object[] args) {
        try {
            int id = Integer.parseInt(String.valueOf(args[1]));
            Organization organization = (Organization) args[0];
            organization.setOwner(user);
            Collection<Organization> collection = server.getCollectionManager().getCollection();
            Optional<Organization> optional = collection.stream().filter(org -> org.getId() == id).findFirst();
            if (!optional.isPresent()) {
                return new MessagePacket(LoggerUtil.negativeAsString("Элемента с таким ID нет"));
            }
            Organization existOrganization = optional.get();
            if (!existOrganization.isOwner(user)) {
                return new MessagePacket(LoggerUtil.negativeAsString("Данный элемент Вам не принадлежит"));
            }
            organization.setId(id);
            organization.setCreationDate(existOrganization.getCreationDate());
            server.getCollectionManager().getDataController().update(organization);
            collection.remove(existOrganization);
            collection.add(organization);
            return new MessagePacket(LoggerUtil.positiveAsString("Элемент был успешно обновлен"));
        } catch (ArrayIndexOutOfBoundsException ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Нужно ввести аргумент"));
        } catch (Exception ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Неверно введены данные " + ex.getMessage()));
        }
    }
}