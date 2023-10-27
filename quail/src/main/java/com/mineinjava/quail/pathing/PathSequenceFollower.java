package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.robotMovement;

/**
 * A class that helps you follow a sequence of paths
 * 
 * This is useful for autonomous, where you want to follow a path, then do something, then follow another path, etc.
 */
public class PathSequenceFollower {
    
    PathFollower pathFollower;
    ArrayList<SequenceSegment> segments;
    int currentSegment = 0;

    private long startTime = System.nanoTime();

    public PathSequenceFollower(PathFollower pathFollower) {
        this.pathFollower = pathFollower;
        this.segments = new ArrayList<SequenceSegment>();
    }

    public PathSequenceFollower addPath(Path path) {
        segments.add(new SequenceSegment(this, SegmentType.PATH, () -> {
            pathFollower.setPath(path);
        }));

        return this;
    }

    public PathSequenceFollower addDisplacementMarker(Runnable action) {
        segments.add(new SequenceSegment(this, SegmentType.MARKER, action));
        return this;
    }

    public SequenceSegment getCurrentSegment() {
        return segments.get(currentSegment);
    }

    public void nextSegment() {
        currentSegment++;
    }

    public robotMovement followPathSequence() {
        if (segments.get(currentSegment).getType() == SegmentType.PATH) {
            segments.get(currentSegment).run();
            return pathFollower.calculateNextDriveMovement();
        } else if (segments.get(currentSegment).getType() == SegmentType.MARKER){
            segments.get(currentSegment).run();
            return new robotMovement(0, 0, 0);
        } else {
            return new robotMovement(0, 0, 0);
        }
    }

    public boolean isFinished() {
        return currentSegment < segments.size();
    }

    public double getElapsedTime() {
        return (System.nanoTime() - startTime) / 1e9;
    }
}