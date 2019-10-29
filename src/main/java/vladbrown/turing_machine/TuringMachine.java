package vladbrown.turing_machine;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Класс {@link  TuringMachine}
 * симулирует работу абстрактной машины Тьюринга,
 * включает в себя вложенные классы {@link Head}, {@link Tape} и {@link Box},
 * имитирующие внутреннее строение машины Тьюринга и базовый абстрактный класс {@link State},
 * от которого наследуются дочерние классы, реализующие состояния машины Тьюринга
 *
 * @author Бурый Владислав
 * @version 1.1
 */
public class TuringMachine {

    private static DataBase dataBase;
    private boolean wasLogGiven = false;
    private String textFolder;

    /**
     * Конуструктор класса {@link TuringMachine} создает объект класса {@link DataBase}
     * инциализирует private поля textFolder и wasLogGiven параметрами,
     * переданными программе при консольном запуске приложения.
     *
     * @param args Аргументы командной строки, первым из которых должен быть путь к файлу с начальным машины тьюринга, а вторым специальная команда
     * @see DataBase
     */
    public TuringMachine(String[] args) throws ClassNotFoundException {
        dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        this.textFolder = args[0];
        if (args[1].equals("-log")) {
            this.wasLogGiven = true;
        }
    }

    /**
     * Метод Start реализует интерфейс взаимодействия с машиной Тьюринга
     */
    public void start() throws SQLException {
        int menuIndex;
        outer:
        while (true) {
            System.out.println(
                    "1 - start machine\n" +
                            "2 - view rules\n" +
                            "3 - add rule\n" +
                            "4 - change rule\n" +
                            "5 - delete rule\n" +
                            "6 - show tape\n" +
                            "7 - exit\n"
            );
            Scanner in = new Scanner(System.in);
            menuIndex = in.nextInt();
            switch (menuIndex) {
                case 1: {
                    Tape tape = new Tape(textFolder);
                    tape.showTape();
                    Head head = new Head(tape);
                    State currentState = new State_0();
                    while (currentState != null) {
                        if (this.wasLogGiven) {
                            System.out.println(currentState.toString());
                        }
                        currentState = currentState.executeRules(head, currentState.toString());
                    }
                    tape.showTape();
                    break;
                }
                case 2: {
                    dataBase.showRules();
                    break;
                }
                case 3: {
                    dataBase.insertRule();
                    break;
                }
                case 4: {
                    dataBase.showRules();
                    System.out.println("Enter number of rule that must be changed");
                    int numberOfRule = in.nextInt();
                    System.out.println("Enter new current state. For example \"q1\" means state 1");
                    String newCurrentState = in.next();
                    System.out.println("Enter new current symbol. For example \"(\"");
                    String newCurrentSymbol = in.next();
                    System.out.println("Enter new next state. For example \"q2\" means state 2");
                    String newNextState = in.next();
                    System.out.println("Enter new direction. \"Right\" - to move head right, \"Left\" - to move head left, \"Stay\" - to not move head");
                    String newDirection = in.next();
                    dataBase.changeRule(numberOfRule, newCurrentState, newCurrentSymbol, newNextState, newDirection);
                    System.out.println("Rule was changed\n");
                    break;
                }
                case 5: {
                    dataBase.showRules();
                    System.out.println("Enter number of rule that must be changed");
                    int numberOfRule = in.nextInt();
                    dataBase.removeRule(numberOfRule);
                    break;
                }
                case 6: {
                    Tape tape = new Tape(textFolder);
                    tape.showTape();
                    break;
                }
                case 7: {
                    break outer;
                }
            }
        }
    }

    /**
     * Абстрактный класс {@link State} показывает поведение состояний;
     * {@link State_0},
     * {@link State_1},
     * {@link State_2},
     * {@link State_3},
     * и является базовым для всех состояний
     */
    static abstract class State {

        /**
         * Метод execute_rules выполняет необходимые действия для данного состояния:
         * 1. Манипуляции с данными головной ячейки (изменение, удаление, игнорирование).
         * 2. Перемещение каретки, исходя из правил интерпретации машины Тьюринга.
         * 3. Переход в следующее состояние, исходя из правил интерпретации машины Тьюринга.
         *
         * @param head         объект класса Head {@code ...} для взаимодействия с лентой
         * @param currentState текущее состояние машины Тьюринга
         * @return {@code State}   следующее состояние машины Тьюринга, исходя из правил интерпретации машины Тьюринга
         */
        public abstract State executeRules(Head head, String currentState) throws SQLException;

        /**
         * @return текущее состояние в строковом формате для взаимодействия с базой данный
         */
        @Override
        public abstract String toString();
    }

    /**
     * {@link State}
     *
     * @see vladbrown.turing_machine.TuringMachine.State
     */
    static class State_0 extends State {

        /**
         * @see State#executeRules(Head, String)
         */
        @Override
        public State executeRules(Head head, String currentState) {
            if (head.currentElement == null && head.it.hasNext()) {
                head.move("Right");
                return this;
            }
            if (head.currentElement != null) {
                if (head.currentElement.getValue().equals("A") && head.it.hasNext()) {
                    head.move("Right");
                    return new State_1();
                } else {
                    return null;
                }
            } else {
                System.out.println("Null founded in Tape");
                return null;
            }
        }

        /**
         * @return Текущее состояние в строковом формате для взаимодействия с базой данный.
         */
        @Override
        public String toString() {
            return "q0";
        }
    }

    /**
     * {@link State}
     *
     * @see vladbrown.turing_machine.TuringMachine.State
     */
    static class State_1 extends State {

        /**
         * @see State#executeRules(Head, String)
         */
        @Override
        public State executeRules(Head head, String currentState) throws SQLException {
            String currentElement = head.currentElement.getValue();
            head.move(dataBase.getDirection(this.toString(), head.currentElement.getValue()));

            return dataBase.getNextState(this.toString(), currentElement);
        }

        /**
         * @return Текущее состояние в строковом формате для взаимодействия с базой данный.
         */
        @Override
        public String toString() {
            return "q1";
        }
    }

    /**
     * {@link State}
     *
     * @see vladbrown.turing_machine.TuringMachine.State
     */
    static class State_2 extends State {

        /**
         * @see State#executeRules(Head, String)
         */
        @Override
        public State executeRules(Head head, String currentState) throws SQLException {
            String currentElement = head.currentElement.getValue();
            head.move(dataBase.getDirection(this.toString(), head.currentElement.getValue()));
            return dataBase.getNextState(this.toString(), currentElement);
        }

        /**
         * @return Текущее состояние в строковом формате для взаимодействия с базой данный.
         */
        @Override
        public String toString() {
            return "q2";
        }
    }

    /**
     * {@link State}
     *
     * @see vladbrown.turing_machine.TuringMachine.State
     */
    static class State_3 extends State {

        /**
         * Метод для манипуляции с данными текущей ячейки.
         */
        private void specialRules(Head head) {
            head.clear();
            head.move("Right");
            head.move("Right");
            head.clear();
            head.move("Right");
        }

        /**
         * @see State#executeRules(Head, String)
         */
        @Override
        public State executeRules(Head head, String currentState) throws SQLException {
            specialRules(head);
            return dataBase.getNextState(this.toString(), head.currentElement.getValue());
        }

        /**
         * @return Текущее состояние в строковом формате для взаимодействия с базой данный.
         */
        @Override
        public String toString() {
            return "q3";
        }
    }

    /**
     * Класс {@link Box} - это ячейка "ленты" {@link Tape}, в которой хранятся данные.
     * Содержит поле symbol для хранения значение ячейки.
     */
    static class Box {

        private String symbol;

        /**
         * Конструктор класса {@link Box} инициализирует поле symbol строковым значением.
         *
         * @param symbol Значение, которым инициализируется поле класса {@link Box}
         */
        Box(String symbol) {
            this.symbol = symbol;
        }

        public String getValue() {
            return this.symbol;
        }

        public void setValue(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Класс Tape {@link Tape} - это "лента" машины Тьюринга, по которой перемещается каретка {@link Head}.
     * Содержит поле tape - список объектов класса {@link Box} и поле
     * it - интератор, перемещающийся по "ленте", передаваемый в другие классы для сохранения позиции каретки.
     */
    static class Tape {

        private LinkedList<Box> tape;
        private ListIterator<Box> it;

        /**
         * Конструктор класса {@link Tape} создает "ленту" и заполняет её значениями, которые прописаны в текстовом файле
         *
         * @param path Директория текстового файла с "начальным" машины Тьюринга.
         */
        Tape(String path) {
            this.tape = new LinkedList<>();
            this.it = tape.listIterator(0);
            File file;
            Scanner scanner = null;
            String rawTape = "";
            try {
                file = new File(path);
                scanner = new Scanner(file);

                while (scanner.hasNext()) {
                    rawTape = scanner.nextLine();
                }

                for (String x : rawTape.split("", rawTape.length())) {
                    this.tape.add(new Box(x));
                }
            } catch (FileNotFoundException e) {
                System.err.println(e);
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
            this.it = tape.listIterator(0);
        }


        /**
         * Выводит на экран "ленту."
         */
        void showTape() {
            for (Box x : this.tape) {
                System.out.print(x);
            }
            System.out.println("\n");
        }
    }

    /**
     * Класс {@link Head} это "каретка" машины Тьюринга.
     * Содержит поле it, которое хранит положение "каретки", поле current_element, для доступа к значению текущей ячейки
     * и методы, для перемещения "каретки" и манипуляции данными.
     */
    static class Head {

        private ListIterator<Box> it;
        private Box currentElement;
        private Tape currentTape;

        /**
         * Конструктор класса Head{@code Head}
         *
         * @param objectTape Объект "ленты" с которой должна взаимодействовать каретка.
         */
        Head(Tape objectTape) {
            this.currentTape = objectTape;
            this.it = objectTape.it;
        }

        /**
         * Перемещает каретку на одно положение влево или вправо(или оставляет на месте).
         *
         * @param direction Направление по которому необходимо переместить каретку
         */
        void move(String direction) {
            if ("error".equals(direction)) {
                System.out.println("Program hasn't enough instructions to work correctly and will be stopped!");
                return;
            }
            if ("Left".equals(direction)) {
                if (this.it.hasPrevious()) {
                    this.currentElement = this.it.previous();
                    this.currentElement = this.it.previous();
                } else System.out.println("Head in the extreme left position!");
            }

            if ("Right".equals(direction)) {
                if (this.it.hasNext()) {
                    this.currentElement = this.it.next();
                } else System.out.println("Head in the extreme right position!");
            }

            if ("Stay".equals(direction)) {
                System.out.println("Head didn't move!\n");
            }
        }

        /**
         * Заменяет значение текущей ячейки на новое, переданно как параметр метода.
         *
         * @param newSymbol новое значение ячейки, на которое заменяется текущее
         */
        void change(String newSymbol) { this.it.set(new Box(newSymbol));
        }

        /**
         * Зачищает текущую ячейку.
         */
        void clear() {
            change(".");
        }

        /**
         * @return Значение текущей ячейки.
         */
        public String getCurrentValue() {
            return this.currentElement.getValue();
        }

        /**
         * Возвращает каретку в начальную позициую и обновляет значения ленты.
         * Этот метод должен быть вызван, если вы собираетесь работать с лентой после изменения её значений.
         */
        void updateTape() {
            this.it = currentTape.tape.listIterator(0);
        }
    }
}