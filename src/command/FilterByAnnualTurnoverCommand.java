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

public class FilterByAnnualTurnoverCommand implements IServerCommand {

    public String getName() {
        return "filter_by_annual_turnover";
    }

    public String getDescription() {
        return "Вывести все элементы с равным годовым оборотом";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Годовой оборот");
    }


    public MessagePacket execute(IServer server, User user, Object[] args) {
        MessagePacket packet = new MessagePacket(LoggerUtil.info("Элементы с таким годовым оборотом:"));
        try {
            List<Organization> list = server.getCollectionManager().getCollection().stream()
                    .filter(organization -> organization.isOwner(user) && organization.getAnnualTurnover() == args[0])
                    .distinct().collect(Collectors.toList());
            if (list.isEmpty()) {
                return new MessagePacket(LoggerUtil.negativeAsString("В коллекции нет таких элементов"));
            } else {
                packet.addLines(LoggerUtil.info("Элементы с таким годовым оборотом:"));
                list.forEach(org -> packet.addLines(org.toString()));
            }
        } catch (ArrayIndexOutOfBoundsException ex1) {
            return new MessagePacket(LoggerUtil.negativeAsString("Введите аргумент"));
        } catch (Exception ex) {
            return new MessagePacket(LoggerUtil.negativeAsString("Неверно введены данные " + ex.getMessage()));
        }
        return packet;
    }
}