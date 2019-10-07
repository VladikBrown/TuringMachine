package vladbrown.turing_machine;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.sql.*;

public class Runner {


    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, FileNotFoundException {
        DataBase dm = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        dm.showRules();

    }
}
//C:\JAVA_Library\2_sem\PIVAS\LABA_1\src\text.txt
