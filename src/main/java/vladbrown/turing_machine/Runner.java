package vladbrown.turing_machine;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class Runner {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, FileNotFoundException {
        TuringMachine tm = new TuringMachine(args);
        tm.start("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\main\\java\\text.txt");
    }
}
