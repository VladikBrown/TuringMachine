package vladbrown.turing_machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;


public class TuringMachine {


    public void start(String args) throws FileNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        Tape tape = new Tape(args); //args[0];
        Head head = new Head(tape);
        State current_state = new State_0(); // while current_state != null ???
        while(current_state != null) {
            current_state = current_state.execute_rules(head, current_state.toString());
            tape.showTape();
        }
     }

     //подключение к БД в конструкторе состояния
    static abstract class State{

        protected DataBase dataBase;

        public State() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            dataBase = new DataBase("jdbc:mysql://localhost:3306/turing_machine_rules?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "9098");
        }
        public abstract State execute_rules(Head head, String currentState) throws SQLException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException;

        @Override
        public abstract String toString();
    }

    static class State_0 extends State{
        public State_0() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws SQLException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
            if(head.it.hasNext()){  head.move("Right");//(dataBase.getDirection(currentState, head.current_element.symbol));
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

    //через switch
    static class State_1 extends State{

        public State_1() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            // состояния из БД брать
                head.move(dataBase.getDirection(this.toString(), head.current_element.symbol));
                return dataBase.getNextState(this.toString(), head.current_element.symbol);
            //1. Действие
            //2.Направлеие
            // 3.Состояние

        }

        @Override
        public String toString() {
            return "q1";
        }
    }

    static class State_2 extends State{
        public State_2() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            super();
        }

        @Override
        public State execute_rules(Head head, String currentState) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            //действие
            head.move(dataBase.getDirection(this.toString(), head.current_element.symbol));
            return dataBase.getNextState(this.toString(), head.current_element.symbol);
        }

        @Override
        public String toString() {
            return "q2";
        }
    }

    static class State_3 extends State{
        public State_3() throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        }

        private void specialRules(Head head){
            head.clear();
            head.move("Right");
            head.clear();
            head.move("Right");
        }
        @Override
        public State execute_rules(Head head, String currentState) throws NoSuchMethodException, IllegalAccessException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
            specialRules(head);
            return dataBase.getNextState(this.toString(), head.current_element.symbol);
        }

        @Override
        public String toString() {
            return "q3";
        }
    }

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
            char [] raw_tape = new char[0];
            try {
                file = new File(path);
                scanner = new Scanner(file);
                //  логика чтения из файла
                while(scanner.hasNext()){
                    raw_tape = scanner.nextLine().toCharArray();
                }
                for (char x: raw_tape) {
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
            //сброс итератора для работы машины, во избяжения исключения конкурентыных модификаций
            this.it = tape.listIterator(0);
        }
    public void showTape(){
        for (Box x: this.tape) {
            System.out.print(x);
        }
        System.out.println("\n");
    }
    }




    private class Head {

        private ListIterator<Box> it;
        private Box current_element;

        public Head(Tape object_Tape)
        {
            this.it = object_Tape.it;
        }

        private void move(String direction)
        {
            if(direction == "L")
            {
                if(this.it.hasPrevious()) {
                    this.current_element = this.it.previous();
                }
                else System.out.println("Каретка в крайней левой позиции!");
            }
            if(direction == "R")
            {
                if(this.it.hasNext()) {
                    this.current_element = this.it.next();
                }
                else System.out.println("Каретка в крайней правой позиции!");
            }

            if(direction != "R" && direction != "L")
            {
                System.out.println("Каретка не передвинулась\n");
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

