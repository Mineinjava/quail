package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.RobotMovement;

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
    private long lastTime = System.nanoTime();

    public PathSequenceFollower(PathFollower pathFollower) {
        this.pathFollower = pathFollower;
        this.segments = new ArrayList<SequenceSegment>();
    }

    /**
     * Add a path to the sequence
     * @param path the path to be added to the sequence
     * @return
     */
    public PathSequenceFollower addPath(Path path) {
        this.segments.add(new SequenceSegment(this, SegmentType.PATH, () -> {
            pathFollower.setPath(path);
        }));

        return this;
    }

    /**
     * Add a displacement marker to the sequence (runs a runnable when the robot reaches the marker)
     * @param action the action to be completed by the marker
     * @return
     */
    public PathSequenceFollower addDisplacementMarker(Runnable action) {
        this.segments.add(new SequenceSegment(this, SegmentType.MARKER, action));
        return this;
    }

    /**
     * Add a temporal marker to the sequence (runs a runnable after a certain amount of local time)
     * @param delay the delay after the last segment ends before marker is run (in seconds)
     * @param action the action to be completed by the marker
     * @return
     */
    public PathSequenceFollower addLocalTemporalMarker(double delay, Runnable action) {
        this.segments.add(new SequenceSegment(this, SegmentType.MARKER, () -> {
            if (lastTime > delay) {
                action.run();
                nextSegment();
            }
        }));
        return this;
    }

    /**
     * Set the translation constraints for the path follower, used for all paths AFTER this call
     * @param constraints the constraints to be set (your units)
     * @return
     */
    public PathSequenceFollower setTranslationConstraints(ConstraintsPair constraints) {
        this.segments.add(new SequenceSegment(this, SegmentType.MARKER, () -> {
            pathFollower.setTranslationConstraints(constraints);
        }));
        return this;
    }

    /**
     * Set the rotation constraints for the path follower, used for all paths AFTER this call
     * @param constraints the constraints to be set (your units)
     * @return
     */
    public PathSequenceFollower setRotationConstraints(ConstraintsPair constraints) {
        this.segments.add(new SequenceSegment(this, SegmentType.MARKER, () -> {
            pathFollower.setRotationConstraints(constraints);
        }));
        return this;
    }

    /**
     * Returns the current segment
     * @return
     */
    public SequenceSegment getCurrentSegment() {
        return this.segments.get(currentSegment);
    }

    /**
     * Moves on to the next segment (can be used to force the sequence to move on)
     * @return
     */
    public void nextSegment() {
        currentSegment++;
        lastTime = System.nanoTime();
    }

    /**
     * Follows the pathsequence using PathFollower and markers
     * @return
     */
    public RobotMovement followPathSequence() {
        if (segments.get(currentSegment).getType() == SegmentType.PATH) {
            this.segments.get(currentSegment).run();
            return pathFollower.calculateNextDriveMovement();
        } else if (segments.get(currentSegment).getType() == SegmentType.MARKER){
            segments.get(currentSegment).run();
            return new RobotMovement(0, 0, 0);
        } else {
            return new RobotMovement(0, 0, 0);
        }
    }

    /**
     * Returns true if the sequence is finished
     * @return
     */
    public boolean isFinished() {
        return !(currentSegment < this.segments.size());
    }

    /**
     * Returns the elapsed time since the sequence started
     * @return
     */
    public double getElapsedTime() {
        return (System.nanoTime() - startTime) / 1e9;
    }
}