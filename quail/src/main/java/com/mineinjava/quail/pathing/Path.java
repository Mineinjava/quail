package com.mineinjava.quail.pathing;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Represents a path that the robot can follow. Instantiate with a list of pose2d.
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
 * <p>the LAST POINT is the last point that the robot hit the CURRENT POINT is the point that the
 * robot is currently pathing towards the NEXT POINT is the point that the robot will path towards
 * after reaching the CURRENT POINT
 */
public class Path {
  public final ArrayList<Pose2d> points;
  private int currentPointIndex = 0;
  private final int lastPointIndex;
  private boolean isFinished = false;

  /**
   * creates a path with the specified points and final heading.
   *
   * @param points - a list of pose2ds that the robot will follow in order
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
   * Gets the next point in the path. If the path is finished, or if we are on the last point,
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

  public int getCurrentPointIndex() {
    return currentPointIndex;
  }

  /**
   * Gets the current point in the path. If the path is finished, returns null.
   *
   * @return the current point in the path
   */
  public Pose2d getCurrentPoint() {
    if (isFinished) {
      return null;
    }

    return points.get(currentPointIndex);
  }

  /**
   * Gets the point at the specified index relative to the current point. If the path is finished, returns null
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
          "Cannot get point at index " + index + " because it would be out of bounds.");
    }

    return points.get(newIndex);
  }

  /**
   * @return a vector from the passed pose to the current point
   */
  public Vec2d vectorToCurrentPoint(Pose2d point) {
    if (isFinished) {
      return null;
    }
    // TODO: Confirm changing this doesn't have issue w/ heading subtraction, should be fine since it's not headingVec
    return this.getCurrentPoint().minus(point).vec();
  }

  /** returns the overall length of the path assuming the robot paths on straight lines */
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
   * @param point - the point to calculate the distance from
   * @return - the distance from the current point to the next point
   */
  public double distanceToNextPoint(Pose2d point) {
    return point.distanceTo(this.getNextPoint());
  }

  /**
   * Returns the distance from the passed point to the current point
   *
   * @throws IllegalStateException if the path is finished
   * @param point - the point to calculate the distance from
   * @return - the distance from the current point to the next point
   */
  public double distanceToCurrentPoint(Pose2d point) {
    if (isFinished) {
      throw new IllegalStateException("Path is finished. Cannot calculate distance to current point.");
    }

    return point.distanceTo(this.getCurrentPoint());
  }

  /**
   * @return a vector from the last point to the current point
   */
  public Vec2d vectorLastToCurrentPoint() {
    if (isFinished) {
      return null;
    }

    // Check if this is the first point, if so return a vector of 0,0
    if (currentPointIndex == 0) {
      return new Vec2d(0, 0);
    }

    // Otherwise, return the vector from the last point to the current point
    // TODO: Confirm changing this doesn't have issue w/ heading subtraction, should be fine since it's not headingVec
    return this.getCurrentPoint().minus(this.getPointRelativeToCurrent(-1)).vec();
  }

  /**
   * @return distance from last point to current point
   */
  public double distanceLastToCurrentPoint() {
    return vectorLastToCurrentPoint().getLength();
  }

  /**
   * next movement vector for lookahead
   *
   * @return a vector from the current point to the next point
   */
  public Vec2d nextMovementVector() {
    if (isFinished) {
      return null;
    }

    // Check if we are at the last point, if so return null
    if (currentPointIndex == lastPointIndex) {
      return null;
    }

    return this.getNextPoint().minus(this.getCurrentPoint()).vec();
  }

  /**
   * Calculates a vector from (x,y) to the nearest point on the path. The index of the nearest point
   * must be greater than or equal to minIndex
   *
   * @param point - the point to calculate the vector from
   * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
   * @return - a vector from (x,y) to the nearest point on the path
   */
  public Vec2d vectorToNearestPoint(Pose2d point, int minIndex) {
    Pose2d nearestPoint = this.nearestPoint(point, minIndex);
    return new Vec2d(nearestPoint.x - point.x, nearestPoint.y - point.y);
  }

  /**
   * Calculates the index of the nearest point on the path. The index of the nearest point must be
   * greater than or equal to minIndex
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
   * Calculates the nearest point on the path. The index of the nearest point must be greater than
   * or equal to minIndex
   *
   * Note: right now when there is a tie the first point at that distance will be returned
   *
   * @param point the point which you want to find the nearest point on the path
   * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
   * @return - the nearest point on the path
   */
  public Pose2d nearestPoint(Pose2d point, int minIndex) {
    if (minIndex > lastPointIndex) {
      throw new IllegalArgumentException("Unable to use a minIndex greater than the last point index in nearestPoint");
    }

    // If min index is < 0, update it to 0 and TODO: throw a warning log
    minIndex = max(minIndex, 0);

    Pose2d nearestPoint = null, p;

    // TODO(Bernie, pre merge): Change to infinity
    double minDistance = 10000, distance;

    for (int i = minIndex; i <= lastPointIndex; i++) {
      p = points.get(i);

      // Get the distance from the p we are iterating on to point
      distance = point.distanceTo(p);

      // TODO: behavior when there is a tie
      if (distance < minDistance) {
        minDistance = distance;
        nearestPoint = p;
      }
    }

    if (nearestPoint == null) {
      throw new IllegalStateException("This should be impossible, was unable to find a nearest point given >1 point");
    }

    return nearestPoint;
  }

  /**
   * Given a position, calculate the distance from that position to the next point then the remaining distance on the path
   * @param position where to calculate the distance to the currentPoint
   * @return the total length remaining
   */
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
