package com.mineinjava.quail.odometry;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.mineinjava.quail.util.Vec2d;

/**
 * Represents a path that the robot can follow.
 * Instantiate with a list of points.
 *
 * The robot will follow the path by going to each point in order.
 *
 * For example, I would pass in points=[[0,0], [1,0], [1,1], [0,1]] to make the robot travel in a square
 *
 * The robot will go to [0,0], then [1,0], then [1,1], then [0,1]
 *
 * The robot will stop at the last point in the path, and its heading will be the finalHeading that is passed in.
 *
 */
public class path {
    public ArrayList<double[]> points;
    int currentPoint = 0;
    int lastpoint = 0;
    double finalHeading = 0;

    /** creates a path with the specified points and final heading.
     *
     * @param points - a list of points that the robot will follow, in the form of [x, y] where x and y are both doubles.
     * @param finalHeading - the heading that the robot will be at when it reaches the end of the path.
     */
    public path(ArrayList<double[]> points, double finalHeading) {
        this.points = points;
        this.finalHeading = finalHeading;
        lastpoint = points.size() - 1;
    }
    /** returns the next point in the path. Also increments the current point.
     */
    public double[] getNextPoint() {
        if (currentPoint < points.size()) {
            return points.get(currentPoint++);
        } else {
            return null;
        }
    }
    /** returns the current point in the path.
     */
    public double[] getCurrentPoint() {
        if (currentPoint < points.size()) {
            return points.get(currentPoint);
        } else {
            return null;
        }
    }
    /** returns the point at the specified index relative to the current point.
     */
    public double[] getPointRelativeToCurrent(int index){
        if (currentPoint + index < points.size()) {
            return points.get(currentPoint + index);
        } else {
            return null;
        }
    }
    /** returns the overall length of the path.
     */
    public double length(){
        double length = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            double[] p1 = points.get(i);
            double[] p2 = points.get(i + 1);
            length += Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
        }
        return length;
    }

    /** calculates a vector from (x,y) to the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     *
     * @param x - x
     * @param y - y
     * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
     * @return - a vector from (x,y) to the nearest point on the path
     */
    public Vec2d vectorToNearestPoint(double x, double y, int minIndex){
        double[] nearestPoint = this.nearestPoint(x, y, minIndex);
        return new Vec2d(nearestPoint[0] - x, nearestPoint[1] - y);

    }
    /** calculates the index of the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     *
     * @param x
     * @param y
     * @param minIndex
     * @return
     */
    public int nearestPointIndex(double x, double y, int minIndex){
        double[] nearestPoint = this.nearestPoint(x, y, minIndex);
        return points.indexOf(nearestPoint);

    }
    /** calculates the nearest point on the path. The index of the nearest point must be greater than or equal to minIndex
     *
     * @param x - x
     * @param y - y
     * @param minIndex - the index of the nearest point must be greater than or equal to minIndex
     * @return - the nearest point on the path
     */
    public double[] nearestPoint(double x, double y, int minIndex){
        double[] nearestPoint = points.get(minIndex);
        double nearestDistance = Math.sqrt(Math.pow(x - nearestPoint[0], 2) + Math.pow(y - nearestPoint[1], 2));
        for (int i = minIndex + 1; i < points.size(); i++) {
            double[] point = points.get(i);
            double distance = Math.sqrt(Math.pow(x - point[0], 2) + Math.pow(y - point[1], 2));
            if (distance < nearestDistance) {
                nearestPoint = point;
                nearestDistance = distance;
            }
        }
        return nearestPoint;
    }
}
