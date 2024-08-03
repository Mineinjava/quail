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

package quail.pathing;

import com.mineinjava.quail.RobotMovement;
import com.mineinjava.quail.localization.KalmanFilterLocalizer;
import com.mineinjava.quail.pathing.ConstraintsPair;
import com.mineinjava.quail.pathing.Path;
import com.mineinjava.quail.pathing.PathFollower;
import com.mineinjava.quail.util.MiniPID;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** PathFollowerTest */
public class PathFollowerTest {

  Pose2d poseStart = new Pose2d(0, 0, 0);
  Pose2d poseSecond = new Pose2d(1, 0, 0);
  Pose2d poseThird = new Pose2d(1, 1, 0);
  Pose2d poseFourth = new Pose2d(0, 1, 0);
  Pose2d poseEnd = new Pose2d(0, 2, 0);

  double simulatedtime;

  Path path;
  PathFollower pathFollower;
  @BeforeEach
  void setUp() {
    path =
        new Path(
            new ArrayList<Pose2d>() {
              {
                add(poseStart);
                add(poseSecond);
                add(poseThird);
                add(poseFourth);
                add(poseEnd);
              }
            });
    KalmanFilterLocalizer localizer = new KalmanFilterLocalizer(new Pose2d().vec(), 1d);
    ConstraintsPair translationPair = new ConstraintsPair(1, 10);
    ConstraintsPair rotationPair = new ConstraintsPair(2, 20);

    MiniPID turnController = new MiniPID(1, 0, 0);
    double precision = 0.1;
    double slowDownDistance = 0d;
    double kP = 1;
    double minVelocity = 0;

pathFollower = new PathFollower(localizer, path, translationPair, rotationPair, turnController, precision, slowDownDistance, kP, minVelocity);

    simulatedtime = 0d;
  }

  // Helper to move the path index forward
  private void movePathIndexForward(int times) {
    for (int i = 0; i < times; i++) {
      path.incrementCurrentPointIndex();
    }
  }

  @Test
  void throwsIfPoseIsNull() {
    this.pathFollower.setLocalizer(null);
    assertThrows(NullPointerException.class, pathFollower::calculateNextDriveMovement);
  }

  @Test
  void noMovementIfPathIsFinished(){
    this.movePathIndexForward(5);
    assertEquals(new Vec2d(0), this.pathFollower.calculateNextDriveMovement().translation);
    assertEquals(0, this.pathFollower.calculateNextDriveMovement().rotation);
  }
  
  @Test
  void incrementPathIndexIfPointHit(){
    
  }

}
