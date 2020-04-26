package solver;

import dataHelper.IllegalGridStateException;
import decoder.GridDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

class BacktrackingTest {

    @Test
    void step() {
        var impossible = "{\"numberCells\":[\n" +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":6},\n" +
                "{\"key\":{\"y\":0,\"x\":1},\"value\":8},\n" +
                "{\"key\":{\"y\":0,\"x\":2},\"value\":6},\n" +
                "{\"key\":{\"y\":0,\"x\":3},\"value\":8},\n" +
                "{\"key\":{\"y\":1,\"x\":0},\"value\":6},\n" +
                "{\"key\":{\"y\":1,\"x\":1},\"value\":16},\n" +
                "],\"lenX\":4,\"lenY\":4}";
        var possible = "{\"numberCells\":[\n" +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":1},\n" +
                "{\"key\":{\"y\":1,\"x\":0},\"value\":2},\n" +
                "{\"key\":{\"y\":3,\"x\":0},\"value\":4},\n" +
                "{\"key\":{\"y\":5,\"x\":0},\"value\":9},\n" +
                "{\"key\":{\"y\":6,\"x\":0},\"value\":3},\n" +
                "{\"key\":{\"y\":0,\"x\":2},\"value\":3},\n" +
                "{\"key\":{\"y\":1,\"x\":2},\"value\":4},\n" +
                "{\"key\":{\"y\":3,\"x\":2},\"value\":2},\n" +
                "{\"key\":{\"y\":5,\"x\":2},\"value\":5},\n" +
                "{\"key\":{\"y\":6,\"x\":2},\"value\":4},\n" +
                "{\"key\":{\"y\":0,\"x\":4},\"value\":3},\n" +
                "{\"key\":{\"y\":1,\"x\":4},\"value\":4},\n" +
                "{\"key\":{\"y\":3,\"x\":4},\"value\":8},\n" +
                "{\"key\":{\"y\":5,\"x\":4},\"value\":7},\n" +
                "{\"key\":{\"y\":6,\"x\":4},\"value\":8},\n" +
                "{\"key\":{\"y\":0,\"x\":6},\"value\":5},\n" +
                "{\"key\":{\"y\":1,\"x\":6},\"value\":6},\n" +
                "{\"key\":{\"y\":3,\"x\":6},\"value\":6},\n" +
                "{\"key\":{\"y\":5,\"x\":6},\"value\":6},\n" +
                "{\"key\":{\"y\":6,\"x\":6},\"value\":2},\n" +
                "],\"lenX\":7,\"lenY\":7}";
        Reader targetReader = new StringReader(impossible);
        var impossibleGrid = GridDecoder.getJsonGrid(targetReader);
        var unsuccesfull = new Backtracking(impossibleGrid);
        Assertions.assertThrows(IllegalGridStateException.class, ()-> solve(unsuccesfull));
        targetReader = new StringReader(possible);
        var possibleGrid = GridDecoder.getJsonGrid(targetReader);
        var successfull = new Backtracking(possibleGrid);
        Assertions.assertDoesNotThrow(()->solve(successfull));
    }

    private void solve(Backtracking solver) {
        var solved = false;
        while (!solved) {
            solved = solver.Step();
        }
    }
}