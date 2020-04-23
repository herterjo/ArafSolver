package structure;

import dataHelper.Point;
import dataHelper.Tuple;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    public static final int gridLenX = 3;
    public static final int gridLenY = 4;

    public static Grid getGroupGrid() {
        var grid = new Grid(gridLenX, gridLenY);
        var group1 = getGroup1();
        var group2 = getGroup2();
        var group3 = getGroup3();
        grid.setCellGroup(0, 0, group3);
        grid.setCellGroup(0, 1, group1);
        grid.setCellGroup(0, 2, group1);
        grid.setCellGroup(0, 3, group1);
        grid.setCellGroup(1, 0, group3);
        grid.setCellGroup(1, 1, group2);
        grid.setCellGroup(1, 2, group2);
        grid.setCellGroup(1, 3, group1);
        grid.setCellGroup(2, 0, group3);
        grid.setCellGroup(2, 1, group2);
        grid.setCellGroup(2, 2, group2);
        grid.setCellGroup(2, 3, group1);
        return grid;
    }

    public static Grid getValidGrid() {
        var grid = getGroupGrid();
        grid.setNumberCell(0, 0, 4);
        grid.setNumberCell(2, 0, 2);
        grid.setNumberCell(0, 1, 2);
        grid.setNumberCell(0, 2, 7);
        grid.setNumberCell(1, 1, 27);
        grid.setNumberCell(1, 2, 2);
        return grid;
    }

    public static Group getGroup1() {
        return new Group(1);
    }

    public static Group getGroup2() {
        return new Group(2);
    }

    public static Group getGroup3() {
        return new Group(3);
    }

    public static Grid getInValidGrid() {
        var grid = GridTest.getGroupGrid();
        grid.setNumberCell(0, 0, 2);
        grid.setNumberCell(2, 0, 3);
        grid.setNumberCell(0, 1, 2);
        grid.setNumberCell(0, 2, 4);
        grid.setNumberCell(1, 1, 12);
        grid.setNumberCell(1, 2, 27);
        return grid;
    }

    @Test
    void lenTest() {
        var argExceptClass = IllegalArgumentException.class;
        assertDoesNotThrow(() -> new Grid(1, 1));
        assertThrows(argExceptClass, () -> new Grid(0, 1));
        assertThrows(argExceptClass, () -> new Grid(1, 0));
        assertThrows(argExceptClass, () -> new Grid(0, 0));
        assertThrows(argExceptClass, () -> new Grid(-1, -1));
    }

    @Test
    void cellsInGroupTest() {
        var lenX = 2;
        var lenY = 3;
        var grid = new Grid(lenX, lenY);
        var validation = grid.isAllCellsInGroups();
        assertFalse(validation.getKey());
        assertEquals(validation.getValue().size(), lenX * lenY);
        assertEquals(0, grid.getGroupCount());
        var group1 = getGroup1();
        var group2 = getGroup2();
        grid.setCellGroup(0, 0, group1);
        validation = grid.isAllCellsInGroups();
        assertFalse(validation.getKey());
        assertEquals(lenX * lenY - 1, validation.getValue().size());
        assertEquals(1, grid.getGroupCount());
        grid.setCellGroup(0, 1, group1);
        grid.setCellGroup(0, 2, group1);
        grid.setCellGroup(1, 0, group1);
        grid.setCellGroup(1, 1, group1);
        grid.setCellGroup(1, 2, group1);
        validation = grid.isAllCellsInGroups();
        assertTrue(validation.getKey());
        assertNull(validation.getValue());
        grid.setCellGroup(1, 2, group2);
        validation = grid.isAllCellsInGroups();
        assertTrue(validation.getKey());
        assertNull(validation.getValue());
    }

    @Test
    void groupCopyTest() {
        var grid = getValidGrid();
        var groups = grid.getGroupMapCopy();
        assertEquals(3, groups.size());
        var group1 = getGroup1();
        var group2 = getGroup2();
        var gr1Memebers = groups.get(group1);
        gr1Memebers.get(0).setGroup(group2);
        assertEquals(gr1Memebers.size(), grid.getGroupMapCopy().get(group1).size());
        grid.deleteCellFromGroup(0, 1);
        assertEquals(gr1Memebers.size() - 1, grid.getGroupMapCopy().get(group1).size());
    }

    @Test
    void setDeleteGroupTest() {
        var grid = new Grid(2, 2);
        var g1 = getGroup1();
        assertThrows(IllegalArgumentException.class, () -> grid.setCellGroup(0, 1, null));
        assertDoesNotThrow(() -> grid.setCellGroup(0, 0, g1));
        assertDoesNotThrow(() -> grid.setCellGroup(0, 1, g1));
        assertDoesNotThrow(() -> grid.setCellGroup(1, 1, g1));
        assertEquals(1, grid.getGroupCount());
        assertThrows(IllegalArgumentException.class, () -> grid.deleteCellFromGroup(0, 1));
        assertDoesNotThrow(() -> grid.deleteCellFromGroup(1, 1));
        assertDoesNotThrow(() -> grid.deleteCellFromGroup(0, 1));
        assertThrows(IllegalArgumentException.class, () -> grid.setCellGroup(1, 1, g1));
        assertEquals(1, grid.getGroupCount());
    }

    @Test
    void cellsCopyTest() {
        var grid = getValidGrid();
        var cells = grid.getCellsCpy();
        assertEquals(gridLenY, cells.length);
        assertEquals(gridLenX, cells[0].length);
        cells[0][0] = null;
        cells = grid.getCellsCpy();
        assertNotNull(cells[0][0]);
    }

    @Test
    void numberTest() {
        var grid = new Grid(1, 1);
        var cells = grid.getCellsCpy();
        assertNull(cells[0][0].getNumber());
        assertFalse(cells[0][0].isNumberCell());
        grid.setNumberCell(0, 0, 2);
        cells = grid.getCellsCpy();
        assertEquals(2, cells[0][0].getNumber());
        assertTrue(cells[0][0].isNumberCell());
        grid.deleteNumberCell(0, 0);
        cells = grid.getCellsCpy();
        assertNull(cells[0][0].getNumber());
        assertFalse(cells[0][0].isNumberCell());
    }

    @Test
    void isNumberCellsCountValid() {
        var validGrid = GridTest.getValidGrid();
        assertTrueGrid(validGrid::isNumberCellCountValid);
        setAllNumbers(validGrid, 3);
        assertFalseGrid(validGrid::isNumberCellCountValid);
    }

    private void setAllNumbers(Grid validGrid, int number) {
        for (int veri = 0; veri < GridTest.gridLenY; veri++) {
            for (int hori = 0; hori < GridTest.gridLenX; hori++) {
                validGrid.setNumberCell(hori, veri, number);
            }
        }
    }

    private void assertTrueGrid(Function<Group, Tuple<Boolean, Integer>> expression) {
        var g1 = GridTest.getGroup1();
        var g2 = GridTest.getGroup2();
        var g3 = GridTest.getGroup3();
        var v1 = expression.apply(g1);
        var v2 = expression.apply(g2);
        var v3 = expression.apply(g3);
        assertTrue(v1.getKey());
        assertTrue(v2.getKey());
        assertTrue(v3.getKey());
        assertNull(v1.getValue());
        assertNull(v2.getValue());
        assertNull(v3.getValue());
    }

    private void assertFalseGrid(Function<Group, Tuple<Boolean, Integer>> expression) {
        var g1 = GridTest.getGroup1();
        var g2 = GridTest.getGroup2();
        var g3 = GridTest.getGroup3();
        var v1 = expression.apply(g1);
        var v2 = expression.apply(g2);
        var v3 = expression.apply(g3);
        assertFalse(v1.getKey());
        assertFalse(v2.getKey());
        assertFalse(v3.getKey());
        assertNotNull(v1.getValue());
        assertNotNull(v2.getValue());
        assertNotNull(v3.getValue());
    }

    @Test
    void isGroupCellNumberValid() {
        var validGrid = GridTest.getValidGrid();
        assertTrueGrid(validGrid::isGroupCellNumberValid);
        var invalidGrid = getInValidGrid();
        assertFalseGrid(invalidGrid::isGroupCellNumberValid);
    }

    @Test
    void validateAll() {
        var validGrid = GridTest.getValidGrid();
        assertTrue(validGrid::validateAll);
        var invalidGrid = getInValidGrid();
        assertFalse(invalidGrid::validateAll);
    }

    @Test
    void getAdjacentPoints(){
        var grid = getValidGrid();
        var aPoints = grid.getAdjacentPoints(1,0, null, false, false);
        assertTrue(aPoints.contains(new Point(0,0)));
        assertTrue(aPoints.contains(new Point(1,1)));
        assertFalse(aPoints.contains(new Point(0,1)));
        assertFalse(aPoints.contains(new Point(1,0)));

        grid.deleteNumberCell(0,0);
        aPoints = grid.getAdjacentPoints(1,0, null, false, true);
        assertTrue(aPoints.contains(new Point(0,0)));
        assertFalse(aPoints.contains(new Point(1,1)));

        grid.deleteCellFromGroup(1,1);
        aPoints = grid.getAdjacentPoints(1,0, null, true, false);
        assertFalse(aPoints.contains(new Point(0,0)));
        assertTrue(aPoints.contains(new Point(1,1)));

        grid.setCellGroup(1,1, new Group(1));
        aPoints = grid.getAdjacentPoints(1,0, new Group(1), false, false);
        assertFalse(aPoints.contains(new Point(0,0)));
        assertTrue(aPoints.contains(new Point(1,1)));
    }
}