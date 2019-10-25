package vladbrown.turing_machine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


import static org.junit.jupiter.api.Assertions.*;

class TuringMachineTest {

    @Nested
    class BoxTesting {
        @Test
        public void BoxConstructorTesting() {
            TuringMachine.Box box = new TuringMachine.Box("test constructor");
            assertEquals("test constructor", box.toString());
        }

        @Test
        public void getValueTesting() {
            TuringMachine.Box box = new TuringMachine.Box("test symbol");
            assertEquals("test symbol", box.getValue());
        }

        @Test
        public void setValueTesting() {
            TuringMachine.Box box = new TuringMachine.Box("wrong symbol");
            box.setValue("right symbol");
            assertEquals("right symbol", box.getValue());
        }
    }

    @Nested
    class TapeAndHeadTesting {

        public void prepareTestTapeAndHead() {
            File file = new File("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\test\\java\\vladbrown\\turing_machine\\tapeTest.txt");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                PrintWriter out = new PrintWriter(file.getAbsoluteFile());

                try {
                    out.print("12345");

                } finally {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void finishTestTapeAndHead() {
            File file = new File("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\test\\java\\vladbrown\\turing_machine\\tapeTest.txt");
            if (file.exists()) {
                file.delete();
            }
        }

        @Test
        public void TapeAndHeadConstructorTesting() {
            //также был протестирован метод Head.move() и Head.getValue();
            prepareTestTapeAndHead();
            TuringMachine.Tape tape = new TuringMachine.Tape("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\test\\java\\vladbrown\\turing_machine\\tapeTest.txt");
            TuringMachine.Head head = new TuringMachine.Head(tape);
            for (int i = 0; i < 5; i++) {
                head.move("Right");
                assertEquals(i + 1 + "", head.getCurrentValue());
            }
            finishTestTapeAndHead();
        }

        @Test
        public void changeTest(){
            prepareTestTapeAndHead();
            TuringMachine.Tape tape = new TuringMachine.Tape("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\test\\java\\vladbrown\\turing_machine\\tapeTest.txt");
            TuringMachine.Head head = new TuringMachine.Head(tape);
            for (int i = 0; i < 5; i++) {
                head.move("Right");
                head.change("changed");
            }
            head.updateTape();
            for (int i = 0; i < 5; i++) {
                head.move("Right");
                assertEquals("changed", head.getCurrentValue());
            }
            finishTestTapeAndHead();
        }

        @Test
        public void clearTest(){
            prepareTestTapeAndHead();
            TuringMachine.Tape tape = new TuringMachine.Tape("C:\\JAVA_Library\\2_sem\\PIVAS\\LABA_1\\src\\test\\java\\vladbrown\\turing_machine\\tapeTest.txt");
            TuringMachine.Head head = new TuringMachine.Head(tape);
            for (int i = 0; i < 5; i++) {
                head.move("Right");
                head.clear();
            }
            head.updateTape();
            for (int i = 0; i < 5; i++) {
                head.move("Right");
                assertEquals(".", head.getCurrentValue());
            }
            finishTestTapeAndHead();
        }
    }
}
