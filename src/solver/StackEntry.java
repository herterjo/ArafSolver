package solver;

import dataHelper.Point;
import dataHelper.Tuple;
import structure.StreamHelper;

import java.util.*;
import java.util.stream.Collectors;

public class StackEntry {
    private final static ReverseFunc nop = p -> {
    };
    private final Map<Point, ReverseFunc> possibilities;
    private final List<Point> order;
    private final StepType stepType;
    private final Point origin;
    private final Point target;
    private Tuple<Point, ReverseFunc> lastPoppedReverse;

    public StackEntry(Map<Point, ReverseFunc> possibilities, List<Point> order,
                      StepType stepType, Point origin, Point target) {
        this.stepType = stepType;
        this.origin = origin;
        this.target = target;
        List<Point> keys = new LinkedList<>(possibilities.keySet());
        var keyNull = StreamHelper.hasNull(keys);
        var funcNull = StreamHelper.hasNull(new ArrayList<>(possibilities.values()));
        if (keyNull || funcNull) {
            throw new IllegalArgumentException("Possibilities can't include null");
        }
        //Maybe a copy of indiviual object is needed
        this.possibilities = new HashMap<>(possibilities);
        lastPoppedReverse = new Tuple<>(null, nop);
        if (order == null) {
            this.order = keys;
        } else {
            keys = keys.stream().filter(p -> !order.contains(p)).collect(Collectors.toList());
            order.addAll(keys);
            this.order = new LinkedList<>(order);
        }
    }

    public static ReverseFunc getNop() {
        return nop;
    }

    public StepType getStepType() {
        return stepType;
    }

    public Point getOrigin() {
        return origin;
    }

    public Point getTarget() {
        return target;
    }

    public Point pop() {
        var lastPoppedPoint = lastPoppedReverse.getKey();
        var lastPoppedFunc = lastPoppedReverse.getValue();
        lastPoppedFunc.execute(lastPoppedPoint);
        if (this.size() < 1) {
            lastPoppedReverse = new Tuple<>(null, nop);
            return null;
        }
        var key = order.remove(0);
        var value = possibilities.remove(key);
        lastPoppedReverse = new Tuple<>(key, value);
        return key;
    }

    public int size() {
        return possibilities.size();
    }

    @Override
    public String toString() {
        return "size: " + size();
    }
}
