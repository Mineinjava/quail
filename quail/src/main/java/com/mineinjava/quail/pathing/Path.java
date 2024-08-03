// Copyright (C) Marcus Kauffman 2023-Present

// This work would not have been possible without the work of many
// contributors, most notably Colin Montigel. See ACKNOWLEDGEMENT.md for
// more details.

// This file is part of Quail.

// Quail is free software: you can redistribute it and/or modify it
// underthe terms of the GNU General Public License as published by the
// Free Software Foundation, version 3.

// Quail is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details.

// You should have received a copy of the GNU General Public License
// along with Quail. If not, see <https://www.gnu.org/licenses/>

package com.mineinjava.quail.pathing;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;

/**
 * Represents a path that the robot can follow.
 *
 * <p>Instantiate with a list of pose2d.
 *
 * <p>The robot will follow the path by going to each pose in order.
 *
 * <p>For example, I would pass in poses =[[0,0,0], [1,0,0], [1,1,0], [0,1,0]] to make the robot
 * travel in a 1 unit square
 *
 * <p>The robot will go to [0,0], then [1,0], then [1,1], then [0,1] always facing '0'
 *
 * <p>The robot will stop at the last point in the path, and its heading will be the heading of the
 * last pose in the path.
 *
 * <ul>
 *   <li>The `LAST POINT` is the last point that the robot hit.
 *   <li>The `CURRENT POINT` is the point that the robot is currently pathing towards
 *   <li>The NEXT POINT is the point that the robot will path towards after reaching the CURRENT
 *       POINT
 * </ul>
 */
public class Path {
  public final ArrayList<Pose2d> points;
  private int currentPointIndex = 0;
  public int lastPointIndex = 0;
  private boolean isFinished = false;

  /**
   * Creates a path with the specified points and final heading.
   *
   * @param points a list of pose2ds that the robot will follow in order
   */
  public Path(ArrayList<Pose2d> points) {
      // Require at least 1 point
      if (points.isEmpty()) {
          throw new IllegalArgumentException("Path must have at least 1 point.");
      }
      this.points = points;
      lastPointIndex = points.size() - 1;
  }

  public boolean isFinished() {
      return isFinished;
  }

  /**
   * Marks the path as finished, should only be internally by incrementCurrentPointIndex
   *
   * @throws IllegalStateException if the path is already finished
   */
  private void finishPath() {
      if (isFinished) {
          throw new IllegalStateException("Path is already marked as finished. Cannot finish again.");
      }
      isFinished = true;
  } 

  /**
   * Increments the current point index.
   *
   * @throws IllegalStateException if the path is already finished or if the current point index is greater than the last point index
   */
  public void incrementCurrentPointIndex() {
      // If we are finished, we should not be able to increment the current point index
      if (isFinished) {
          throw new IllegalStateException(
                  "Path is already marked as finished. Cannot increment current point index.");
      }

      // Check if we are at the last point, if so mark the path as finished
      if (currentPointIndex == lastPointIndex) {
          this.finishPath();
          return;
      } else if (currentPointIndex > lastPointIndex) {
          throw new IllegalStateException(
                  "Path is already marked as finished. Cannot increment current point index.");
      }

      currentPointIndex++;
  }

  /**
   * Gets the next point in the path. 
   *
   * <p> If the path is finished, or if we are on the last point,
   * returns null.
   *
   * @return the next point in the path, or null
   */
  public Pose2d getNextPoint() {
     if (isFinished) {
       return null;
     }

     if (currentPointIndex + 1 > lastPointIndex) {
       return null;
     }

     return points.get(currentPointIndex + 1);
  }

    /**
     * Gets the index of the current point in the path.
     * 
     * <p> may be out of bounds
     */
  public int getCurrentPointIndex() {
      return currentPointIndex;
  }

  /** Returns the current point in the path. */
  public Pose2d getCurrentPoint() {
      if (isFinished) {
          throw new IllegalStateException("Path is finished, there is no current point");
      }
      if (currentPointIndex <= lastPointIndex) {
          return points.get(currentPointIndex);
      } 
      return null;
      
  }
  /**
   * Gets the point at the specified index relative to the current point.
   *
   * <p>If the path is finished, returns null
   *
   * @throws IllegalArgumentException if the index is out of bounds
   * @param index the offset from the current point
   * @return the point at the specified index relative to the current point, or null if the path is finished
   */
  public Pose2d getPointRelativeToCurrent(int index) {
      if (isFinished) {
          return null;
      }
        int newIndex = currentPointIndex + index;
        if (newIndex < 0 || newIndex > lastPointIndex) {
        throw new IllegalArgumentException(
           "Cannot get point at index " + index + " because it would be out of bounds.");}
      return points.get(newIndex);
  }

  /** Returns a vector from the passed pose to the current point. */
  public Vec2d vectorToCurrentPoint(Pose2d point) {
      if (isFinished) {
          return null;
      }
      return point.vectorTo(this.getCurrentPoint());
  }

  /** Returns the overall length of the path assuming the robot paths on straight lines. */
  public double length() {
      double length = 0;

      for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distanceTo(points.get(i + 1));
      }
      return length;
  }

  /**
   * Returns the distance from the passed point to the next point.
   *
   * @param point the point to calculate the distance from
   * @throws IllegalStateException if the path is finished
   * @return the distance from the current point to the next point
   */
  public double distanceToNextPoint(Pose2d point) {
      if (isFinished) {
          throw new IllegalStateException("Path is finished. There is no next point.");
      }
      return point.distanceTo(this.getNextPoint());
  }

  /**
   * Returns the distance from the passed point to the current point.
   *
   * @param point the point to calculate the distance from
   * @throws IllegalStateException if the path is finished
   * @return the distance from the current point to the next point
   */
  public double distanceToCurrentPoint(Pose2d point) {
      if (isFinished) {
          throw new IllegalStateException("Path is finished. There is no current point");
      }
      return point.distanceTo(this.getCurrentPoint());
  }

  /** Returns a vector from the last point to the current point.
   *
   * @throws IllegalStateException if the path is finished
   */
  public Vec2d vectorLastToCurrentPoint() {
      if (isFinished) {
          throw new IllegalStateException("Path is finished. There is no current point");
      }
      if (this.getCurrentPointIndex() == 0) {
              return new Vec2d(0);
      }
      return this.getPointRelativeToCurrent(-1).vectorTo(this.getCurrentPoint());
  }

  /** Returns the distance from last point to current point. */
  public double distanceLastToCurrentPoint() {
      return vectorLastToCurrentPoint().getLength();
  }

  /**
   * Next movement vector for lookahead.
   *
   * @return a vector from the current point to the next point
   */
  public Vec2d nextMovementVector() {
      if (isFinished) {
          return null;
      }
      if (currentPointIndex >= lastPointIndex){
          return null; // TODO: make sure this doesn't break anything
                       // (ensure robot stops)
      }
      return this.getCurrentPoint().vectorTo(this.getNextPoint());
  }

  /**
   * Calculates a vector from (x,y) to the nearest point on the path.
   *
   * <p>The index of the nearest point must be greater than or equal to minIndex
   *
   * @param point the point to calculate the vector from
   * @param minIndex the index of the nearest point must be greater than or equal to minIndex
   * @return a vector from (x,y) to the nearest point on the path
   */
  public Vec2d vectorToNearestPoint(Pose2d point, int minIndex) {
      Pose2d nearestPoint = this.nearestPoint(point, minIndex);
      return new Vec2d(nearestPoint.x - point.x, nearestPoint.y - point.y);
  }

  /**
   * Calculates the index of the nearest point on the path.
   *
   * <p>The index of the nearest point must be greater than or equal to minIndex
   *
   * @param point
   * @param minIndex
   * @return
   */
  public int nearestPointIndex(Pose2d point, int minIndex) {
      Pose2d nearestPoint = this.nearestPoint(point, minIndex);
      return points.indexOf(nearestPoint);
  }

  /**
   * Calculates the nearest point on the path.
   *
   * <p>The index of the nearest point must be greater than or equal to minIndex
   * 
   * <p> In case of a tie, the point that occurs earlier on the path is
   * returned
   *
   * @param point the point relative to which you want to find the
   * nearest point on the path
   * @param minIndex the index of the nearest point must be greater than or equal to minIndex
   * @throws IllegalArgumentException if there are no points between
   * minIndex and the end of the path.
   * @return the nearest point on the path
   * 
   * @bernstern I like this way of doing it better
   */
  public Pose2d nearestPoint(Pose2d point, int minIndex) {
      if (minIndex >= lastPointIndex) {
          throw new IllegalArgumentException("No points are left to search through");
      }

      Pose2d nearestPoint = points.get(minIndex);
      double nearestDistance = point.distanceTo(nearestPoint);

      for (int i = minIndex; i < points.size(); i++) {
          Pose2d p = points.get(i);
          double distance = point.distanceTo(p);

          if (distance < nearestDistance) {
              nearestPoint = p;
              nearestDistance = distance;
          }
      }
      return nearestPoint;
  }

  /** 
   * * Given a position, calculate the distance from that position to the next point then the remaining distance on the path
   * @param position where to calculate the distance to the currentPoint
   * @return the total length remaining */
  public double remainingLength(Pose2d position) {
      double length = 0;

      // If the path is complete, throw an illegal state
      if (isFinished) {
          throw new IllegalStateException("Path is marked is complete, remainingLength should not be called");
      }

      // Calculate the distance from position to the first point left in the path
      Pose2d firstPoint = points.get(currentPointIndex);
      length += position.distanceTo(firstPoint);

      // Go through the remainder of the path and calculate how much distance is left
      for (int i = currentPointIndex; i < lastPointIndex; i++) {
          Pose2d p1 = points.get(i);
          Pose2d p2 = points.get(i + 1);
          length += p1.distanceTo(p2);
      }

      return length;
  }
}
