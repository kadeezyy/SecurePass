package interfaces;

import interfaces.common.IAuthenticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IClient {
    void send(IServerPacket packet);
    IInputRequester getInputRequester();
    ICommandManager getCommandManager();
    IAuthenticator getAuthenticator();
    default List<String> getCommandHistory(){
        return new ArrayList(Arrays.asList());
    }
}