package vladbrown.turing_machine;

import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Scanner;

/**
 * Класс {@link DataBase} позволяет взаимодействовать с таблицей правил для машины Тьюринга,
 * которые хрянятся в базе данных.
 * Поля класса хранят запросы к базе данных и имена полей для более просто взаимодействия между машиной и базой данных.
 */
public class DataBase {

    private static final String SELECT_DIRECTION = "select direction from turing_machine_rules.rules_with_id where current_state = ? and current_symbol = ?";
    private static final String SELECT_NEXT_STATE = "select next_state from turing_machine_rules.rules_with_id where current_state = ? and current_symbol = ?";
    private static final String INSERT_RULE = "insert into turing_machine_rules.rules_with_id values (null, ?, ?, ?, ?)";
    private static final String DELETE_RULE = "delete from turing_machine_rules.rules_with_id where id = ?";
    private static final String UPDATE_RULE = "UPDATE turing_machine_rules.rules_with_id SET current_state = ?, current_symbol = ?, next_state = ?, direction = ? WHERE id = ?";
    private static final String SELECT_ALL_RULES = "select * from turing_machine_rules.rules_with_id";

    private static final String COLUMNNAME_ID = "id";
    private static final String COLUMNNAME_CURRENT_STATE = "current_state";
    private static final String COLUMNNAME_CURRENT_SYMBOL = "current_symbol";
    private static final String COLUMNNAME_NEXT_STATE = "next_state";
    private static final String COLUMNNAME_DIRECTION = "direction";

    private static final String STATE_0 = "q0";
    private static final String STATE_1 = "q1";
    private static final String STATE_2 = "q2";
    private static final String STATE_3 = "q3";

    private Connection connection;
    private String URL, userName, password;
    private boolean isConnectable = false;

    /**
     * Устанавливается соединение с базой данных.
     *
     * @param URL      Адрес базы данных с таблицой правил.
     * @param userName Имя пользователя.
     * @param password Пароль от базы данных.
     * @throws ClassNotFoundException Исключение для подключения к базе данных.
     */
    public DataBase(String URL, String userName, String password) throws ClassNotFoundException {
        this.URL = URL;
        this.userName = userName;
        this.password = password;
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(URL, userName, password);
             Statement statement = connection.createStatement()) {
            Statement st = connection.createStatement();
            this.connection = connection;
            this.isConnectable = true;
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Connection getConnection() throws SQLException {
        if (isConnectable) {
            return connection = DriverManager.getConnection(URL, userName, password);
        } else return null; //через try catch?
    }


    /**
     * @param currentState  Текущее состояние машины Тьюринга.
     * @param currentSymbol Текущий символ на ленте машины Тьюринга.
     * @return Строковое значение направления следующего шага каретки.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    String getDirection(String currentState, String currentSymbol) throws SQLException {
        return getDataFromDataBase(currentState, currentSymbol, SELECT_DIRECTION, COLUMNNAME_DIRECTION);
    }

    @Nullable
    private String getDataFromDataBase(String currentState, String currentSymbol, String selectQuery, String columnName) throws SQLException {
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, currentState);
            preparedStatement.setString(2, currentSymbol);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(columnName);
            }
            return "error";
        } else {
            return "error";
        }
    }

    /**
     * @param currentState  Текущее состояние машины Тьюринга.
     * @param currentSymbol Текущий символ на ленте машины Тьюринга.
     * @return Строковое значение следующего состояния, в которое должна перейти машина Тьюринга.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    String stringGetNextState(String currentState, String currentSymbol) throws SQLException {
        return getDataFromDataBase(currentState, currentSymbol, SELECT_NEXT_STATE, COLUMNNAME_NEXT_STATE);
    }

    /**
     * @param currentState  Текущее состояние машины Тьюринга.
     * @param currentSymbol Текущий символ на ленте машины Тьюринга.
     * @return Следующее состояние, в которое должна перейти машина Тьюринга.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    TuringMachine.State getNextState(String currentState, String currentSymbol) throws SQLException {
        String rawState = stringGetNextState(currentState, currentSymbol);
        switch (rawState) {
            case STATE_0: {
                return new TuringMachine.State_0();
            }
            case STATE_1: {
                return new TuringMachine.State_1();
            }
            case STATE_2: {
                return new TuringMachine.State_2();
            }
            case STATE_3: {
                return new TuringMachine.State_3();
            }
            default:
                return new TuringMachine.State_0();
        }
    }

    /**
     * Выводит описание указанного правила.
     *
     * @param currentState  Текущее состояние машины Тьюринга.
     * @param currentSymbol Текущий символ на ленте машины Тьюринга.
     * @return Описание правила, которое выполняется машиной Тьюринга в текущий момент.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    public String getDescriptionOfRule(String currentState, String currentSymbol) throws SQLException {
        if (currentState.equals("q0")) {
            return "This is default rule";
        }
        String processedDirection = "remain in place";
        if (getDirection(currentState, currentSymbol).equals("Left")) {
            processedDirection = "move to the Left";
        } else if (getDirection(currentState, currentSymbol).equals("Right")) {
            processedDirection = "move to the Right";
        }
        return "If current State is " + currentState + " and current Symbol is " + currentSymbol +
                " then Head should " + processedDirection + " and go into the State: " + stringGetNextState(currentState, currentSymbol);
    }

    /**
     * Добавляет правило интерпретации в таблицу правил в базе данных.
     *
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    void insertRule() throws SQLException {

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
        System.out.println("Enter direction of movement: Right, Left, Stay - means remaining in current place\n");
        String direction = in.next();
        PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_RULE);
        preparedStatement.setString(1, current_state);
        preparedStatement.setString(2, current_symbol);
        preparedStatement.setString(3, next_state);
        preparedStatement.setString(4, direction);
        preparedStatement.executeUpdate();
        System.out.println("Data has been successfully added\n");
    }

    /**
     * Удаляет правило из таблицы правил в базе данных.
     *
     * @param numberOfRule Номер правила, которое необходимо удалить.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    void removeRule(int numberOfRule) throws SQLException {
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_RULE);
            preparedStatement.setInt(1, numberOfRule);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Изменяет выбранное правило в таблице правил в базе данных
     *
     * @param    numberOfRule    Номер правила, которое необходимо изменить.
     * @param newCurrentState Новое значение поля current_state в базе данных.
     * @param newCurrentValue Новое значение поля current_value в базе данных.
     * @param newNextValue    Новое значение для поля next_value.
     * @param newDirection    Новое значеие для поля direction.
     * @throws SQLException Исключение, обрабатываещее ошибки с подключением к базе данных.
     */
    void changeRule(int numberOfRule, String newCurrentState, String newCurrentValue, String newNextValue, String newDirection) throws SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement(UPDATE_RULE); //null в БД, как избегать?
        preparedStatement.setString(1, newCurrentState);
        preparedStatement.setString(2, newCurrentValue);
        preparedStatement.setString(3, newNextValue);
        preparedStatement.setString(4, newDirection);
        preparedStatement.setInt(5, numberOfRule);
        preparedStatement.executeUpdate();
    }

    /**
     * Выводит на экран всю таблицу с правилами.
     */
    void showRules() throws SQLException {
        if (isConnectable) {
            connection = DriverManager.getConnection(URL, userName, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_RULES);
            String[] columnNames = {"id", "current_state", "current_symbol", "next_state", "direction"};
            for (String x : columnNames) {
                System.out.print(x + " | ");
            }
            System.out.print("\n");
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < columnNames[i].length(); j++) {
                    System.out.print("-");
                }
                System.out.print("-+-");
            }
            System.out.print("\n");
            while (resultSet.next()) {
                for (int i = 1; i <= 5; i++) {
                    System.out.print(resultSet.getString(i));
                    for (int j = 0; j < columnNames[i - 1].length() - resultSet.getString(i).length(); j++) {
                        System.out.print(" ");
                    }
                    System.out.print(" | ");
                }

                System.out.print("\n");
            }
        }
    }
}

   /*
    String userName = "root";
    String password = "9098";
    String connectionURL = "jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
   */