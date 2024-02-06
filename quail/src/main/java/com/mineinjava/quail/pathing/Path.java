package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

/**
 * Represents a path that the robot can follow.
 * Instantiate with a list of pose2d.
 *
 * The robot will follow the path by going to each pose in order.
 *
 * For example, I would pass in poses =[[0,0,0], [1,0,0], [1,1,0], [0,1,0]] to make the robot travel in a 1 unit square
 *
 * The robot will go to [0,0], then [1,0], then [1,1], then [0,1] always facing '0'
 *
 * The robot will stop at the last point in the path, and its heading will be the heading of the last pose in the path.
 *
 * the LAST POINT is the last point that the robot hit
 * the CURRENT POINT is the point that the robot is currently pathing towards
 * the NEXT POINT is the point that the robot will path towards after reaching the CURRENT POINT
 */
public class Path {
    public ArrayList<Pose2d> points;
    public int currentPointIndex = 0;
    public int lastPointIndex = 0;

    /** creates a path with the specified points and final heading.
     *
     * @param points - a list of pose2ds that the robot will follow in order
     */
    public Path(ArrayList<Pose2d> points) {
        this.points = points;
        lastPointIndex = points.size() - 1;
    }

    /** returns the next point in the path.
     */
    public Pose2d getNextPoint() {
        if (currentPointIndex < lastPointIndex) {
            return points.get(currentPointIndex + 1);
        } else {
            return null;
        }
    }

    /** returns the current point in the path.
     */
    public Pose2d getCurrentPoint() {
        if (currentPointIndex <= lastPointIndex) {
            return points.get(currentPointIndex);
        } else {
            return null;
        }
    }

    /** returns the point at the specified index relative to the current point.
     */
    public Pose2d getPointRelativeToCurrent(int index){
        if (currentPointIndex + index < points.size()) {
            return points.get(currentPointIndex + index);
        } else {
            return null;
        }
    }

    /**
     * @return a vector from the passed pose to the current point
     */
    public Vec2d vectorToCurrentPoint(Pose2d point){
        Pose2d nextPoint = this.getCurrentPoint();
        if(nextPoint == null){
            return null;
        }
        return new Vec2d(nextPoint.x - point.x, nextPoint.y - point.y);
    }

    /** returns the overall length of the path assuming the robot paths on straight lines
     */
    public double length(){
        double length = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            Pose2d p1 = points.get(i);
            Pose2d p2 = points.get(i + 1);
            length += Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }
        return length;
    }

    /**
    * Returns the distance from the passed point to the next point.
    * @param point - the point to calculate the distance from
    * @return - the distance from the current point to the next point
    */
    public double distanceToNextPoint(Pose2d point){
        Pose2d nextPoint = this.getNextPoint();
        if(nextPoint == null){
            return 0;
        }
        return Math.sqrt(Math.pow(point.x - nextPoint.x, 2) + Math.pow(point.y - nextPoint.y, 2));
    }
    /**
    * Returns the distance from the passed point to the current point
    * @param point - the point to calculate the distance from
    * @return - the distance from the current point to the next point
    */
    public double distanceToCurrentPoint(Pose2d point){
        Pose2d currentPoint = this.getCurrentPoint();
        if(currentPoint == null){
            return 0;
        }
        return Math.sqrt(Math.pow(point.x - currentPoint.x, 2) + Math.pow(point.y - currentPoint.y, 2));
    }
    /**
     * @return a vector from the last point to the current point
     * TODO: Convert to camelCase
     */
    public Vec2d vector_last_to_current_point(){
        Pose2d currentPoint = this.getCurrentPoint();
        if(currentPoint == null){
            return null;
        }
        Pose2d lastPoint = this.getPointRelativeToCurrent(-1);
        if(lastPoint == null){
            return null;
        }
        return new Vec2d(currentPoint.x - lastPoint.x, currentPoint.y - lastPoint.y);
    }

    /**
     * @return distance from last point to current point
     */

    public double distance_last_to_current_point(){
        return vector_last_to_current_point().getLength();
    }
  
    /**
     * next movement vector for lookahead
     * @return a vector from the current point to the next point
     */
    public Vec2d nextMovementVector(){
        Pose2d currentPoint = this.getCurrentPoint();
        if(currentPoint == null){
            return null;
        }
        Pose2d nextPoint = this.getNextPoint();
        if(nextPoint == null){
            return null;
        }
        return new Vec2d(nextPoint.x - currentPoint.x, nextPoint.y - currentPoint.y);
    }

    /**
     * Calculates a vector from (x,y) to the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     * @param point - the point to calculate the vector from
     * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
     * @return - a vector from (x,y) to the nearest point on the path
     */
    public Vec2d vectorToNearestPoint(Pose2d point, int minIndex){
        Pose2d nearestPoint = this.nearestPoint(point, minIndex);
        return new Vec2d(nearestPoint.x - point.x, nearestPoint.y - point.y);

    }

    /**
     * Calculates the index of the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     * @param point
     * @param minIndex
     * @return
     */
    public int nearestPointIndex(Pose2d point, int minIndex){
        Pose2d nearestPoint = this.nearestPoint(point, minIndex);
        return points.indexOf(nearestPoint);

    }
    /**
    * Calculates the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     * @param point
     * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
     * @return - the nearest point on the path
     */
    public Pose2d nearestPoint(Pose2d point, int minIndex){
        Pose2d nearestPoint = points.get(minIndex);
        double nearestDistance = Math.sqrt(Math.pow(point.x - nearestPoint.x, 2) + Math.pow(point.y - nearestPoint.y, 2));

        for (int i = minIndex; i < points.size(); i++) {
            Pose2d p = points.get(i);
            double distance = Math.sqrt(Math.pow(point.x - point.x, 2) + Math.pow(point.y - point.y, 2));

            if (distance < nearestDistance) {
                nearestPoint = p;
                nearestDistance = distance;
            }
        }
        return nearestPoint;
    }

    /** returns the remaining length of the path.
     */
    public double remainingLength(Pose2d position){
        double length = 0;

        for (int i = currentPointIndex; i < points.size() - 1; i++) {
            Pose2d p1 = points.get(i);
            Pose2d p2 = points.get(i + 1);
            length += Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }

        if (currentPointIndex < points.size()) {
            Pose2d firstPoint = points.get(currentPointIndex);
            length += Math.sqrt(Math.pow(position.x - firstPoint.x, 2) + Math.pow(position.y - firstPoint.y, 2));
        }

        return length;
    }
}
