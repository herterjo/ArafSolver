package solver;

import dataHelper.ColorHelper;
import dataHelper.Point;
import structure.Cell;
import structure.Grid;
import structure.Group;
import structure.StreamHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Backtracking {
    private final Grid grid;
    private Point targetNumPoint;
    private Cell originNumCell;
    private final Stack stack;
    private final ReverseFunc deleteGroupFromCellFunc;
    private final List<Cell> numberCells;
//    private final int neededGroupsCount;

    public Backtracking(Grid grid) {
        if (grid == null) {
            throw new IllegalArgumentException("grid can't be null");
        }
        this.grid = grid;
        this.targetNumPoint = null;
        this.originNumCell = null;

        deleteGroupFromCellFunc = grid::deleteCellFromGroup;

        var cells = grid.getFlatCellsCopy();
        this.numberCells = StreamHelper.getNumberCells(cells);
        numberCells.sort(Comparator.comparing(Cell::getNumber));
        var numCellCount = numberCells.size();
        if (numCellCount % 2 != 0) {
            throw new IllegalArgumentException("grid can not be solved, has numberCell count not divisible by two");
        }
//        neededGroupsCount = numCellCount / 2;

        this.stack = new Stack();
        pushNewGroupPossibility();

//        //rule out cells not in distance
//        for (var alli = 0; alli < numberCells.size(); alli++) {
//            var numCell = numberCells.get(alli);
//            for (var resti = alli + 1; resti < numberCells.size(); resti++) {
//                var otherNumCell = numberCells.get(resti);
//                //if the difference is less than 2, the pair is not possible
//                //if the distance is >= the greater number, the two numberCells can't be connected
//                if (Math.abs(numCell.getNumber() - otherNumCell.getNumber()) > 1
//                        && isReachableManhatten(numCell, otherNumCell))
//                    notUsedPairs.add(new Tuple<>(numCell, otherNumCell));
//            }
//        }
//        sortPairs();
    }

    private boolean pushNewGroupPossibility(){
        var numberPoints =  numberCells.stream().filter(c -> !c.isGroupCell()).map(Cell::getPos).collect(Collectors.toList());
        if(numberPoints.size() < 1){
            return true;
        }
        stack.push(numberPoints, deleteGroupFromCellFunc);
        return false;
    }

    public boolean Step() {
        var poppedPossibility = stack.popPossibility();
        //backtrack
        while (poppedPossibility == null) {
            if (stack.size() < 1) {
                throw new IllegalStateException("grid can't be solved");
            }
            stack.popEntry();
            poppedPossibility = stack.popPossibility();
        }
        return createNewGroup(poppedPossibility)
                && fillGroup(poppedPossibility)
                && grid.validateAll();
    }

    private boolean createNewGroup(Point possibility) {
        if (grid.getCellCopy(possibility).getGroup() == null) {
            return true;
        }
//        var groupCount = grid.getGroupCount();
//        if (neededGroupsCount != groupCount) {
//            var allCellsInG = grid.isAllCellsInGroups();
//            if (allCellsInG.getKey()) {
//        var groups = grid.getGroupsCopy();
//        //var groupCount = groups.size();
//        //TODO: not every step
//        for (var g : groups) {
//            var groupCellCountV = grid.isGroupCellNumberValid(g);
//            //if a group is missing cells, correct it first
//            if (!groupCellCountV.getKey() /*&& neededGroupsCount != groupCount*/) {
//                return true;
//            }
//        }
//                return true;
//            }
//        }

        var groups = grid.getGroupsCopy();
        int groupColor;
        Group group;
        do {
            groupColor = ColorHelper.getRandomIntColor();
            group = new Group(groupColor);
        } while (groups.contains(group));
        grid.setCellGroup(possibility, group);
        originNumCell = grid.getCellCopy(possibility);
        numberCells.remove(originNumCell);
        targetNumPoint = numberCells.get(0).getPos();

        return false;
    }

//    private void sortPairs() {
//        notUsedPairs.sort((t1, t2) -> {
//            var t1keyNum = t1.getKey().getNumber();
//            var t1valNum = t1.getValue().getNumber();
//            var t1compNum = Math.abs(t1keyNum - t1valNum);
//            var t2keyNum = t2.getKey().getNumber();
//            var t2valNum = t2.getValue().getNumber();
//            var t2compNum = Math.abs(t2keyNum - t2valNum);
//            return t1compNum - t2compNum;
//        });
//    }

    //TODO: When to fill and when to let go
    private boolean fillGroup(Point possibility) {
//        var hasPossibility = originNumCell != null && targetNumPoint != null && possibility != null;
        // if next cell is already determined
//        if (hasPossibility) {
        // check if posibility cell has a group (could have changed with time)
        if (grid.getCellCopy(possibility).getGroup() == null) {
            var oGroup = originNumCell.getGroup();
            grid.setCellGroup(possibility, oGroup);

            List<Point> nextPossibilities;
            if (getDistMahatten(possibility, targetNumPoint) < 1) {
                nextPossibilities = new ArrayList<>(Collections.singletonList(targetNumPoint));
            } else {
                //TODO: Sort for A*
                //TODO: Flood Fill Algorithm
                nextPossibilities = grid.getAdjacentPoints(possibility, null, true, true);
            }
            //if there are no next possibilities,
            if (nextPossibilities.size() < 1) {
                //TODO: grid.isGroupCellNumberValid(oGroup)
                return pushNewGroupPossibility();
            }
            stack.push(nextPossibilities, deleteGroupFromCellFunc);
            return false;
        }
        // possibility was invalid
        return Step();
        // }
    }

    private boolean isReachableManhatten(Cell c1, Cell c2) {
        var max = Math.max(c1.getNumber(), c2.getNumber());
        return isReachableManhatten(c1.getPos(), c2.getPos(), max);
    }

    private boolean isReachableManhatten(Point p1, Point p2, int max) {
        var dist = getDistMahatten(p1, p2);
        return dist < max;
    }

    private int getDistMahatten(Point p1, Point p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }
}
