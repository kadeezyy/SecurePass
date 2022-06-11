package interfaces;

import common.Command;
import manager.MessagePacket;

public interface  IClientCommand extends Command {
    void execute(IClient client, Object[] args);

}