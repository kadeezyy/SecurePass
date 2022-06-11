package common;

import interfaces.IClient;

import java.io.BufferedReader;

public abstract class ScriptCommand {
    public abstract void execute(IClient client, String command, BufferedReader reader);
    public abstract String getName();
}