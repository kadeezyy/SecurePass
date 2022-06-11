package common;

import interfaces.IClient;
import interfaces.common.IAuthenticator;
import packet.AuthenticationRequestPacket;
import util.LoggerUtil;

import java.io.Console;

public class Authentication implements IAuthenticator {
    IClient client;
    boolean authenticated = false;

    public Authentication(IClient client) {
        this.client = client;
    }

    public void processAuthentication() {
        LoggerUtil.info("Для использования приложения необходима аутентификация");
        while (!authenticated) {
            client.send(new AuthenticationRequestPacket(client.getInputRequester().requestInput(LoggerUtil.info("Введите логин: "),
                    input -> {
                        if (input.length() < 3 | input.length() > 16) {
                            LoggerUtil.negative("Длина логина должна быть от 3 до 16 символов");
                            return null;
                        }
                        return input.trim();
                    }), requestPassword()));
        }
    }

    private String requestPassword() {
        Console console = System.console();
        if (console == null) {
//            char [] pass = System.console().readPassword("[%s]",LoggerUtil.info("Введите пароль: "));
//            String password = pass.toString();
//            if (password.length()< 6 || password.length() > 32) {
//                LoggerUtil.negative("Длина пароля должна быть от 6 до 32 символов.");
//                return null;
//            }
//            return password.trim();
            return client.getInputRequester().requestInput(LoggerUtil.info("Введите пароль: "), input -> {
                if (input.length() < 6 || input.length() > 32) {
                    LoggerUtil.negative("Длина пароля должна быть от 6 до 32 символов.");
                    return null;
                }
                return input.trim();
            });
        }
        char[] passwordChars = console.readPassword(String.format(LoggerUtil.info("Введите пароль%s: "),
                LoggerUtil.ConsoleColor.RESET));
        if (passwordChars.length < 6 || passwordChars.length > 32) {
            LoggerUtil.negative("Длина пароля должна быть от 6 до 32 символов.");
            return requestPassword();
        }
        return new String(passwordChars).trim();
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
