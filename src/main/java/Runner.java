import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.sql.*;

public class Runner {


    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        String userMame = "root";
        String password = "9098";
        String conectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        try (Connection connection = DriverManager.getConnection(conectionURL, userMame, password);
             Statement statement = connection.createStatement()) {
            Statement st = connection.createStatement();
            System.out.println("DB is connected");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Connection connection1 = DriverManager.getConnection(conectionURL, userMame, password);
        Statement sta =connection1.createStatement();

    }
}
//C:\JAVA_Library\2_sem\PIVAS\LABA_1\src\text.txt
