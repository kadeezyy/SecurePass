package packet;

import common.User;
import interfaces.IServer;
import interfaces.IServerPacket;
import util.LoggerUtil;

import java.net.SocketAddress;

public class AuthenticationRequestPacket implements IServerPacket {
    private static final SHA265Generator hashGenerator = new SHA265Generator();
    private final String name, password;

    public AuthenticationRequestPacket(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void handleOnServer(IServer server, SocketAddress clientAddress) {
        String passwordHash = hashGenerator.hash(password);
        if (server.getUserDataController().exists(name)) {
            User user = server.getUserDataController().getByKey(name);
            if (!passwordHash.equals(user.getPasswordHash())) {
                server.send(clientAddress, new AuthenticationResponsePacket(
                        AuthenticationResponsePacket.AuthenticationStatus.UNSUCCESSFUL,
                        LoggerUtil.negativeAsString("Неправильный пароль. Попробуйте еще раз")
                ));
                return;
            }
            server.getUsers().put(clientAddress, user);
            server.send(clientAddress, new AuthenticationResponsePacket(
                    AuthenticationResponsePacket.AuthenticationStatus.SUCCESSFUL,
                    LoggerUtil.positiveAsString("Вы успешно авторизовались")
            ));
        } else {
            User user = new User(name, passwordHash);
            if (!server.getUserDataController().insert(user)) {
                server.send(clientAddress, new AuthenticationResponsePacket(
                        AuthenticationResponsePacket.AuthenticationStatus.ERROR,
                        LoggerUtil.negativeAsString("Во время регистрации произошла ошибка")
                ));
                return;
            }
            server.getUsers().put(clientAddress, user);
            server.send(clientAddress, new AuthenticationResponsePacket(
                    AuthenticationResponsePacket.AuthenticationStatus.SUCCESSFUL,
                    LoggerUtil.positiveAsString("Вы успешно зарегистрировались.")
            ));
        }
    }
}
