package common;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface Command extends Serializable {
    String getName();

    String getDescription();

    default List<String> getParameters() {
        return new ArrayList<>();
    }

}