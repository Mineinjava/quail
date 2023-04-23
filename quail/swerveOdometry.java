package quail;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class swerveOdometry {
    public ArrayList<Vec2d> moduleVectors;
    public double x=0;
    public double y=0;
    public double theta=0;

    public swerveOdometry(Vec2d[] moduleVectors){
        this.moduleVectors = new ArrayList<Vec2d>(Arrays.asList(moduleVectors));
        assert moduleVectors.length >= 2;
    }
    public swerveOdometry(List<Vec2d> moduleVectors){
        this.moduleVectors = new ArrayList<Vec2d>(moduleVectors);
        assert moduleVectors.size() >= 2;

    }
    public swerveOdometry(ArrayList<Vec2d> moduleVectors){
        this.moduleVectors = moduleVectors;
        assert moduleVectors.size() >= 2;
    }

    private static Vec2d[] extractModuleVectors(swerveDrive drvetrain){
        ArrayList<Vec2d> moduleVectors = new ArrayList<>();
        for (swerveModuleBase module : drvetrain.swerveModules) {
            moduleVectors.add(module.position);
        }
        return moduleVectors.toArray(new Vec2d[0]);
    }
    public swerveOdometry(swerveDrive drvetrain) {
        this(extractModuleVectors(drvetrain));
    }
    public void updateDeltaOdometry(double dx, double dy, double dtheta){
        x+=dx;
        y+=dy;
        theta+=dtheta;
    }
    public void updateDeltaOdometry(Vec2d dpos, double dtheta){
        this.updateDeltaOdometry(dpos.x, dpos.y, dtheta);
    }
    public void updateOdometry(double mx, double my, double mtheta){
        x=mx;
        y=my;
        theta=mtheta;
    }
    public void updateOdometry(Vec2d pos, double mtheta){
        this.updateOdometry(pos.x, pos.y, mtheta);
    }
    public void updateOdometry(Vec2d pos){
        this.x = pos.x;
        this.y = pos.y;
    }
    public void updateDeltaOdometry(Vec2d dpos){
        this.updateDeltaOdometry(dpos.x, dpos.y, 0);
    }
    /**
     * Calculates the robot's movement based on the module positions
     * NOTE: due to drift, it is recommended not to use the robot's rotation value given from this method.
     * Instead, use the robot's gyro.
     *
     * Translation units are somewhat arbitrary (depends on the units of the module positions and vectors passed in)
     * Rotation units are radians
     *
     * @param modules the positions of the modules
     * @return the robot's movement
     */
    public robotMovement calculateOdometry(ArrayList<Vec2d> modules){
        // to account for errors, we will take the average of all the module pairs
        List<List<Vec2d>> modulePairs = PairMaker.getPairs(modules.toArray(new Vec2d[0]));
        // lists to be averaged over
        List<Double> rotationValues = new ArrayList<>();
        List<Vec2d> movementVectors = new ArrayList<>();
        // for each pair of modules, calculate the movement vector and rotation speed
        for (List<Vec2d> pair : modulePairs) {
            // make it easier to access modules
            Vec2d module1 = pair.get(0);
            Vec2d module2 = pair.get(1);
            // also calculate the rotation vectors (needed for calculations)
            Vec2d module1RotationVector = this.moduleVectors.get(modules.indexOf(module1)).rotate(-Math.PI/2, false);
            Vec2d module2RotationVector = this.moduleVectors.get(modules.indexOf(module2)).rotate(-Math.PI/2, false);

            // calculate the difference between the module vectors
            Vec2d moduleDifference = module1.subtract(module2);
            // calculate the difference between the rotation vectors
            Vec2d rotationDifference = module1RotationVector.subtract(module2RotationVector);

            // calculate the rotation speeed
            double rotationSpeed = moduleDifference.x/rotationDifference.x;
            rotationValues.add(rotationSpeed);
            // calculate the movement vector using substitution
            movementVectors.add(module1.subtract(module1RotationVector.scale(rotationSpeed)));
        }
        // average the rotation values
        double rotation = rotationValues.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        // calculate the sum of the movement vectors
        Vec2d translation = new Vec2d(0, 0);
        for (Vec2d movementVector : movementVectors) {
            translation = translation.add(movementVector);
        }
        // average the movement vectors
        translation = translation.scale((double) 1 /movementVectors.size());
        return new robotMovement(rotation, translation);
    }

    public robotMovement calculateOdometry(Vec2d[] modules){
        return this.calculateOdometry(new ArrayList<Vec2d>(Arrays.asList(modules)));
    }

    /**
     * Calculates the robot's movement based on the module positions
     * This is the recommended method to use if:
     * - the sum of all the module placement vectors is (0, 0)
     *     - this means that the sum of each module's rotation vectors will equal (0, 0), so the average of the module
     *       vectors will be the robot movement vector
     * Again, due to drift, it is recommended not to use the robot's rotation value given from this method.
     * Translation values are again arbitrary, but are the same as the ones given by the other method.
     * Rotation values are radians.
     *
     * @param modules the positions of the modules (Vec2d)
     * @return the robot's movement.
     */
    public robotMovement calculateFastOdometry(ArrayList<Vec2d> modules){
        // take average of all modules (this is the robot's movement vector)
        Vec2d averageModulePosition = new Vec2d(0, 0);
        for (Vec2d module : modules) {
            averageModulePosition = averageModulePosition.add(module);
        }
        averageModulePosition = averageModulePosition.scale((double) 1/modules.size());

        // calculate the rotation speed
        double rotation = 0;
        for (int i = 0; i < modules.size(); i++) {
            Vec2d module = modules.get(i);
            Vec2d moduleVector = this.moduleVectors.get(i);
            // inverse of inverse (forwards) kinematics: undo the addition of rotation + movement
            Vec2d scaledRotationVector = module.subtract(averageModulePosition);
            // add the scalar value
            rotation += scaledRotationVector.x/moduleVector.x;
        }
        // average the rotation speed
        rotation /= modules.size();
        return new robotMovement(rotation, averageModulePosition);
    }
}
