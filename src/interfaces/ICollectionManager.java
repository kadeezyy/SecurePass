package interfaces;

import collection.Organization;
import interfaces.common.IDataController;

import java.util.Collection;

public interface ICollectionManager {
    void loadCollection();

    Collection<Organization> getCollection();

    IDataController<Integer, Organization> getDataController();

    long getInitDate();

}
