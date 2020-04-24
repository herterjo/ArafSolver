package solver;

import dataHelper.Point;

public class EntryContainer {
    private final StepType stepType;
    private final Point origin;
    private final Point target;

    public EntryContainer(StepType stepType, Point origin, Point target) {
        this.stepType = stepType;
        this.origin = origin;
        this.target = target;
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
}
