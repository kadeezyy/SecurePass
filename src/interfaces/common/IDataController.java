package interfaces.common;

import java.sql.ResultSet;
import java.util.List;

public interface IDataController<Key, Entity> {
    List<Entity> getAll();

    Entity getByKey(Key key);

    boolean insert(Entity entity);

    boolean update(Entity entity);

    void delete(Entity entity);

    boolean exists(Key key);

    Entity construct(ResultSet resultSet);
}
