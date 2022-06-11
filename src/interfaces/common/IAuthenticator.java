package interfaces.common;

public interface IAuthenticator {

    void setAuthenticated(boolean authenticated);

    void processAuthentication();
}
