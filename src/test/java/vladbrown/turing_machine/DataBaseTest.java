package vladbrown.turing_machine;

import org.junit.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {
    public static final String userName = "root";
    public static final String password = "9098";
    public static final String connectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";


    private static Connection connection;
    private static DataBase dataBase;

    static {
        try {
            dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void prepareTestGetDirection() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into turing_machine_rules.rules values ('test_q0','A','q4','right_direction')");
    }

    public void finishTestGetDirection() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from turing_machine_rules.rules where current_state = 'test_q0' and current_value = 'A'");
    }
    @Test
    public void getDirection() throws SQLException {
        prepareTestGetDirection();
        assertEquals("right_direction", dataBase.getDirection("test_q0","A"));
        finishTestGetDirection();
    }


    public void prepareTestStringGetNextState() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into turing_machine_rules.rules values ('q0','A','right_next_state','test_direction')");
    }

    public void finishTestStringGetNextState() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from turing_machine_rules.rules where current_state = 'q0' and current_value = 'A'");
    }
    @Test
    public void stringGetNextState() throws SQLException {
        prepareTestStringGetNextState();
        assertEquals("right_next_state",  dataBase.stringGetNextState("q0", "A"));
        finishTestStringGetNextState();
    }

    public void prepareTestChangeRule() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into turing_machine_rules.rules values ('1','2','3','4')");
    }

    public void finishTestChangeRule() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from turing_machine_rules.rules where current_state = '1+' and current_value = '2+'");
    }
    @Test
    public void changeRule() throws SQLException {
        prepareTestChangeRule();
        dataBase.changeRule(1, "1+", "2+", "3+", "4+");
        finishTestChangeRule();
    }

    @Test
    void insertRule() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into turing_machine_rules.rules values ('insertTest_1','insertTest_2','testInsert_next_state','testInsert_direction')");
        assertEquals("testInsert_next_state", dataBase.stringGetNextState("insertTest_1","insertTest_2"));
        assertEquals("testInsert_direction", dataBase.getDirection("insertTest_1","insertTest_2"));
        statement.executeUpdate("delete from turing_machine_rules.rules where current_state = 'insertTest_1' and current_value = 'insertTest_2'");
    }

    @Test
    void removeRule() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, userName, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into turing_machine_rules.rules values ('removeTest_1','removeTest_2','tesRemove_next_state','testRemove_direction')");
        assertEquals("testRemove_direction", dataBase.getDirection("removeTest_1","removeTest_2"));
        dataBase.removeRule(1);
        assertNull(dataBase.getDirection("removeTest_1","removeTest_2"));
    }
}