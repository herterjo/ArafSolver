package decoder;

import org.junit.jupiter.api.Test;
import structure.Group;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class GridDecoderTest {

    @Test
    void getJsonGrid() throws IOException {
        var jsonString = "{\"numberCells\":[" +
                "{\"key\":{\"y\":1,\"x\":0},\"value\":1}," +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":5}," +
                "],\"lenX\":2,\"lenY\":2}";
        Reader targetReader = new StringReader(jsonString);
        var grid = GridDecoder.getJsonGrid(targetReader);
        targetReader.close();
        var cells = grid.getCellsCpy();
        assertTrue(cells[0][0].isNumberCell());
        assertTrue(cells[1][0].isNumberCell());
        assertFalse(cells[0][1].isNumberCell());
        assertFalse(cells[1][1].isNumberCell());
        var g1 = new Group(1);
        grid.setCellGroup(0, 0, g1);
        grid.setCellGroup(0, 1, g1);
        grid.setCellGroup(1, 1, g1);
        grid.setCellGroup(1, 0, g1);
        assertDoesNotThrow(grid::validateAll);
        var stateExClass = IllegalStateException.class;
        //Multiple entries for same coord
        assertThrows(stateExClass, () -> GridDecoder.getJsonGrid(new StringReader("{\"numberCells\":[" +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":2}," +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":4}" +
                "],\"lenX\":2,\"lenY\":2}")));
        //only one numbercell
        assertThrows(stateExClass, () -> GridDecoder.getJsonGrid(new StringReader("{\"numberCells\":[" +
                "{\"key\":{\"y\":0,\"x\":2},\"value\":2}" +
                "],\"lenX\":2,\"lenY\":2}")));
        //0 length
        assertThrows(stateExClass, () -> GridDecoder.getJsonGrid(new StringReader("{\"numberCells\":[" +
                "{\"key\":{\"y\":0,\"x\":0},\"value\":2}," +
                "{\"key\":{\"y\":0,\"x\":1},\"value\":4}" +
                "],\"lenX\":0,\"lenY\":2}")));
        //wrong number
        assertThrows(IllegalArgumentException.class, () -> GridDecoder.getJsonGrid(new StringReader(
                "{\"numberCells\":[" +
                        "{\"key\":{\"y\":0,\"x\":0},\"value\":0}," +
                        "{\"key\":{\"y\":0,\"x\":1},\"value\":4}" +
                        "],\"lenX\":2,\"lenY\":2}")));
    }
}