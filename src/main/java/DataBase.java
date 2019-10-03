import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Scanner;

//rename
public class DataBase {
    private Connection connection;
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
            this.isConnectable = true;
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String getDataFromDB(String currentState, String currentSymbol, String typeOfData) throws SQLException {
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement("select ? from turing_machine_rules.rules where current_state = ? and current_value = ?");
            preparedStatement.setString(1, typeOfData);
            preparedStatement.setString(2, currentState);
            preparedStatement.setString(3, currentSymbol);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.toString();
        }
        else {
            return null;
        }
    }

    public String getDirection(String currentState, String currentSymbol) throws SQLException {
        return getDataFromDB(currentState, currentSymbol, "direction");
    }

    public String getNextState(String currentState, String currentSymbol) throws SQLException {
        return getDataFromDB(currentState, currentSymbol, "next_state");
    }

    public String getRule(String currentState, String currentSymbol) throws SQLException {
        //можно заменить лямбдой???
        String processedDirection = "remain in place";
        if(getDirection(currentState, currentSymbol) == "L"){
            processedDirection = "move to the Left";
        }
        else if (getDirection(currentState, currentSymbol) == "R"){
            processedDirection = "move to the Right";
        }
        String processedRule = "If current state is " + currentState + "and current symbol is " + currentSymbol +
                "then head should " + processedDirection + " and go into the State: " + getNextState(currentState, currentSymbol);
        return  processedRule;
    }

    public void insertRule() throws SQLException {
        //вывод можно закинуть в отдельную функцию
        //проверочки добавить
        System.out.println("Attention! You are adding a rule\n Please, enter right data");
        System.out.println("First entering data is current state\n");
        System.out.println("Second entering data is current symbol\n");
        System.out.println("Third entering data is next state\n");
        System.out.println("Fourth entering data is direction of movement of the Head\n");
        Scanner in = new Scanner(System.in);
        System.out.println("Enter current state in format: q%, where % - number of state\n");
        String current_state = in.next();
        System.out.println("Enter current symbol\n");
        String current_symbol = in.next();
        System.out.println("Enter next state in format: q%, where % - number of state\n");
        String next_state = in.next();
        System.out.println("Enter direction of movement: R means Right, L means Left, S - means remaining in current place\n" );
        String direction = in.next();
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into turing_machine_rules.rules(*) values (?, ?, ?, ?)");
            preparedStatement.setString(1, current_state);
            preparedStatement.setString(2, current_symbol);
            preparedStatement.setString(3, next_state);
            preparedStatement.setString(4, direction);
        }
        System.out.println("Данные были успешно добавлены");
    }

    public void showRules() throws SQLException {
        if(isConnectable){
            connection = DriverManager.getConnection(URL, userName, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from turing_machine_rules.rules");
            int index = 1;
            while (resultSet.next()){
                System.out.print(index++ + ". ");
                for (int i = 1; i <= 4; i++){
                System.out.print(resultSet.getString(i) + "\t");
                }
            }
        }
    }
}

   /*
    String userName = "root";
    String password = "9098";
    String connectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
   */