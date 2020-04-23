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
    private Cell targetNumCell;
    private Cell originNumCell;
    private final Stack stack;
    private final ReverseFunc deleteGroupFromCellFunc;
    private final List<Cell> numberCells;
    private StepType stepType;
    private final int neededGroupsCount;

    public Backtracking(Grid grid) {
        if (grid == null) {
            throw new IllegalArgumentException("grid can't be null");
        }
        this.grid = grid;
        this.targetNumCell = null;
        this.originNumCell = null;

        deleteGroupFromCellFunc = grid::deleteCellFromGroup;
        stepType = StepType.AddGroup;

        var cells = grid.getFlatCellsCopy();
        this.numberCells = StreamHelper.getNumberCells(cells);
        numberCells.sort(Comparator.comparing(Cell::getNumber));
        var numCellCount = numberCells.size();
        if (numCellCount % 2 != 0) {
            throw new IllegalArgumentException("grid can not be solved, has numberCell count not divisible by two");
        }
        neededGroupsCount = numCellCount / 2;

        this.stack = new Stack();
        var pushed = pushNewGroupPossibilities();
        if (!pushed) {
            throw new IllegalArgumentException("something wnt wrong with the first step to solve the grid");
        }

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

    private boolean pushNewGroupPossibilities() {
        var numberPoints = numberCells.stream().filter(c -> !c.isGroupCell()).map(Cell::getPos).collect(Collectors.toList());
        if (numberPoints.size() < 1) {
            return false;
        }
        pushOnStack(numberPoints, deleteGroupFromCellFunc, StepType.AddGroup);
        return true;
    }

    private void pushOnStack(List<Point> p, ReverseFunc f, StepType s) {
        stepType = s;
        stack.push(p, f, s);
    }

    public boolean Step() {
        var poppedPossibility = stack.popPossibility();
        //backtrack
        while (poppedPossibility == null || grid.getCellCopy(poppedPossibility).getGroup() != null) {
            if (stack.size() < 1) {
                throw new IllegalStateException("grid can't be solved");
            }
            this.stepType = stack.popEntry();
            poppedPossibility = stack.popPossibility();
        }

        var solvedPartially = false;
        if (stepType == StepType.AddGroup) {
            solvedPartially = addGroup(poppedPossibility);
        } else if (stepType == StepType.FillMin) {
            solvedPartially = fillMin(poppedPossibility);
        } else if (stepType == StepType.FillMax) {
            solvedPartially = fillMax(poppedPossibility);
        }

        return solvedPartially && grid.validateAll();
    }

    private boolean addGroup(Point possibility) {
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
        stepType = StepType.FillMin;
//        if (neededGroupsCount == groupCount) {
//            return true;
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
        targetNumCell = numberCells.stream().filter(c -> isReachableManhatten(c, originNumCell))
                .findFirst().orElse(null);
        // if no matching target
        if (targetNumCell == null) {
            return Step();
        }
        return pushAdjacentCells(originNumCell);
    }

    private boolean pushAdjacentCells(Cell currentCell) {
        var targetPos = targetNumCell.getPos();
        var currentPos = currentCell.getPos();

        var oGroup = currentCell.getGroup();
        var max = Math.max(targetNumCell.getNumber(), originNumCell.getNumber());
        var min = Math.min(targetNumCell.getNumber(), originNumCell.getNumber());
        StepType nextStepType;
        var groupCellCount = grid.getGroupCellCount(oGroup);
        if (min >= grid.getGroupCellCount(oGroup)) {
            nextStepType = StepType.FillMin;
        } else if (max > groupCellCount) {
            nextStepType = StepType.FillMax;
        } else {
            var groupCount = grid.getGroupsCount();
            if (neededGroupsCount == groupCount) {
                return true;
            }
            pushNewGroupPossibilities();
            return false;
        }

        List<Point> nextPossibilities;
        if (getDistMahatten(currentPos, targetPos) < 1) {
            nextPossibilities = new ArrayList<>(Collections.singletonList(targetPos));
        } else {
            //TODO: Sort for A*
            //TODO: Flood Fill Algorithm
            nextPossibilities = grid.getAdjacentPoints(currentPos, null, true, true);
        }
        //if there are no next possibilities,
        if (nextPossibilities.size() < 1) {
            return false;
        }
        stepType = nextStepType;
        pushOnStack(nextPossibilities, deleteGroupFromCellFunc, nextStepType);
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
    private boolean fillMax(Point possibility) {
        throw new IllegalArgumentException("");
    }

    //TODO: When to fill and when to let go
    private boolean fillMin(Point possibility) {
//        var hasPossibility = originNumCell != null && targetNumPoint != null && possibility != null;
        // if next cell is already determined
//        if (hasPossibility) {
        // check if posibility cell has a group (could have changed with time)
        var oGroup = originNumCell.getGroup();
        grid.setCellGroup(possibility, oGroup);
        var posCell = grid.getCellCopy(possibility);
        return pushAdjacentCells(posCell);
        // possibility was invalid
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
