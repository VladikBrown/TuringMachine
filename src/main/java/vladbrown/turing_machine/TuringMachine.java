    package vladbrown.turing_machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

/*Класс TuringMachine{@code TuringMachine}, симулирующий работу абстрактной машины Тьюринга,
*включающий в себя вложенные классы Head{@code Head}, Tape{@code Tape} и Box{@code Box},
* имитирующие внутреннее строение машины Тьюринга и базовый абстрактный класс State{@code State},
* от которого наследуются дочерние классы, реализующие состояние машины Тьюринга
*
* @author Бурый Владислав
* @version 0.5
 */
public class TuringMachine {

    /*Метод Start реализует интерфейс взаимодействия с машиной Тьюринга
    *@param    args    директория текстового файла и "начальным" машины тьюринга
    */
    public void start(String args) throws FileNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            int menuIndex = 0;
            outer:
            while(true){
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
                switch (menuIndex){
                    case 1: {
                        Tape tape = new Tape(args); //args[0];
                        tape.showTape();
                        Head head = new Head(tape);
                        State current_state = new State_0(); // while current_state != null ???
                        while(current_state != null) {
                            current_state = current_state.execute_rules(head, current_state.toString());
                        }
                        tape.showTape();
                        break;
                    }
                    case 2: {
                        DataBase dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");;
                        dataBase.showRules();
                        break;
                    }
                    case 3: {
                        DataBase dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");;
                        dataBase.insertRule();
                        break;
                    }
                    case 4: {
                        DataBase dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");;
                        dataBase.showRules();
                        System.out.println("Enter number of rule to change");
                        int ruleToChange = in.nextInt();
                        System.out.println("Enter new current state. For example \"q1\" means state 1");
                        String newCurrentState = in.next();
                        System.out.println("Enter new current symbol. For example \"(\"");
                        String newCurrentSymbol = in.next();
                        System.out.println("Enter new next state. For example \"q2\" means state 2");
                        String newNextState = in.next();
                        System.out.println("Enter new direction. \"Right\" - to move head right, \"Left\" - to move head left, \"Stay\" - to not move head");
                        String newDirection = in.next();
                        dataBase.changeRule(ruleToChange, newCurrentState, newCurrentSymbol, newNextState, newDirection );
                        System.out.println("Rule was changed\n");
                        break;
                    }
                    case 5: {
                        DataBase dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");;
                        dataBase.showRules();
                        System.out.println("Enter number of rule to remove");
                        int ruleToRemove = in.nextInt();
                        dataBase.removeRule(ruleToRemove);
                        System.out.println("Rule number " + ruleToRemove + "was successfully removed");
                        break;
                    }
                    case 6: {
                        Tape tape = new Tape(args); //args[0];
                        tape.showTape();
                        break;
                    }
                    case 7: {
                        break outer;
                    }
                }
            }

     }

     //подключение к БД в конструкторе состояния

    /*Абстрактный класс State показывает поведение состояний {@see ...?}
    *
     */
    static abstract class State{

        protected DataBase dataBase;

        public State() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        }
        public abstract State execute_rules(Head head, String currentState) throws SQLException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException;

        @Override
        public abstract String toString();
    }

    /*

     */
    static class State_0 extends State{
        public State_0() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws SQLException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
            if(head.current_element == null && head.it.hasNext()){
                head.move("Right");
                return this;
            }
            if(head.it.hasNext()){
            head.move("Right");
            return new State_1();
            }
            else {
                return null;
            }

        }

        @Override
        public String toString() {
            return "q0";
        }
    }

    /*

     */
    static class State_1 extends State{

        public State_1() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            // состояния из БД брать
                String curElement = head.current_element.getValue();
                head.move(dataBase.getDirection(this.toString(), head.current_element.getValue()));

            //вернуть предыдуший элемент
                return dataBase.getNextState(this.toString(), curElement);
            //1. Действие
            //2.Направлеие
            // 3.Состояние

        }

        @Override
        public String toString() {
            return "q1";
        }
    }

    /*

     */
    static class State_2 extends State{
        public State_2() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            //действие
            String curElement = head.current_element.getValue();
            head.move(dataBase.getDirection(this.toString(), head.current_element.getValue()));
            return dataBase.getNextState(this.toString(),curElement);
        }

        @Override
        public String toString() {
            return "q2";
        }
    }

    /*

     */
    static class State_3 extends State{
        public State_3() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        }

        private void specialRules(Head head){
            head.clear();
            head.move("Right");
            head.move("Right");
            head.clear();
            head.move("Right");
        }
        @Override
        public State execute_rules(Head head, String currentState) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            specialRules(head);
            return dataBase.getNextState(this.toString(), head.current_element.getValue());
        }

        @Override
        public String toString() {
            return "q3";
        }
    }

    /*

     */
    private class Box {

        private String symbol;

        public Box(String symbol) {
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
            return "" + symbol;
        }
    }

    /*

     */
    private class Tape {

        private LinkedList<Box> tape;
        private ListIterator<Box> it;

        public Tape(){
            this.tape = new LinkedList<>();
            this.it = tape.listIterator(0);
        }

        public Tape(String path) {
            // сделать заполнение из файла
            this.tape = new LinkedList<>();
            this.it = tape.listIterator(0);
            File file;
            Scanner scanner = null;
            String raw_tape = "";
            try {
                file = new File(path);
                scanner = new Scanner(file);

                while(scanner.hasNext()){
                    raw_tape = scanner.nextLine();
                }

                for (String x: raw_tape.split("", raw_tape.length())) {
                    this.tape.add(new Box(x));
                }
            }
            catch (FileNotFoundException e){
                System.err.println(e);
            }
            finally {
            if (scanner != null) {
                scanner.close();
            }
        }
            //сброс итератора для работы машины, во избежение исключения конкурентыных модификаций
            this.it = tape.listIterator(0);
        }
    public void showTape(){
        for (Box x: this.tape) {
            System.out.print(x);
        }
        System.out.println("\n");
    }
    }

    /*

     */
    private class Head {

        private ListIterator<Box> it;
        private Box current_element;

        public Head(Tape object_Tape)
        {
            this.it = object_Tape.it;
        }

        private void move(String direction)
        {
            if(direction.equals("Left"))
            {
                if(this.it.hasPrevious()) {
                    this.current_element = this.it.previous();
                    this.current_element = this.it.previous();
                }
                else System.out.println("Head in the extreme left position!");
            }
            if(direction.equals("Right"))
            {
                if(this.it.hasNext()) {
                    this.current_element = this.it.next();
                }
                else System.out.println("Head in the extreme right position!");
            }

            if(direction.equals("Stay"))
            {
                System.out.println("Head didn't move!\n");
            }
        }

        private void print() {


            System.out.print(this.it);
        }


        private void change(String new_symbol){
            this.it.set(new Box(new_symbol));
                    //спросить про утечку памяти
        }

        private void clear(){

            this.it.set(new Box("."));
        }

        private String current_value(){
            return this.current_element.getValue();
        }
    }
}