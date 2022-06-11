package manager;

import collection.Organization;
import common.Command;
import common.InputRequester;
import common.InputUtil;
import interfaces.IServer;
import interfaces.IServerCommand;
import interfaces.IServerPacket;
import packet.AuthenticationResponsePacket;
import util.LoggerUtil;
import util.SerializationUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.util.Locale;

public class CommandPacket implements IServerPacket {
    String command;
    Object[] args;
    Organization organization;
    transient InputRequester inputRequester = new InputRequester(new BufferedReader(new InputStreamReader(System.in)));


    public CommandPacket(String command, Object[] args) {
        this.command = command.trim().split(" ")[0];
        this.args = args;
    }

    public void handleOnServer(IServer server, SocketAddress clientAddress) {
        try {
            server.setOrganization(organization);
            IServerCommand command = (IServerCommand) CommandRegister.getCommands().get(this.command);
            if (!command.isEnabled()) {
                server.send(clientAddress, new MessagePacket(LoggerUtil.negativeAsString("Данная команда Вам недоступна")));
                return;
            }

            if (!server.getUsers().containsKey(clientAddress)) {
                server.send(clientAddress, new AuthenticationResponsePacket(AuthenticationResponsePacket.AuthenticationStatus.ERROR,
                        LoggerUtil.negativeAsString("Аутентификация не пройдена. Отключение")));
                return;
            }
            server.send(clientAddress, command.execute(server, server.getUsers().get(clientAddress), args));
        } catch (ClassCastException ex) {
            server.send(clientAddress,new MessagePacket(LoggerUtil.info("Продолжай работать")));
            return;
        }
    }

}