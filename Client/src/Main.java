import common.Authentication;
import common.InputRequester;
import manager.CommandManager;
import util.EnvVariablesUtil;
import util.LoggerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        InputRequester inputRequester = new InputRequester(new BufferedReader(new InputStreamReader(System.in)));
        Client client = new Client(EnvVariablesUtil.getHost(), EnvVariablesUtil.getPort());
        client.setInputRequester(inputRequester);
        Authentication authenticator = new Authentication(client);
        client.setAuthenticator(authenticator);
        client.registerResponseHandler();
        authenticator.processAuthentication();
        CommandManager commandManager = new CommandManager(client, inputRequester);
        client.setCommandManager(commandManager);

        while (true) {
            System.out.println("Введите команду: ");
            try {
                String command = inputRequester.getReader().readLine().trim().toLowerCase();
                commandManager.findCommand(command);
            } catch (IOException | NullPointerException ignored) {
                ignored.printStackTrace();
                LoggerUtil.negative("Данные введены неверно! " + ignored.getMessage());
            }
        }
    }
}