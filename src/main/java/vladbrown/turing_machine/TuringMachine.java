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
    private static DataBase dataBase;
    private boolean wasLogGiven = false;
    private String textFolder;

    public TuringMachine(String[] args) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        DataBase dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        this.textFolder = args[0];
        if(args[1].equals("-log")){ this.wasLogGiven = true;}
    }

    /*Метод Start реализует интерфейс взаимодействия с машиной Тьюринга
    *@param    args    директория текстового файла и "начальным" машины тьюринга
    *
    */

    public void start(String textFolder) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            int menuIndex;
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
                        Tape tape = new Tape(textFolder);
                        tape.showTape();
                        Head head = new Head(tape);
                        State current_state = new State_0();
                        while(current_state != null) {
                            if(this.wasLogGiven){
                                System.out.println(current_state.toString());
                            }
                            current_state = current_state.execute_rules(head, current_state.toString());
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
                        dataBase.showRules();
                        System.out.println("Enter number of rule to remove");
                        int ruleToRemove = in.nextInt();
                        dataBase.removeRule(ruleToRemove);
                        System.out.println("Rule number " + ruleToRemove + "was successfully removed");
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

     //подключение к БД в конструкторе состояния

    /*Абстрактный класс State показывает поведение состояний {@see ...?}
    *
     */
    static abstract class State{

        public abstract State execute_rules(Head head, String currentState) throws SQLException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException;

        @Override
        public abstract String toString();
    }

    /*

     */
    static class State_0 extends State{

        @Override
        public State execute_rules(Head head, String currentState) {
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

        @Override
        public State execute_rules(Head head, String currentState) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
                String currentElement = head.current_element.getValue();
                head.move(dataBase.getDirection(this.toString(), head.current_element.getValue()));

                return dataBase.getNextState(this.toString(), currentElement);
        }

        @Override
        public String toString() {
            return "q1";
        }
    }

    /*

     */
    static class State_2 extends State{

        @Override
        public State execute_rules(Head head, String currentState) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            String currentElement = head.current_element.getValue();
            head.move(dataBase.getDirection(this.toString(), head.current_element.getValue()));
            return dataBase.getNextState(this.toString(),currentElement);
        }

        @Override
        public String toString() {
            return "q2";
        }
    }

    /*

     */
    static class State_3 extends State{

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
    private static class Box {

        private String symbol;

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
            return "" + symbol;
        }
    }

    /*

     */
    private class Tape {

        private LinkedList<Box> tape;
        private ListIterator<Box> it;

        Tape(String path) {
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

        Head(Tape object_Tape)
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

        private void change(String new_symbol){
            this.it.set(new Box(new_symbol));
        }

        private void clear(){
            this.it.set(new Box("."));
        }

        private String current_value(){
            return this.current_element.getValue();
        }
    }
}