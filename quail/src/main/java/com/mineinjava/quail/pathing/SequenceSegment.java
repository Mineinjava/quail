package com.mineinjava.quail.pathing;

public class SequenceSegment {
    private PathSequenceFollower pathSequenceFollower;
    private Runnable action;
    private SegmentType type;
    private boolean isRunning = false;

    public SequenceSegment(PathSequenceFollower pathSequenceFollower, SegmentType type, Runnable action) {
        this.pathSequenceFollower = pathSequenceFollower;
        this.action = action;
    }

    public SegmentType getType() {
        return type;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        isRunning = true;
        action.run();

        if (type == SegmentType.PATH) {
            if (pathSequenceFollower.pathFollower.isFinished()) {
                pathSequenceFollower.nextSegment();
                isRunning = false;
            } else {
                isRunning = true;
                return;
            }
        }

        pathSequenceFollower.nextSegment();
        isRunning = false;
    }
}
