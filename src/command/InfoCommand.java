package command;

import collection.Organization;
import common.User;
import interfaces.IServer;
import interfaces.IServerCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.text.SimpleDateFormat;
import java.util.Collection;

public class InfoCommand implements IServerCommand {

    public String getName(){
        return "info";
    }

    public String getDescription(){
        return "Вывести информацию о коллекции";
    }

    public MessagePacket execute(IServer server, User user, Object[] args){
        MessagePacket packet = new MessagePacket(LoggerUtil.info("Информация о коллекции:"));
        Collection<Organization> collection = server.getCollectionManager().getCollection();
        packet.addLines("Тип: " + collection.getClass().getSuperclass().getName());
        packet.addLines("Дата инициализации: " + new SimpleDateFormat().format(server.getCollectionManager().getInitDate()));
        packet.addLines("Количество элементов: " + collection.size());
        return packet;
    }
}