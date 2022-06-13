package common;

import collection.Organization;
import interfaces.IDatabase;
import interfaces.common.IDataController;

import java.sql.*;
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
            ResultSet res = database.executeQuery("SELECT MAX(%s) FROM %s", ID_COLUMN, COLLECTION_TABLE_NAME);
            long maxId = 0;
            while (res.next()) {
                if (res.getInt(1) >= maxId) {
                    maxId = res.getInt(1);
                }
            }
            res.close();
            database.executeQuery("DROP SEQUENCE IF EXISTS " + SEQUENCE_NAME).close();

            database.executeQuery("CREATE SEQUENCE IF NOT EXISTS " + SEQUENCE_NAME + " START " + (maxId + 1)).close();
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
            ResultSet result = database.executeQuery("SELECT MAX(%s) FROM %s", ID_COLUMN, COLLECTION_TABLE_NAME);
            result.next();
            organization.setId(result.getInt(1) + 1);
            result.close();
            database.setStatement("INSERT INTO collection VALUES (nextval(?), ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            database.getStatement().setString(1, SEQUENCE_NAME);
            database.getStatement().setString(2, organization.getName());
            database.getStatement().setString(3, organization.getCoordinates());
            database.getStatement().setString(4, String.valueOf(organization.getCreationDate()));
            database.getStatement().setFloat(5, organization.getAnnualTurnover());
            database.getStatement().setString(6, organization.getFullName());
            database.getStatement().setInt(7, organization.getEmployeesCount());
            database.getStatement().setString(8, organization.getType().name());
            database.getStatement().setString(9, organization.getPostalAddress());
            database.getStatement().setString(10, organization.getOwner().getName());
            database.executeQueryStatement().close();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(Organization organization) {
        try {
            database.setStatement("UPDATE  collection SET name=?, coordinates=?, creation_date=?, " +
                    "annual_turnover=?, full_name=?, employees_count=?, type=?, postal_address=?,id=?");
            database.getStatement().setString(1, organization.getName());
            database.getStatement().setString(2, organization.getCoordinates());
            database.getStatement().setString(3, String.valueOf(organization.getCreationDate()));
            database.getStatement().setFloat(4, organization.getAnnualTurnover());
            database.getStatement().setString(5, organization.getFullName());
            database.getStatement().setInt(6, organization.getEmployeesCount());
            database.getStatement().setString(7, organization.getType().name());
            database.getStatement().setString(8, organization.getPostalAddress());
            database.getStatement().setInt(9, organization.getId());
            database.executeQueryStatement().close();
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
