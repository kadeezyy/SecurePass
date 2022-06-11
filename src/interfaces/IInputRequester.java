package interfaces;

import java.io.BufferedReader;
import java.util.function.Function;

public interface IInputRequester {
    BufferedReader getReader();

    void setReader(BufferedReader reader);

    <T> T requestInput(String message, Function<String, Object> handler);

    default String requestInput(String message) {
        return requestInput(message, input -> input);
    }
}
