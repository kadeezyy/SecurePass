package manager;

import collection.Organization;
import common.CollectionDataController;
import common.User;
import interfaces.ICollectionManager;
import interfaces.IDatabase;
import interfaces.common.IDataController;
import util.LoggerUtil;

import java.util.*;


public class CollectionManager implements ICollectionManager {
    Collection<Organization> collection = new LinkedHashSet<>();
    long initDate = 0;
    CollectionDataController dataController;

    public CollectionManager(IDatabase database, IDataController<String, User> userDataController){
        this.dataController = new CollectionDataController(database, userDataController);
    }

    @Override
    public void loadCollection() {
        collection.addAll(dataController.getAll());
        initDate = System.currentTimeMillis();
        LoggerUtil.infoAsString(String.format("Загружена коллекция, состоящая из %d элементов.",
                collection.size()));
    }

    public long getInitDate(){
        return initDate = System.currentTimeMillis();
    }

    public Collection<Organization> getCollection() {
        return collection;
    }

    @Override
    public CollectionDataController getDataController() {
        return  dataController;
    }



}