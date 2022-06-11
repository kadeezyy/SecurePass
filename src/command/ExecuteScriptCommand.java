package command;

import interfaces.IClient;
import interfaces.IClientCommand;
import manager.MessagePacket;
import util.LoggerUtil;

import java.io.*;
import java.util.*;

public class ExecuteScriptCommand implements IClientCommand {
    Set<String> scriptInProcess = new HashSet<>();

    public String getName() {
        return "execute_script";
    }

    public String getDescription() {
        return "Считать и исполнить скрипт из файла";
    }

    public List<String> getParameters() {
        return Collections.singletonList("Имя файла");
    }


    public void execute(IClient client, Object[] args) {
        if (args.length < 1) {
            LoggerUtil.negative("Необходимо указать путь к файлу.");
            return;
        }

        File file = new File(args[0].toString());
        String absolutePath = file.getAbsolutePath();

        if (scriptInProcess.contains(absolutePath)) {
            LoggerUtil.negative("Данный скрипт уже выполняется.");
            return;
        }

        scriptInProcess.add(absolutePath);
        BufferedReader lastReader = client.getInputRequester().getReader();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            client.getInputRequester().setReader(reader);
            while (reader.ready()) {
                String line = reader.readLine();
                client.getCommandManager().findCommand(line);
                Thread.sleep(250);
            }
            LoggerUtil.positive("Все команды выполнены");
        } catch (FileNotFoundException ex) {
            LoggerUtil.negative("Файл не найден");
        } catch (Exception ex) {
            LoggerUtil.negative("Возникла ошибка при выполнении скрипта");
        }
        scriptInProcess.remove(absolutePath);
        client.getInputRequester().setReader(lastReader);
    }
}