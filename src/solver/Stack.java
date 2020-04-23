package solver;


import dataHelper.Point;

import java.security.cert.CollectionCertStoreParameters;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stack {
    private final LinkedList<StackEntry> entries;

    public Stack() {
        entries = new LinkedList<>();
    }

    public void push(List<Point> possibilities, ReverseFunc r) {
        var map = possibilities.stream().collect(Collectors.toMap(p -> p, p->r));
        push(map, possibilities);
    }

    public void push(Map<Point, ReverseFunc> possibilities, List<Point> order) {
        var entry = new StackEntry(possibilities, order);
        push(entry);
    }

    public void push(StackEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry can't be null");
        }
        if (entry.size() < 1) {
            throw new IllegalArgumentException("Entry is empty");
        }
        entries.push(entry);
    }

    public int size() {
        return entries.size();
    }

    public Point popPossibility() {
        if (size() < 1) {
            return null;
        }
        return entries.getFirst().pop();
    }

    public boolean popEntry() {
        if (entries.getFirst().size() > 0) {
            throw new IllegalStateException("There are left possibilities in the current stack entry");
        }
        if(entries.size() < 1){
            return false;
        }
        entries.pop();
        return true;
    }
}
