package com.mineinjava.quail.localization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Angle;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

public abstract class TwoWheelLocalizer{
    
    List<Pose2d> wheelPoses;
    DecompositionSolver forwardSolver;
    
    private Pose2d poseEstimate, poseVelocity;
    private List<Double> lastWheelPositions = new ArrayList<>();
    private double lastHeading;

    public TwoWheelLocalizer(List<Pose2d> wheelPoses) {
        this.wheelPoses = wheelPoses;

        Array2DRowRealMatrix inverseMatrix = new Array2DRowRealMatrix(3, 3);
        for (int i = 0; i <= 1; i++) {
            Vec2d orientationVector = wheelPoses.get(i).headingVec();
            Vec2d positionVector = wheelPoses.get(i).vec();

            inverseMatrix.setEntry(i, 0, orientationVector.x);
            inverseMatrix.setEntry(i, 1, orientationVector.y);
            inverseMatrix.setEntry(
                i, 
                2,
                positionVector.x * orientationVector.y - positionVector.y * orientationVector.x
            );
        }
        inverseMatrix.setEntry(2, 2, 1.0);

        forwardSolver = new LUDecomposition(inverseMatrix).getSolver();

        /*
        if (forwardSolver.isNonSingular()) {
            throw new IllegalArgumentException("The specified configuration cannot support full localization");
        }
        */
    }

    private Pose2d calculatePoseDelta(List<Double> wheelDeltas, double headingDelta) {
        RealMatrix inputMatrix = MatrixUtils.createRealMatrix(new double[][]{toDoubleArray(wheelDeltas, headingDelta)}).transpose();
        RealMatrix rawPoseDelta = forwardSolver.solve(inputMatrix);

        return new Pose2d(
                rawPoseDelta.getEntry(0, 0),
                rawPoseDelta.getEntry(1, 0),
                rawPoseDelta.getEntry(2, 0)
        );
    }

    public void update() {
        List<Double> wheelPositions = getWheelPositions();
        double heading = getHeading();
        
        if (!lastWheelPositions.isEmpty()) {
            List<Double> wheelDeltas = new ArrayList<>();
            for (int i = 0; i < wheelPositions.size(); i++) {
                wheelDeltas.add(wheelPositions.get(i) - lastWheelPositions.get(i));
            }
            
            double headingDelta = Angle.normDelta(heading - lastHeading);
            Pose2d robotPoseDelta = calculatePoseDelta(wheelDeltas, headingDelta);
            poseEstimate = relativeOdometryUpdate(poseEstimate, robotPoseDelta);
        }
    
        List<Double> wheelVelocities = getWheelVelocities();
        Double headingVelocity = getHeadingVelocity();
        
        if (wheelVelocities != null && headingVelocity != null) {
            poseVelocity = calculatePoseDelta(wheelVelocities, headingVelocity);
        }
    
        lastWheelPositions = wheelPositions;
        lastHeading = heading;
    }

    public Pose2d getPoseEstimate() {
        return poseEstimate;
    }

    /**
     * Returns the positions of the tracking wheels in the desired distance units (not encoder counts!)
     */
    public abstract List<Double> getWheelPositions();

    /**
     * Returns the velocities of the tracking wheels in the desired distance units (not encoder counts!)
     */
    public List<Double> getWheelVelocities() {
        return null; // You can return an actual List<Double> implementation here if needed
    }

    /**
     * Returns the heading of the robot (usually from a gyroscope or IMU).
     */
    public abstract double getHeading();

    /**
     * Returns the heading velocity of the robot (usually from a gyroscope or IMU).
     */
    public Double getHeadingVelocity() {
        return null; // You can return a Double value here if needed
    }

    public static Pose2d relativeOdometryUpdate(Pose2d fieldPose, Pose2d robotPoseDelta) {
        double dtheta = robotPoseDelta.heading;
        double sineTerm, cosTerm;
        
        if (MathUtil.epsilonEquals(dtheta, 0.0)) {
            sineTerm = 1.0 - dtheta * dtheta / 6.0;
            cosTerm = dtheta / 2.0;
        } else {
            sineTerm = Math.sin(dtheta) / dtheta;
            cosTerm = (1 - Math.cos(dtheta)) / dtheta;
        }

        double xDelta = sineTerm * robotPoseDelta.x - cosTerm * robotPoseDelta.y;
        double yDelta = cosTerm * robotPoseDelta.x + sineTerm * robotPoseDelta.y;
        
        Vec2d fieldPositionDelta = new Vec2d(xDelta, yDelta);
        
        Pose2d fieldPoseDelta = new Pose2d(
            fieldPositionDelta.rotate(fieldPose.heading, true),
            robotPoseDelta.heading
        );
        
        double newX = fieldPose.x + fieldPoseDelta.x;
        double newY = fieldPose.y + fieldPoseDelta.y;
        double newHeading = Angle.norm(fieldPose.heading + fieldPoseDelta.heading);
        
        return new Pose2d(newX, newY, newHeading);
    }

    private double[] toDoubleArray(List<Double> list, double value) {
        double[] array = new double[list.size() + 1];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        array[list.size()] = value;
        return array;
    }
}
