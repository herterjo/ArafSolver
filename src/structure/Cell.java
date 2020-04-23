package structure;

import dataHelper.ICopyable;
import dataHelper.Point;

import java.util.Objects;

public class Cell implements ICopyable<Cell> {
    private final int posX;
    private final int posY;
    private Group group;
    private Integer number;


    public Cell(int posX, int posY, Group group, Integer number) {
        if (posX < 0 || posY < 0) {
            throw new IllegalArgumentException("Position can't be less than 0");
        }
        if (number != null && number < 0) {
            throw new IllegalArgumentException("Number can't be less than 0");
        }
        this.posX = posX;
        this.posY = posY;
        this.group = group;
        this.number = number;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Point getPos() {
        return new Point(getPosX(), getPosY());
    }

    public structure.Group getGroup() {
        return group;
    }

    public void setGroup(structure.Group group) {
        this.group = group;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Number can't be less than 1");
        }
        this.number = number;
    }

    public boolean isNumberCell() {
        return getNumber() != null;
    }

    public boolean isGroupCell() {
        return getGroup() != null;
    }

    public void deleteNumber() {
        number = null;
    }

    public Cell copy() {
        return new Cell(posX, posY, group == null ? null : group.copy(), number);
    }

    @Override
    public String toString() {
        return "(" + posX + "," + posY + "), gr: " + group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return posX == cell.posX &&
                posY == cell.posY &&
                group.equals(cell.group) &&
                number.equals(cell.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY, group, number);
    }
}
