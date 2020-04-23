package decoder;

import dataHelper.Point;
import dataHelper.Tuple;
import structure.Grid;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonGrid {
    private final List<Tuple<Point, Integer>> numberCells;
    private final int lenX;
    private final int lenY;

    public JsonGrid() {
        numberCells = new LinkedList<>();
        lenX = 0;
        lenY = 0;
    }

    public Grid getGrid() {
        if (lenX < 1 || lenY < 1) {
            throw new IllegalStateException("Length can't be less than 1, is " + lenX + " and " + lenY);
        }
        var nCellsCpy = numberCells.stream()
                .map(c -> c == null ? null : new Tuple<>(c.getKey() == null ? null : c.getKey().copy(), c.getValue()))
                .collect(Collectors.toList());
        //gson interpretes trailing comma in json list as null
        nCellsCpy.remove(null);
        if (nCellsCpy.size() % 2 != 0) {
            throw new IllegalStateException("NumberCells are not divisible by two");
        }
        var distinctCount = nCellsCpy.stream().map(Tuple::getKey).distinct().count();
        if (distinctCount != nCellsCpy.size()) {
            throw new IllegalStateException("There are multiple entries for the same position");
        }
        var grid = new Grid(lenX, lenY);
        for (var pointNumber : nCellsCpy) {
            var cellPos = pointNumber.getKey();
            var number = pointNumber.getValue();
            var x = cellPos.getX();
            var y = cellPos.getY();
            if (x >= lenX || y >= lenY) {
                throw new IllegalStateException("Position (" + x + "," + y + ") is out of index," +
                        " length is " + lenX + " and " + lenY);
            }
            grid.setNumberCell(x, y, number);
        }
        return grid;
    }
}
