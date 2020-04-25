package decoder;

import dataHelper.IllegalGridStateException;
import dataHelper.Point;
import dataHelper.Tuple;
import gui.DrawableGrid;
import javafx.stage.Stage;
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
        return getGrid(Grid::new);
    }

    private Grid getGrid(GridConstructor gc) {
        if (lenX < 1 || lenY < 1) {
            throw new IllegalGridStateException("Length can't be less than 1, is " + lenX + " and " + lenY);
        }
        var nCellsCpy = numberCells.stream()
                .map(c -> c == null ? null : new Tuple<>(c.getKey() == null ? null : c.getKey().copy(), c.getValue()))
                .collect(Collectors.toList());
        //gson interpretes trailing comma in json list as null
        nCellsCpy.remove(null);
        if (nCellsCpy.size() % 2 != 0) {
            throw new IllegalGridStateException("NumberCells are not divisible by two");
        }
        var distinctCount = nCellsCpy.stream().map(Tuple::getKey).distinct().count();
        if (distinctCount != nCellsCpy.size()) {
            throw new IllegalGridStateException("There are multiple entries for the same position");
        }
        var grid = gc.run(lenX, lenY);
        for (var pointNumber : nCellsCpy) {
            var cellPos = pointNumber.getKey();
            var number = pointNumber.getValue();
            var x = cellPos.getX();
            var y = cellPos.getY();
            if (x >= lenX || y >= lenY) {
                throw new IllegalGridStateException("Position (" + x + "," + y + ") is out of index," +
                        " length is " + lenX + " and " + lenY);
            }
            grid.setNumberCell(x, y, number);
        }
        return grid;
    }

    public DrawableGrid getDrawableGrid(Stage stage) {
        return (DrawableGrid) getGrid((x, y) -> new DrawableGrid(x, y, stage));
    }
}
