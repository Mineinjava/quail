package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.robotMovement;

public class PathSequenceFollower {
    
    PathFollower pathFollower;
    ArrayList<Path> paths;
    int currentPath = 0;

    public PathSequenceFollower(PathFollower pathFollower, ArrayList<Path> paths) {
        this.pathFollower = pathFollower;
        this.paths = paths;
    }

    public void addPath(Path path) {
        paths.add(path);
    }

    public Path getCurrentPath() {
        return paths.get(currentPath);
    }

    public robotMovement followPathSequence() {
        if (pathFollower.path == null) {
            pathFollower.setPath(getCurrentPath());
        }

        robotMovement movement = pathFollower.calculateNextDriveMovement();
        
        if (pathFollower.isFinished()) {
            currentPath++;
            if (currentPath >= paths.size()) {
                currentPath = 0;
            }
            pathFollower.setPath(getCurrentPath());
        }

        return movement;
    }    
}