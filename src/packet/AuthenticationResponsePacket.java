package packet;

import interfaces.IClient;
import interfaces.IClientPacket;

public class AuthenticationResponsePacket implements IClientPacket {
    AuthenticationStatus status;
    String message;

    public AuthenticationResponsePacket(AuthenticationStatus status, String message){
        this.status =status;
        this.message = message;
    }

    public void handleOnClient(IClient client){
        System.out.println(message);
        switch (status){
            case SUCCESSFUL:{
                client.getAuthenticator().setAuthenticated(true);
                break;
            }
            case ERROR: System.exit(0);
        }
    }
    public enum AuthenticationStatus {
        SUCCESSFUL,
        UNSUCCESSFUL,
        ERROR
    }
}
