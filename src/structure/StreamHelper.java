package structure;

import dataHelper.ICopyable;
import dataHelper.Point;
import dataHelper.Tuple;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StreamHelper {

    public static List<Cell> getNumberCells(List<Cell> cells) {
        return getNumberCells(cells.stream());
    }

    public static List<Cell> getNumberCells(Stream<Cell> cells) {
        return cells.filter(Cell::isNumberCell).collect(Collectors.toList());
    }

    public static Stream<Cell> getNumberCellsToStream(Stream<Cell> cells) {
        return cells.filter(Cell::isNumberCell);
    }

    public static Tuple<Integer, Integer> getMinMaxNumbers(List<Cell> cells) {
        return getMinMaxNumbers(cells.stream());
    }

    public static Tuple<Integer, Integer> getMinMaxNumbers(Stream<Cell> cells) {
        var numberCells = getNumberCellsToStream(cells);
        var numbers = numberCells.map(Cell::getNumber).collect(Collectors.toList());
        var max = Math.max(numbers.get(0), numbers.get(1));
        var min = Math.min(numbers.get(0), numbers.get(1));
        return new Tuple<>(min, max);
    }

    public static List<Cell> getNotInGroups(List<Cell> cells) {
        return getNotInGroups(cells.stream());
    }

    public static List<Cell> getNotInGroups(Stream<Cell> cells) {
        return cells.filter(cell -> !cell.isGroupCell()).collect(Collectors.toList());
    }

    public static List<Point> getPoints(List<Cell> cells) {
        return getPoints(cells.stream());
    }

    public static List<Point> getPoints(Stream<Cell> cells) {
        return cells.map(Cell::getPos).collect(Collectors.toList());
    }

    public static <C extends ICopyable<C>> List<C> getCopy(List<C> copyable) {
        return getCopy(copyable.stream());
    }

        public static <C extends ICopyable<C>> List<C> getCopy(Set<C> cells) {
        return getCopy(cells.stream());
    }

    public static <C extends ICopyable<C>> List<C> getCopy(Stream<C> copyable) {
        return copyable.map(ICopyable::copy).collect(Collectors.toList());
    }

    public static <T> List<T> getFlatMap(List<List<T>> toflatten) {
        return getFlatMap(toflatten.stream());
    }

    public static <T> List<T> getFlatMap(Stream<List<T>> toflatten) {
        return getFlatMapToStream(toflatten).collect(Collectors.toList());
    }

    public static <T> Stream<T> getFlatMapToStream(Stream<List<T>> toflatten) {
        return toflatten.flatMap(List::stream);
    }

    public static <C extends ICopyable<C>> List<C> getFlatCopy(List<List<C>> toflatten) {
        return getFlatCopy(toflatten.stream());
    }

    public static <C extends ICopyable<C>> List<C> getFlatCopy(Stream<List<C>> toflatten) {
        return getCopy(getFlatMapToStream(toflatten));
    }

    public static <T> boolean hasNull(List<T> toCheck) {
        return hasNull(toCheck.stream());
    }

    public static <T> boolean hasNull(Stream<T> toCheck) {
        return toCheck.anyMatch(Objects::isNull);
    }
}
