package common;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import util.LoggerUtil;

public class SSHTunnel {
    private final String host, user, password;
    private final int port;
    private final String tunnelRemoteHost;
    private final int tunnelLocalPort, tunnelRemotePort;

    public SSHTunnel(String host, String user, String password, int port, String tunnelRemoteHost,
                     int tunnelLocalPort, int tunnelRemotePort) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.tunnelRemoteHost = tunnelRemoteHost;
        this.tunnelLocalPort = tunnelLocalPort;
        this.tunnelRemotePort = tunnelRemotePort;
    }
    public int create() {
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setUserInfo(new LocalUserInfo());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(3000);
            session.connect();
            return session.setPortForwardingL(tunnelLocalPort, tunnelRemoteHost, tunnelRemotePort);
        } catch (JSchException ex) {
            ex.printStackTrace();
            LoggerUtil.negative("Подключение через SSH-туннель не было установлено: отключение.");
            System.exit(0);
        }
        return -1;
    }

    private class LocalUserInfo implements UserInfo {

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptPassword(String s) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return true;
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public void showMessage(String s) {}
    }
}
