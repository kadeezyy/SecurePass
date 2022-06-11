package command;

import interfaces.IClient;
import interfaces.IClientCommand;
import util.LoggerUtil;

import java.util.List;

public class HistoryCommand implements IClientCommand {
    List<String> commandHistory;

    public String getName() {
        return "history";
    }

    public String getDescription() {
        return "Вывести последние 7 команд";
    }

    public void execute(IClient client, Object [] args) {
        LoggerUtil.info("История: ");
        commandHistory = client.getCommandHistory();

        if (commandHistory.size() > 0) {
            if (commandHistory.size() < 7) {
                for (int i = 0; i < commandHistory.size(); i++) {
                    System.out.println(commandHistory.get(i));
                }
//                System.out.println(list);
            } else {
                for (int i = 0; i < 7; i++) {
                    System.out.println(commandHistory.get(i));
                }
            }
        } else {
            LoggerUtil.negativeAsString("История пустая");
        }

    }
}