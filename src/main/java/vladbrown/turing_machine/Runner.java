package vladbrown.turing_machine;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class Runner {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, FileNotFoundException {
        String[] test = {"C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\main\\java\\text.txt", "-log"};
        TuringMachine tm = new TuringMachine(args);
        tm.start("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\main\\java\\text.txt");
    }
}
