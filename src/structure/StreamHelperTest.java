package structure;

import dataHelper.Point;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StreamHelperTest {
    @Test
    void getNumberCells() {
        var cells = getCells();
        assertEquals(2, StreamHelper.getNumberCells(cells).size());
    }

    private List<Cell> getCells() {
        var cells = new ArrayList<Cell>(3);
        cells.add(new Cell(0, 0, new Group(1), 2));
        cells.add(new Cell(1, 0, null, null));
        cells.add(new Cell(2, 2, null, 25));
        return cells;
    }

    @Test
    void getMinMaxNumbers() {
        var cells = getCells();
        var minmax = StreamHelper.getMinMaxNumbers(cells);
        assertEquals(2, minmax.getKey());
        assertEquals(25, minmax.getValue());
    }

    @Test
    void getNotInGroups() {
        var cells = getCells();
        assertEquals(2, StreamHelper.getNotInGroups(cells).size());
    }

    @Test
    void getPoints() {
        var cells = getCells();
        var points = StreamHelper.getPoints(cells);
        assertEquals(3, points.size());
        assertTrue(points.contains(new Point(0, 0)));
        assertTrue(points.contains(new Point(1, 0)));
        assertTrue(points.contains(new Point(2, 2)));
    }

    @Test
    void getCopy() {
        var cells = getCells();
        var cpys = StreamHelper.getCopy(cells);
        assertEquals(3, cpys.size());
        cpys.get(0).setNumber(3);
        assertNotEquals(3, cells.get(0));
    }

    @Test
    void getFlatMap() {
        var cells = getCells();
        var ll = new ArrayList<List<Cell>>(1);
        ll.add(cells);
        var flatList = StreamHelper.getFlatMap(ll);
        for (var c : cells) {
            assertTrue(flatList.contains(c));
        }
    }

    @Test
    void hasNull() {
        var l1 = Arrays.asList(1, 2, 3, null, 5);
        var l2 = Arrays.asList(1, 2, 3, 4, 5);
        assertTrue(() -> StreamHelper.hasNull(l1));
        assertFalse(() -> StreamHelper.hasNull(l2));
    }

    @Test
    void distinctByKey() {
        List<Point> personListFiltered = new ArrayList<>(Arrays.asList(
                new Point(1, 2),
                new Point(2, 2),
                new Point(3, 2),
                new Point(1, 1),
                new Point(1, 3))).stream()
                .filter(StreamHelper.distinctByKey(Point::getX))
                .collect(Collectors.toList());
        assertEquals(3, personListFiltered.size());
        assertTrue(personListFiltered.contains(new Point(2, 2)));
        assertTrue(personListFiltered.contains(new Point(3, 2)));
        assertEquals(1, personListFiltered.stream().filter(p -> p.getX() == 1).count());
    }
}