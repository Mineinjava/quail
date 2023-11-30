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
 * For example, I would pass in poses =[[0,0,0], [1,0,0], [1,1,0], [0,1,0]] to make the robot travel in a square
 *
 * The robot will go to [0,0], then [1,0], then [1,1], then [0,1] always facing '0'
 *
 * The robot will stop at the last point in the path, and its heading will be the heading of the last pose in the path.
 *
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
    /** returns the next point in the path. Also increments the current point.
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
    /** returns the overall length of the path.
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
    /** returns the distance from the current point to the next point.
    *@param point - the point to calculate the distance from
    *@return - the distance from the current point to the next point
    */
    public double distanceToNextPoint(Pose2d point){
        Pose2d nextPoint = this.getNextPoint();
        if(nextPoint == null){
            return 0;
        }
        return Math.sqrt(Math.pow(point.x - nextPoint.x, 2) + Math.pow(point.y - nextPoint.y, 2));
    }
    /** calculates a vector from (x,y) to the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     * 
     * @param point - the point to calculate the vector from
     * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
     * @return - a vector from (x,y) to the nearest point on the path
     */
    public Vec2d vectorToNearestPoint(Pose2d point, int minIndex){
        Pose2d nearestPoint = this.nearestPoint(point, minIndex);
        return new Vec2d(nearestPoint.x - point.x, nearestPoint.y - point.y);

    }
    /** calculates the index of the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     *
     * @param point
     * @param minIndex
     * @return
     */
    public int nearestPointIndex(Pose2d point, int minIndex){
        Pose2d nearestPoint = this.nearestPoint(point, minIndex);
        return points.indexOf(nearestPoint);

    }
    /** calculates the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     *
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
}