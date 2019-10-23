package vladbrown.turing_machine;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {

    private Connection connection;
    private Statement statement;
    DataBase dataBase;

    @BeforeClass
    void prepareToTestDataBase() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        String userName = "root";
        String password = "9098";
        String connectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        try (Connection connection = DriverManager.getConnection(connectionURL, userName, password);
             Statement statement = connection.createStatement()) {
            this.statement = connection.createStatement();
            this.connection = connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass void finishTestDataBase() throws SQLException {
        this.connection.close();
        this.statement.close();
    }

    @Before
    void prepareTestGetDirection() throws SQLException {
        this.statement.executeUpdate("insert into turing_machine_rules.rules(*) values ('q0','A','q4','right_direction')");
    }
    @After
    void finishTestGetDirection() throws SQLException {
        this.statement.executeUpdate("delete from turing_machine_rules.rules where current_state = 'q0' and current_value = 'A'");
    }
    @Test
    void getDirection() throws SQLException {
        assertEquals("expected direction is right_direction", "right_direction", dataBase.getDirection("q0","A"));
    }

    @Before
    void prepareTestGetNextState() throws SQLException {
        this.statement.executeUpdate("insert into turing_machine_rules.rules(*) values ('q0','A','right_next_state','test_direction')");
    }
    @After
    void finishTestGetNextState() throws SQLException {
        this.statement.executeUpdate("delete from turing_machine_rules.rules where current_state = 'q0' and current_value = 'A'");
    }
    @Test
    void stringGetNextState() throws SQLException {
        assertEquals("expected right_next_direction", "right_next_direction", dataBase.stringGetNextState("q0", "A"));
    }

    @Test
    void getNextState() {
    }

    @Test
    void getDescriptionOfRule() {
    }

    @Test
    void insertRule() {
    }

    @Test
    void removeRule() {
    }

    @Test
    void changeRule() {
    }

    @Test
    void showRules() {
    }
}