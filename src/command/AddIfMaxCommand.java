package command;

import collection.Organization;
import common.InputRequester;
import common.InputUtil;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddIfMaxCommand implements IServerCommand {
    InputRequester inputRequester;

    public String getName() {
        return "add_if_max";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Организация");
    }

    public String getDescription() {
        return "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента коллекции";
    }

    @Override
    public MessagePacket execute(IServer server, User user, Object [] args) {
        inputRequester = new InputRequester(new BufferedReader(new InputStreamReader(System.in)));
        LoggerUtil.infoAsString("Введите данные элемента, с которым нужно сравнить");
        Organization organization = (Organization) args[0];
        organization.setOwner(user);
        Float maxValue = server.getCollectionManager().getCollection().stream()
                .max(Comparator.comparing(Organization::getAnnualTurnover))
                .map(Organization::getAnnualTurnover).orElse(Float.valueOf(-1));
        if (organization.getAnnualTurnover() > maxValue) {
            if (server.getCollectionManager().getDataController().insert(organization)) {
                server.getCollectionManager().getCollection().add(organization);
                return new MessagePacket(LoggerUtil.positiveAsString("Элемент успешно добавлен в коллекцию"));
            }
        }
        return new MessagePacket(LoggerUtil.negativeAsString("Элемент не удалось добавить в коллекцию"));

    }
}