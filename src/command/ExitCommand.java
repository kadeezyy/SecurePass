package command;

import interfaces.*;
import util.LoggerUtil;

public class ExitCommand implements IClientCommand {
    public String getName(){
        return "exit";
    }

    public String getDescription(){
        return "Завершить программу (без сохранения)";
    }

    public void execute(IClient client, Object[] args){
        LoggerUtil.negative("Отключение программы.");
        System.exit(0);
    }
}