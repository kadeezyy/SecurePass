package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RemoveByIdCommand implements IServerCommand {

    public String getName() {
        return "remove_by_id";
    }

    public String getDescription() {
        return "удалить элемент коллекции по ID";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Идентификатор");
    }


    public MessagePacket execute(IServer server, User user, Object[] args) {
        try {
            int id = Integer.parseInt((args[0]).toString());
            Optional<Organization> optional = server.getCollectionManager().getCollection().stream()
                    .filter(organization -> organization.getId() == id).findFirst();

            if (!optional.isPresent()) {
                return new MessagePacket(LoggerUtil.negativeAsString("Элемента с таким ID нет"));
            }
            Organization organization = optional.get();
            if (!organization.isOwner(user)) {
                return new MessagePacket(LoggerUtil.negativeAsString("Этот элемент Вам не принадлежит"));
            }
            organization = server.getCollectionManager().getDataController().getByKey(id);
            server.getCollectionManager().getDataController().delete(organization);
            server.getCollectionManager().getCollection().remove(organization);
            return new MessagePacket(LoggerUtil.positiveAsString(String.format("Элемент с идентификатором %s успешно удален", id)));
        } catch (ArrayIndexOutOfBoundsException exception) {
            return new MessagePacket(LoggerUtil.negativeAsString("Нужно ввести аргумент"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new MessagePacket(LoggerUtil.negativeAsString("Не удалось удалить элемент (необходимо указать число)"));
        }
    }
}