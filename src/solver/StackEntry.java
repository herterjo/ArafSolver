package solver;

import dataHelper.Point;
import dataHelper.Tuple;
import structure.StreamHelper;

import java.util.*;
import java.util.stream.Collectors;

public class StackEntry {
    private final Map<Point, ReverseFunc> possibilities;
    private Tuple<Point, ReverseFunc> lastPoppedReverse;
    private final List<Point> order;

    public StackEntry(Map<Point, ReverseFunc> possibilities, List<Point> order) {
        List<Point> keys = new LinkedList<>(possibilities.keySet());
        var keyNull = StreamHelper.hasNull(keys);
        var funcNull = StreamHelper.hasNull(new ArrayList<>(possibilities.values()));
        if (keyNull || funcNull) {
            throw new IllegalArgumentException("Possibilities can't include null");
        }
        //Maybe a copy of indiviual object is needed
        this.possibilities = new HashMap<>(possibilities);
        lastPoppedReverse = new Tuple<>(null, (p) -> {
        });
        if(order == null){
            this.order = keys;
        }else{
            keys = keys.stream().filter(p -> !order.contains(p)).collect(Collectors.toList());
            order.addAll(keys);
            this.order = new LinkedList<>(order);
        }
    }

    public int size() {
        return possibilities.size();
    }

    public Point pop() {
        var lastPoppedPoint = lastPoppedReverse.getKey();
        var lastPoppedFunc = lastPoppedReverse.getValue();
        lastPoppedFunc.execute(lastPoppedPoint);
        if (this.size() < 1) {
            lastPoppedReverse = new Tuple<>(null, (p) -> {
            });
            return null;
        }
        var key = order.get(0);
        var value = possibilities.remove(key);
        lastPoppedReverse = new Tuple<>(key, value);
        return key;
    }
}
