package com.mineinjava.quail.pathing;

public class SequenceSegment {
    private PathSequenceFollower pathSequenceFollower;
    private Runnable action;
    private SegmentType type;

    public SequenceSegment(PathSequenceFollower pathSequenceFollower, SegmentType type, Runnable action) {
        this.pathSequenceFollower = pathSequenceFollower;
        this.action = action;
    }

    public SegmentType getType() {
        return type;
    }

    public void run() {
        action.run();
        pathSequenceFollower.nextSegment();
    }
}
