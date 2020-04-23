package structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    @Test
    void posTest() {
        var argExceptClass = IllegalArgumentException.class;
        assertDoesNotThrow(() -> new Cell(0, 0, null, null));
        assertDoesNotThrow(() -> new Cell(1, 1, null, null));
        assertThrows(argExceptClass, () -> new Cell(-1, 0, null, null));
        assertThrows(argExceptClass, () -> new Cell(0, -1, null, null));
        assertThrows(argExceptClass, () -> new Cell(-1, -1, null, null));
    }

    @Test
    void numberTest() {
        var argExceptClass = IllegalArgumentException.class;
        var cell = new Cell(0, 0, null, null);
        assertFalse(cell.isNumberCell());
        assertThrows(argExceptClass, () -> cell.setNumber(-1));
        assertThrows(argExceptClass, () -> cell.setNumber(0));
        assertDoesNotThrow(() -> cell.setNumber(1));
        assertDoesNotThrow(() -> cell.setNumber(2));
        assertEquals(2, (int) cell.getNumber());
        assertTrue(cell.isNumberCell());
    }

}