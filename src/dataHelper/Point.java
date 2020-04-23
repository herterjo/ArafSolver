package dataHelper;

import java.util.Objects;

public class Point implements ICopyable<Point> {
    private int y;
    private int x;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point p = (Point) o;
        return getX() == p.getX() && getY() == p.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point copy() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}