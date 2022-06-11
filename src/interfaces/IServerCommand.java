package interfaces;

import common.Command;
import common.User;
import manager.MessagePacket;

public interface IServerCommand extends Command {
    MessagePacket execute(IServer server, User user, Object[] args);

    default boolean isEnabled(){
        return true;
    }

}