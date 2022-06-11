package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveLowerCommand implements IServerCommand {

    public String getName() {
        return "remove_lower";
    }

    public String getDescription() {
        return "удалить из коллекции элементы, меньшие, чем заданный";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Организация");
    }

    public MessagePacket execute(IServer server, User user, Object[] args) {
        MessagePacket packet = new MessagePacket(LoggerUtil.info("Введите данные элемента, с которым нужно сравнивать"));
        try {
            Set<Organization> toRemove = server.getCollectionManager().getCollection().stream()
                    .filter(organization -> organization.isOwner(user) && organization.getAnnualTurnover() < ((Organization) args[0]).getAnnualTurnover())
                    .collect(Collectors.toSet());
            toRemove.forEach(server.getCollectionManager().getDataController()::delete);
            server.getCollectionManager().getCollection().removeAll(toRemove);
            packet.addLines(LoggerUtil.positiveAsString("Элементы успешно удалены"));
        } catch (ArrayIndexOutOfBoundsException ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Нужно ввести аргумент"));
        } catch (Exception ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Ошибка! " + ex.getMessage()));
        }
        return packet;
    }
}