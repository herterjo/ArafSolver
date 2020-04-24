package decoder;

import structure.Grid;

@FunctionalInterface
public interface GridConstructor {
    Grid run(int posX, int posY);
}
