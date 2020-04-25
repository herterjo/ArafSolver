package structure;

import dataHelper.ICopyable;

public class Group implements ICopyable<Group> {
    private int id;

    public Group(int id) {
        this.id = id;
    }

    public Group() {
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public String toString() {
        return id + "";
    }

    public Group copy() {
        return new Group(id);
    }
}
