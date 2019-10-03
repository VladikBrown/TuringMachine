import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;


public class TuringMachine {


    public void start(String args) throws FileNotFoundException {
        Tape tape = new Tape(args); //args[0];
        Head head = new Head(tape);
        State current_state = new State_0(); // while current_state != null ???
        while(current_state != null) {
            current_state = current_state.execute_rules(head);
            tape.showTape();
        }
     }

    private abstract class State{
        public abstract State execute_rules(Head head);
        @Override
        public abstract String toString();
    }

    private class State_0 extends State{
        @Override
        public State execute_rules(Head head) {
            if(head.it.hasNext()){  head.move("R");
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

    private class State_1 extends State{

        @Override
        public State execute_rules(Head head) {
            // состояния из БД брать
            if(head.current_value() == 'A'){
                return new State_0();
            }
            if(head.current_value() == '('){
                head.move("R");
                return new State_2();
            } else if(head.current_value() == ')'){
                head.move("R");
                return new State_1();
            }
            return null;
        }

        @Override
        public String toString() {
            return "q1";
        }
    }

    private class State_2 extends State{
        @Override
        public State execute_rules(Head head) {
           if(head.current_value() == 'A'){
               return new State_0();
           }
           if(head.current_value() == '('){
               head.move("R");
               return new State_2();
           }
           else if(head.current_value() == ')'){
               head.move("L");
               return new State_3();
           }
           return null;
        }

        @Override
        public String toString() {
            return "q2";
        }
    }

    private class State_3 extends State{
        @Override
        public State execute_rules(Head head) {
            head.clear();
            head.move("L");
            head.clear();
            head.move("R");
            head.move("R");
            head.move("R");
            return new State_1();
        }

        @Override
        public String toString() {
            return "q3";
        }
    }

    private class Box {

        private char symbol;

        public Box(char symbol) {
            this.symbol = symbol;

        }

        public char getValue() {
            return this.symbol;

        }

        public void setValue(char symbol) {
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


        private void change(char new_symbol){
            this.it.set(new Box(new_symbol));
                    //спросить про утечку памяти
        }

        private void clear(){

            this.it.set(new Box('.'));
        }

        private char current_value(){
            return this.current_element.getValue();
        }


    }

}

