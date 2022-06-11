package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterByFullNameCommand implements IServerCommand {

    public String getName() {
        return "filter_contains_full_name";
    }

    public String getDescription() {
        return "Вывести элементы, имеющие такую же фамилию";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Фамилия");
    }

    public MessagePacket execute(IServer server, User user, Object[] args) {
        MessagePacket packet = new MessagePacket(LoggerUtil.info("Элементы с такой фамилией:"));
        try {
            List<Organization> list = server.getCollectionManager().getCollection().stream()
                    .filter(organization1 -> organization1.isOwner(user) && organization1.getFullName().contains(String.valueOf(args[0])))
                    .distinct().collect(Collectors.toList());
            if (list.isEmpty()) {
                return new MessagePacket(LoggerUtil.negativeAsString("В коллекции нет элементов"));
            } else {
                list.forEach(org -> packet.addLines(org.toString()));
            }
        } catch (ArrayIndexOutOfBoundsException ex1) {
            return new MessagePacket(LoggerUtil.negativeAsString("Необходимо ввести аргумент"));
        } catch (Exception ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Неверно введены данные " + ex.getMessage()));
        }
        return packet;
    }
}