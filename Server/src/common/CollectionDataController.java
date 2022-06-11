package common;

import collection.Organization;
import interfaces.IDatabase;
import interfaces.common.IDataController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectionDataController implements IDataController<Integer, Organization> {
    private static final String COLLECTION_TABLE_NAME = "collection",
            SEQUENCE_NAME = "id_sequence",
            ID_COLUMN = "id",
            NAME_COLUMN = "name",
            COORDINATES_COLUMN = "coordinates",
            CREATION_DATE_COLUMN = "creation_date",
            ANNUAL_TURNOVER_COLUMN = "annual_turnover",
            FULL_NAME_COLUMN = "full_name",
            EMPLOYEES_COUNT_COLUMN = "employees_count",
            TYPE_COLUMN = "type",
            POSTAL_ADDRESS_COLUMN = "postal_address",
            OWNER_COLUMN = "owner";
    IDatabase database;
    IDataController<String, User> userDataController;

    public CollectionDataController(IDatabase database, IDataController<String, User> userDataController) {
        this.database = database;
        this.userDataController = userDataController;
        try {
//            database.executeQuery("DROP TABLE IF EXISTS collection CASCADE").close();
            database.executeQuery("DROP SEQUENCE IF EXISTS id_sequence").close();

            database.executeQuery("CREATE SEQUENCE IF NOT EXISTS %s", SEQUENCE_NAME).close();
            database.executeQuery("CREATE TABLE IF NOT EXISTS %s (" +
                            "%s bigint PRIMARY KEY UNIQUE, " +
                            "%s varchar(128) NOT NULL, " +
                            "%s varchar(128) NOT NULL, " +
                            "%s varchar(32) NOT NULL, " +
                            "%s float NOT NULL, " +
                            "%s varchar NOT NULL, " +
                            "%s integer NOT NULL, " +
                            "%s varchar NOT NULL, " +
                            "%s varchar NOT NULL, " +
                            "%s varchar NOT NULL" +
                            ")",
                    COLLECTION_TABLE_NAME, ID_COLUMN,
                    NAME_COLUMN, COORDINATES_COLUMN,
                    CREATION_DATE_COLUMN, ANNUAL_TURNOVER_COLUMN,
                    FULL_NAME_COLUMN, EMPLOYEES_COUNT_COLUMN,
                    TYPE_COLUMN, POSTAL_ADDRESS_COLUMN,
                    OWNER_COLUMN
            ).close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Organization> getAll() {
        List<Organization> organizations = new ArrayList<>();
        try {
            ResultSet result = database.executeQuery("SELECT * FROM %s ", COLLECTION_TABLE_NAME);
            while (result.next()) {
                organizations.add(construct(result));
            }
            result.close();
        } catch (SQLException ex) {

        }
        return organizations;
    }

    public Organization getByKey(Integer id) {
        try {
            ResultSet result = database.executeQuery("SELECT * FROM %s WHERE %s = %d",
                    COLLECTION_TABLE_NAME, ID_COLUMN, id);
            result.next();
            Organization organization = construct(result);
            result.close();
            return organization;
        } catch (SQLException ex) {
            return null;
        }
    }

    public boolean insert(Organization organization) {
        try {
            database.executeQuery("INSERT INTO %s  VALUES " +
                            "(nextval('%s'), '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s', '%s')",
                    COLLECTION_TABLE_NAME, SEQUENCE_NAME, organization.getName(),
                    organization.getCoordinates(), organization.getCreationDate(),
                    organization.getAnnualTurnover(), organization.getFullName(),
                    organization.getEmployeesCount(), organization.getType().name(),
                    organization.getPostalAddress(),
                    organization.getOwner().getName()
            ).close();
            ResultSet result = database.executeQuery("SELECT MAX(%s) FROM %s", ID_COLUMN, COLLECTION_TABLE_NAME);
            result.next();
            organization.setId(result.getInt(1));
            result.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(Organization organization) {
        try {
            database.executeQuery("UPDATE %s SET " +
                            "%s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s=%d, %s='%s', %s='%s'" +
                            "WHERE %s=%d",
                    COLLECTION_TABLE_NAME, NAME_COLUMN, organization.getName(),
                    COORDINATES_COLUMN, organization.getCoordinates(),
                    CREATION_DATE_COLUMN, organization.getCreationDate(),
                    ANNUAL_TURNOVER_COLUMN, organization.getAnnualTurnover(),
                    FULL_NAME_COLUMN, organization.getFullName(),
                    EMPLOYEES_COUNT_COLUMN, organization.getEmployeesCount(),
                    TYPE_COLUMN, organization.getType().name(),
                    POSTAL_ADDRESS_COLUMN, organization.getPostalAddress(),
                    ID_COLUMN, organization.getId()).close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public void delete(Organization organization) {
        try {
            database.executeQuery("DELETE FROM %s WHERE %s=%d",
                    COLLECTION_TABLE_NAME, ID_COLUMN, organization.getId()).close();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean exists(Integer id) {
        boolean exists;
        try {
            ResultSet result = database.executeQuery("SELECT * FROM %s WHERE %s=%d",
                    COLLECTION_TABLE_NAME, ID_COLUMN, id);
            exists = result.next();
            result.close();
        } catch (SQLException ex) {
            exists = false;
        }
        return exists;
    }

    public Organization construct(ResultSet result) {
        try {
            String[] coordinates = result.getString(COORDINATES_COLUMN).split(";");
            Organization organization = new Organization(
                    result.getString(NAME_COLUMN),
                    new Organization.Coordinates(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])),
                    Float.parseFloat(result.getString(ANNUAL_TURNOVER_COLUMN)),
                    result.getString(FULL_NAME_COLUMN),
                    result.getInt(EMPLOYEES_COUNT_COLUMN),
                    Organization.OrganizationType.valueOf(result.getString(TYPE_COLUMN)),
                    new Organization.Address(result.getString(POSTAL_ADDRESS_COLUMN))
            );
            organization.setId(result.getInt(ID_COLUMN));
            organization.setOwner(userDataController.getByKey(result.getString(OWNER_COLUMN)));
            organization.setCreationDate(LocalDate.parse(result.getString(CREATION_DATE_COLUMN)));
            return organization;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
