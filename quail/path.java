package quail;

import java.util.ArrayList;

/**
 * Represents a path that the robot can follow.
 * Instantiate with a list of points.
 */
public class path {
    public ArrayList<double[]> points;
    int currentPoint = 0;
    double finalHeading = 0;
    public path(ArrayList<double[]> points) {
        this.points = points;
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
     * @param x
     * @param y
     * @param minIndex
     * @return
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
     * @param x
     * @param y
     * @param minIndex
     * @return
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
