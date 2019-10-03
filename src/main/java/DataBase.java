import com.mysql.cj.x.protobuf.MysqlxCrud;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

//rename
public class DataBase {
    private Connection connection;
    private Statement statement;
    private String currentState;
    private String currentSymbol;
    private String direction;
    private String nextState;
    private String URL, userName, password;
    private boolean isConnectable = false;
    public DataBase(String URL, String userName, String password) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.URL = URL;
        this.userName = userName;
        this.password = password;
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        try (Connection connection = DriverManager.getConnection(URL, userName, password);
             Statement statement = connection.createStatement()) {
            Statement st = connection.createStatement();
            System.out.println("DB is connected");
            this.connection = connection;
            this.statement = statement;
            this.isConnectable = true;
            st.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // спросить про prepared statement
    // нормально ли что нет ключевого столбца??
    public String getDirection(String currentState, String currentSymbol) throws SQLException {
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select direction from turing_machine_rules.rules where current_state = '" + currentState + "' and current_value = '"+ currentSymbol +"'");
            return resultSet.toString();
        }
        else {
            return null;
        }
    }
}

   /*
    String userName = "root";
    String password = "9098";
    String connectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
   */