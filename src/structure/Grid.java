package structure;

import dataHelper.IllegalGridStateException;
import dataHelper.Point;
import dataHelper.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class Grid {
    // is no color from color helper
    public static final Group floodFillGroup = new Group(16777216);
    protected static final int numberCellsInGroup = 2;
    protected static final List<Point> searchPattern = Arrays.asList(
            new Point(1, 0),
            new Point(-1, 0),
            new Point(0, 1),
            new Point(0, -1)
    );
    protected static final List<Point> floodFillModifier = Arrays.asList(
            new Point(1, 1),
            new Point(-1, -1),
            new Point(-1, 1),
            new Point(1, -1)
    );
    // First vertical, then horizontal
    protected final List<List<Cell>> cells;
    protected final int lenX;
    protected final int lenY;
    protected final Map<Group, List<Cell>> groups;

    public Grid(int lenX, int lenY) {
        if (lenX < 1 || lenY < 1) {
            throw new IllegalArgumentException("Length can't be less than 1, is " + lenX + " and " + lenY);
        }
        this.groups = new HashMap<>();
        this.lenX = lenX;
        this.lenY = lenY;
        this.cells = new ArrayList<>(this.lenY);
        for (int verti = 0; verti < this.lenY; verti++) {
            var innerList = new ArrayList<Cell>(this.lenX);
            this.cells.add(verti, innerList);
            for (int hori = 0; hori < this.lenX; hori++) {
                innerList.add(hori, new Cell(hori, verti, null, null));
            }
        }
    }

    public void setNumberCell(int posX, int posY, int number) {
        getCell(posX, posY).setNumber(number);
    }

    protected Cell getCell(int posX, int posY) {
        if (isIndexInValid(posX, posY)) {
            throw new IllegalArgumentException("Position (" + posX + "," + posY + ") is invalid");
        }
        return this.cells.get(posY).get(posX);
    }

    public boolean isIndexInValid(int posX, int posY) {
        return posX < 0 || posY < 0 || posX >= lenX || posY >= lenY;
    }

    public void deleteNumberCell(int posX, int posY) {
        getCell(posX, posY).deleteNumber();
    }

    public void setCellGroup(Point p, Group group) {
        setCellGroup(p.getX(), p.getY(), group);
    }

    public void setCellGroup(int posX, int posY, Group group) {
        setCellGroupPrivate(posX, posY, group);
    }

    private void setCellGroupPrivate(int posX, int posY, Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group can't be null");
        }
        var cell = getCell(posX, posY);
        var oldGroup = cell.getGroup();
        if (oldGroup == group) {
            return;
        } else if (oldGroup != null) {
            deleteCellFromGroup(posX, posY);
        }
        if (!groups.containsKey(group)) {
            var cellList = new LinkedList<Cell>();
            cellList.add(cell);
            groups.put(group, cellList);
        } else if (hasAdjacentGroupCell(posX, posY, group)) {
            var cellsInGroup = groups.get(group);
            cellsInGroup.add(cell);
        } else {
            throw new IllegalArgumentException("Seperated cell of group found (x: " + posX + ",y: " + posY + ")");
        }
        cell.setGroup(group);
    }

    public void deleteCellFromGroup(Point p) {
        deleteCellFromGroup(p.getX(), p.getY());
    }

    public void deleteCellFromGroup(int posX, int posY) {
        deleteCellFromGroupPrivate(posX, posY, true);
    }

    public void deleteCellFromGroupPrivate(int posX, int posY, boolean check) {
        var cell = getCell(posX, posY);
        var oldGroup = cell.getGroup();
        if (oldGroup == null) {
            return;
        }
        if (check) {
            var adjacentPoints = getAdjacentPoints(posX, posY, oldGroup, false, false);
            if (adjacentPoints.size() > 1) {
                for (var adPoint : adjacentPoints) {
                    if (getAdjacentPoints(adPoint.getX(), adPoint.getY(), oldGroup, false, false)
                            .size() < 2) {
                        throw new IllegalArgumentException("The adjacent cell " + adPoint + " would be cut off");
                    }
                }
            }
        }

        var groupCells = groups.get(oldGroup);
        groupCells.remove(cell);
        cell.setGroup(null);
        if (groupCells.size() < 1) {
            groups.remove(oldGroup);
        }
    }

    public List<Point> getAdjacentPoints(int posX, int posY, Group g, boolean noGroup, boolean noNumber) {
        var adCells = searchPattern.stream().map(p -> {
            var newX = posX + p.getX();
            var newY = posY + p.getY();
            if (isIndexInValid(newX, newY)) {
                return null;
            }
            return getCell(newX, newY);
        }).filter(Objects::nonNull);

        if (g != null) {
            adCells = adCells.filter(c -> c.getGroup() != null && c.getGroup().equals(g));
        } else if (noGroup) {
            adCells = adCells.filter(c -> !c.isGroupCell());
        }

        if (noNumber) {
            adCells = adCells.filter(c -> !c.isNumberCell());
        }
        return adCells.map(Cell::getPos).collect(Collectors.toList());
    }

    public Map<Group, List<Cell>> getGroupMapCopy() {
        var grpcopy = new HashMap<Group, List<Cell>>();
        for (var group : groups.keySet()) {
            if (group == null) {
                continue;
            }
            var cells = groups.get(group);
            var cellscpy = StreamHelper.getCopy(cells);
            grpcopy.put(group.copy(), cellscpy);
        }
        return grpcopy;
    }

    public Cell[][] getCellsCpy() {
        var cellscpy = new Cell[lenY][lenX];
        for (int verti = 0; verti < lenY; verti++) {
            for (int hori = 0; hori < lenX; hori++) {
                cellscpy[verti][hori] = getCell(hori, verti).copy();
            }
        }
        return cellscpy;
    }

    public Cell getCellCopy(Point p) {
        return getCellCopy(p.getX(), p.getY());
    }

    public Cell getCellCopy(int posX, int posY) {
        return this.getCell(posX, posY).copy();
    }

    public List<Point> getAdjacentPoints(Point p, Group g, boolean noGroup, boolean noNumber) {
        return getAdjacentPoints(p.getX(), p.getY(), g, noGroup, noNumber);
    }

    public boolean hasAdjacentGroupCell(int posX, int posY, Group group) {
        return getAdjacentPoints(posX, posY, group, true, false).size() > 0;
    }

//    //Debug variant
//    public List<Point> getAdjacentPoints(int posX, int posY, Group g, boolean noGroup, boolean noNumber) {
//        var adCells = searchPattern.stream().map(p -> {
//            var newX = posX + p.getX();
//            var newY = posY + p.getY();
//            if (isIndexInValid(newX, newY)) {
//                return null;
//            }
//            return getCell(newX, newY);
//        }).filter(Objects::nonNull);
//        var tmp = adCells.collect(Collectors.toList());
//
//        if (g != null) {
//            adCells = tmp.stream().filter(c -> c.getGroup() != null && c.getGroup().equals(g));
//            tmp = adCells.collect(Collectors.toList());
//        } else if (noGroup) {
//            adCells = tmp.stream().filter(c -> !c.isGroupCell());
//            tmp = adCells.collect(Collectors.toList());
//        }
//
//        if (noNumber) {
//            adCells = tmp.stream().filter(c -> !c.isNumberCell());
//            tmp = adCells.collect(Collectors.toList());
//        }
//        return tmp.stream().map(Cell::getPos).collect(Collectors.toList());
//    }

    public boolean validateAll() {
        var validResultCells = isAllCellsInGroups();
        if (!validResultCells.getKey()) {
            throw new IllegalGridStateException("Not all cells are in a group");
        }
        for (Group group : groups.keySet()) {
            var numberValid = isNumberCellCountValid(group);
            if (!numberValid.getKey()) {
                return false;
            }
            var countValid = isGroupCellNumberValid(group);
            if (!countValid.getKey()) {
                return false;
            }
        }
        return true;
    }

    public Tuple<Boolean, List<Point>> isAllCellsInGroups() {
        var cells = getFlatCellsCopy();
        var notInGroups = StreamHelper.getNotInGroups(cells);
        if (notInGroups.size() > 0) {
            var mapped = StreamHelper.getPoints(notInGroups);
            return new Tuple<>(false, mapped);
        }
        return new Tuple<>(true, null);
    }

    public Tuple<Boolean, Integer> isNumberCellCountValid(Group group) {
        var cells = groups.get(group);
        var numberCells = StreamHelper.getNumberCells(cells);
        var numberCellsCount = numberCells.size();
        var difference = numberCellsCount - numberCellsInGroup;
        if (difference != 0) {
            return new Tuple<>(false, difference);
        }
        return new Tuple<>(true, null);
    }

    public Tuple<Boolean, Integer> isGroupCellNumberValid(Group group) {
        var numCellsValid = isNumberCellCountValid(group);
        if (!numCellsValid.getKey()) {
            throw new IllegalGridStateException("NumberCellsCount is not valid for this group, you have to check this first");
        }
        var cells = groups.get(group);
        var minMaxNumbers = StreamHelper.getMinMaxNumbers(cells);
        var min = minMaxNumbers.getKey();
        var max = minMaxNumbers.getValue();
        var groupCount = cells.size();

        if (groupCount >= max) {
            return new Tuple<>(false, groupCount - max + 1);
        }
        if (groupCount <= min) {
            return new Tuple<>(false, groupCount - min - 1);
        }
        return new Tuple<>(true, null);
    }

    public List<Cell> getFlatCellsCopy() {
        return StreamHelper.getFlatCopy(cells);
    }

    public int getGroupsCount() {
        return groups.size();
    }

    public List<Group> getGroupsCopy() {
        return StreamHelper.getCopy(groups.keySet());
    }

    public int getGroupCellCount(Group group) {
        return groups.get(group).size();
    }

    public List<Cell> getCellsInGroup(Group g) {
        return StreamHelper.getCopy(groups.get(g));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return Objects.equals(cells, grid.cells);
    }

    @Override
    public String toString() {
        return lenX + "*" + lenY + ": " + cells;
    }

    public int getStatusCode() {
        var i = 0;
        var list = new LinkedList<Tuple<Integer, List<Point>>>();
        for (List<Cell> cells : groups.values()) {
            var innerList = StreamHelper.getPoints(cells);
            innerList.sort(Comparator.comparing(p -> lenX * p.getY() + p.getX()));
            list.add(new Tuple<>(i, innerList));
            i++;
        }
        list.sort(Comparator.comparing(Tuple::getKey));
        return Objects.hash(list);
    }

    public boolean isFloodFillAcceptable(Point p, Integer maxFloodedAcceptable, Point exclude, boolean checkOtherNumsMax) {
        setCellGroupPrivate(p.getX(), p.getY(), Grid.floodFillGroup);
        var maxNull = maxFloodedAcceptable == null;
        var originalToFloodPoints = getAdjacentPoints(p, null, true, false);
        for (var mod : floodFillModifier) {
            var newX = p.getX() + mod.getX();
            var newY = p.getY() + mod.getY();
            if (this.isIndexInValid(newX, newY)) {
                continue;
            }
            var diagCell = getCell(newX, newY);
            if (diagCell.isGroupCell()) {
                continue;
            }
            var diagAdj = getAdjacentPoints(diagCell.getPos(), null, true, false);
            var cut = originalToFloodPoints.stream().filter(diagAdj::contains).collect(Collectors.toList());
            if (cut.size() > 1) {
                // both are reachable with floodfill, so remove one
                originalToFloodPoints.remove(cut.get(0));
            }
        }
        var valid = true;
        for (var ofp : originalToFloodPoints) {
            var flooded = floodFill(ofp, true).stream()
                    .filter(fp -> !fp.equals(exclude))
                    .map(fp -> getCell(fp.getX(), fp.getY())).collect(Collectors.toList());
            var floodedCount = flooded.size();
            var floodedNumCells = StreamHelper.getNumberCells(flooded);
            var floodedNumsCount = floodedNumCells.size();
            var flNumCtLess1 = floodedNumsCount < 1;
            if (maxNull && flNumCtLess1) {
                continue;
            }
            if (!maxNull && flNumCtLess1) {
                if (floodedCount <= maxFloodedAcceptable) {
                    continue;
                }
                valid = false;
                break;
            }
            if (floodedNumsCount % 2 != 0) {
                valid = false;
                break;
            }
            var floodedNumsNumbers = floodedNumCells.stream().map(Cell::getNumber)
                    .sorted(Comparator.comparing(n -> n)).collect(Collectors.toList());
            var min = 0;
            for (var i = 0; i < floodedNumsCount / 2; i++) {
                min += floodedNumsNumbers.get(i) + 1;
            }
            if (floodedCount < min) {
                valid = false;
                break;
            }
            if (checkOtherNumsMax) {
                var max = 0;
                for (var i = floodedNumsCount / 2; i < floodedNumsCount; i++) {
                    max += floodedNumsNumbers.get(i) - 1;
                }
                if (floodedCount > max) {
                    valid = false;
                    break;
                }
            }

        }
        deleteCellFromGroupPrivate(p.getX(), p.getY(), false);
        return valid;
    }

    private List<Point> floodFill(Point p, boolean first) {
        var floodedPoints = new LinkedList<>(Collections.singletonList(p));
        if (first) {
            setCellGroupPrivate(p.getX(), p.getY(), floodFillGroup);
        }
        var toFloodPoints = getAdjacentPoints(p, null, true, false);
        for (var tfp : toFloodPoints) {
            setCellGroupPrivate(tfp.getX(), tfp.getY(), floodFillGroup);
        }
        for (var tfp : toFloodPoints) {
            floodedPoints.addAll(floodFill(tfp, false));
        }
        if (first) {
            for (var fp : floodedPoints) {
                deleteCellFromGroupPrivate(fp.getX(), fp.getY(), false);
            }
        }
        return floodedPoints;
    }
}
