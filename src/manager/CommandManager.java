package manager;

import collection.Organization;
import common.Command;
import common.InputRequester;
import common.InputUtil;
import interfaces.*;
import util.LoggerUtil;

import java.util.*;
import java.util.stream.Stream;

public class CommandManager implements ICommandManager{
    private Map<String, Command> map = new HashMap<>(15);
    private final IClient client;
    private final InputRequester inputRequester;
    List<String> commandHistory = new ArrayList<>();

    public CommandManager(IClient client, InputRequester inputRequester) {
        this.client = client;
        this.inputRequester = inputRequester;
    }


    public void findCommand(String commandName) {
        String[] args = commandName.trim().split(" ");
        String commadd = args[0].toLowerCase(Locale.ROOT);
        map = CommandRegister.getCommands();
        if (!map.containsKey(commadd)) {
            LoggerUtil.negative("Такой команды нет");
            LoggerUtil.infoAsString("Напишите \"help\" , чтобы увидеть список команд");
            return;
        }
        commandHistory.add(commadd);
        Command command = CommandRegister.getCommands().get(commadd);
        Object[] arg = Arrays.copyOfRange(args, 1, args.length);

//        if(commandName.equals("exit")){
//
//        }

        if (command.getParameters().contains("Организация")) {
            Organization organization = InputUtil.requestOrganization(inputRequester);
            arg = Stream.concat(Stream.of(organization), Arrays.stream(arg)).toArray();
        }
        client.getCommandHistory().add(commandName);
        if (command instanceof IClientCommand) {
            ((IClientCommand) command).execute(client, arg);
        }
        client.send(new CommandPacket(commandName, arg));
    }

    public List<String> getCommandHistory(){
        return commandHistory;
    }
}