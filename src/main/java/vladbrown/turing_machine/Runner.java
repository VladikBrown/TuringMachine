package vladbrown.turing_machine;

import java.sql.SQLException;

public class Runner {
    // убрать throws ибо дурной тон
    public static void main(String[] args) throws ClassNotFoundException , SQLException{
        String[] test = {"C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\main\\java\\text.txt", "-log"};
        TuringMachine tm = new TuringMachine(test);
        tm.start();

    }
}
