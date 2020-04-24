package solver;


import dataHelper.IllegalGridStateException;
import dataHelper.Point;
import dataHelper.Tuple;

import java.security.cert.CollectionCertStoreParameters;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stack {
    private final LinkedList<Tuple<StackEntry, StepType>> entries;

    public Stack() {
        entries = new LinkedList<>();
    }

    public void push(List<Point> possibilities, ReverseFunc r, StepType stepType) {
        var map = possibilities.stream().collect(Collectors.toMap(p -> p, p->r));
        push(map, possibilities, stepType);
    }

    public void push(Map<Point, ReverseFunc> possibilities, List<Point> order, StepType stepType) {
        var entry = new StackEntry(possibilities, order);
        push(entry, stepType);
    }

    public void push(StackEntry entry, StepType stepType) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry can't be null");
        }
        if (entry.size() < 1) {
            throw new IllegalArgumentException("Entry is empty");
        }
        entries.push(new Tuple<>(entry, stepType));
    }

    public int size() {
        return entries.size();
    }

    public Point popPossibility() {
        if (size() < 1) {
            return null;
        }
        return entries.getFirst().getKey().pop();
    }

    public StepType popEntry() {
        if (entries.getFirst().getKey().size() > 0) {
            throw new IllegalGridStateException("There are left possibilities in the current stack entry");
        }
        if(size() < 1){
            return null;
        }
        entries.pop();
        if(size() < 1){
            return null;
        }
        return entries.getFirst().getValue();
    }
}
