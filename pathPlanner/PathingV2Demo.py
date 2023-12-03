import random

import matplotlib.pyplot as plt

LOOPTIME = 0.150  # seconds

MAXVELOCITY = 2.25 #
MAXACCELERATION = 2.5  # m/s^2
MAXANGULARVELOCITY = 1.0  # rad/s
MAXANGULARACCELERATION = 1.0  # rad/s^2

CruiseVelocity = 2

PRECISION_RADIUS = 0.1
LOOPTIME_DEVIATION = 0  # seconds

SLOW_DOWN_RADIUS = 0.5


class Pose2d:
    def __init__(self, x, y, theta):
        self.x = x
        self.y = y
        self.theta = theta

    def distance(self, other):
        return ((self.x - other.x) ** 2 + (
                self.y - other.y) ** 2) ** 0.5

    def __sub__(self, other):
        return Pose2d(self.x - other.x, self.y - other.y,
                      self.theta - other.theta)

    def __add__(self, other):
        return Pose2d(self.x + other.x, self.y + other.y,
                      self.theta + other.theta)

    def __mul__(self, other):
        return Pose2d(self.x * other, self.y * other,
                      self.theta * other)

    def __truediv__(self, other):
        return Pose2d(self.x / other, self.y / other,
                      self.theta / other)

    def length(self):
        return (self.x ** 2 + self.y ** 2 + self.theta ** 2) ** 0.5

    def __str__(self):
        return "x: " + str(self.x) + ", y: " + str(
            self.y) + ", theta: " + str(self.theta)


def circle_intersect_line(pose, x1, y1, x2, y2):
    # subtract pose from each of the points -- find a better way to do this.
    x1 -= pose.x
    y1 -= pose.y
    x2 -= pose.x
    y2 -= pose.y

    dy = y2 - y1
    dx = x2 - x1
    dr = (dx ** 2 + dy ** 2) ** 0.5
    D = x1 * y2 - x2 * y1
    discriminant = PRECISION_RADIUS ** 2 * dr ** 2 - D ** 2
    if discriminant < 0:
        return False
    else:
        return True


class Waypoint(Pose2d):
    def __init__(self, x, y, theta):
        super().__init__(x, y, theta)

    def is_hit(self, robotPose, oldRobotPose):
        if self.distance(robotPose) < PRECISION_RADIUS:
            return True
        else:
            return False  # circle_intersect_line(self, robotPose.x, robotPose.y,
            #               oldRobotPose.x, oldRobotPose.y)

    def __str__(self):
        return super().__str__()


class RobotPose(Pose2d):
    def __init__(self, x, y, theta):
        super().__init__(x, y, theta)

    def __str__(self):
        return super().__str__() + ", velocity: " + str(self.velocity) \
            + ", angularVelocity: " + str(self.angularVelocity)


if __name__ == "__main__":
    INITIAL_POSE = RobotPose(0, 0, 0)
    ipoints = [(0, 12), (24, 6), (2, 1), (9, 3), (3, 9)]
    splinex = [point[0] for point in ipoints]
    spliney = [point[1] for point in ipoints]

    import cubicSpline

    # splinex, spliney = cubicSpline.interpolate_xy(splinex, spliney, 20)
    points = list(zip(splinex,spliney))
    print(points)


    WAYPOINTS = [Waypoint(point[0], point[1], 0) for point in points]
    print(WAYPOINTS)
    robotPose = INITIAL_POSE
    robotPoseHistory = [robotPose]

    def loop():
        global LOOPTIME
        drift_looptime = LOOPTIME + random.uniform(-LOOPTIME_DEVIATION,
                                                   LOOPTIME_DEVIATION)
        global robotPose
        global robotPoseHistory
        if WAYPOINTS[0].is_hit(robotPose, robotPoseHistory[-1]):
            WAYPOINTS.pop(0)
        if len(WAYPOINTS) == 0:
            return False
        vel = robotPose - robotPoseHistory[-1]
        vel /= LOOPTIME

        robotPoseHistory.append(robotPose)

        desiredvel = WAYPOINTS[0] - robotPose

        if robotPose.distance(WAYPOINTS[-1]) >= SLOW_DOWN_RADIUS:
            desiredvel /= desiredvel.length()
            desiredvel *= CruiseVelocity

        desiredvel *= LOOPTIME
        if desiredvel.length() > MAXVELOCITY:
            desiredvel /= desiredvel.length()
            desiredvel *= MAXVELOCITY

        desiredvel /= LOOPTIME

        accel = (desiredvel - vel) / LOOPTIME
        if accel.length() > MAXACCELERATION:
            accel /= accel.length()
            accel *= MAXACCELERATION

        robotPose.velocity = vel + (accel * LOOPTIME)
        robotPose += robotPose.velocity * drift_looptime
        return True


    while loop():
        pass

    x = []
    y = []
    for pose in robotPoseHistory:
        x.append(pose.x)
        y.append(pose.y)

    print("time", len(robotPoseHistory) * LOOPTIME)

    plt.plot(x, y, 'ro')
    plt.plot([point[0] for point in ipoints],
             [point[1] for point in ipoints], 'bx-')
    plt.plot([point[0] for point in points],
             [point[1] for point in points], 'cx-')
    plt.show()
