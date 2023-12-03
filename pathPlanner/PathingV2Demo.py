import random

import matplotlib.pyplot as plt

LOOPTIME = 0.250  # seconds

MAXVELOCITY = 2.25  #
MAXACCELERATION = 2  # m/s^2
MAXANGULARVELOCITY = 1.0  # rad/s
MAXANGULARACCELERATION = 1.0  # rad/s^2

CruiseVelocity = 1.3

PRECISION_RADIUS = 0.0254
LOOPTIME_DEVIATION = 0.00  # seconds

SLOW_DOWN_RADIUS = 1


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


# https://stackoverflow.com/a/1084899/13224997
def circle_intersect_line(pose, x1, y1, x2, y2):
    d = (x2 - x1, y2 - y1)
    f = (x1 - pose.x, y1 - pose.y)

    a = d[0] ** 2 + d[1] ** 2
    if a == 0:
        return False
    b = 2 * (f[0] * d[0] + f[1] * d[1])
    c = (f[0] ** 2) + (f[1] ** 2) - (PRECISION_RADIUS ** 2)

    discriminant = (b ** 2) - (4 * a * c)
    if discriminant < 0:
        return False
    else:
        discriminant = discriminant ** 0.5
        t1 = (-b - discriminant) / (2 * a)
        t2 = (-b + discriminant) / (2 * a)

        if not (0 <= t1 <= 1 or 0 <= t2 <= 1):
            return False
        else:
            return True


class Waypoint(Pose2d):
    def __init__(self, x, y, theta):
        super().__init__(x, y, theta)

    def is_hit(self, robotPose, oldRobotPose):
        if self.distance(robotPose) < PRECISION_RADIUS:
            return True
        elif (oldRobotPose.distance(robotPose) > self.distance(
                robotPose)):
            return circle_intersect_line(self, robotPose.x, robotPose.y,
                                  oldRobotPose.x, oldRobotPose.y)

    def __str__(self):
        return super().__str__()


class RobotPose(Pose2d):
    def __init__(self, x, y, theta):
        super().__init__(x, y, theta)

    def __str__(self):
        return super().__str__() + ", velocity: "\
            + ", angularVelocity: "


if __name__ == "__main__":
    INITIAL_POSE = RobotPose(0, 0, 0)
    ipoints = [(0,0), (0, 12), (3, 6), (0, 1), (2, 0), (3, 1)]
    splinex = [point[0] for point in ipoints]
    spliney = [point[1] for point in ipoints]
    import numpy as np

    h1 = plt.plot([], [], 'rx-')
    plt.xlim(min(splinex) - 2, max(splinex) + 2)
    plt.ylim(min(spliney) - 2, max(spliney) + 2)
    def update_line(hl, new_data):
        hl.set_xdata(np.append(hl.get_xdata(), new_data[0]))
        hl.set_ydata(np.append(hl.get_ydata(), new_data[1]))
        plt.autoscale()
        plt.draw()
        plt.pause(0.01)

    import cubicSpline

    splinex, spliney = cubicSpline.interpolate_xy(splinex, spliney, 20)
    points = list(zip(splinex, spliney))
    print(points)

    WAYPOINTS = [Waypoint(point[0], point[1], 0) for point in points]
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
        # update_line(h1[0], (robotPose.x, robotPose.y))
        return True

    import time
    start = time.perf_counter()
    while loop():
        pass
    end = time.perf_counter()
    print("time", end - start, "s, average time", 1000 * (end - start) / len(robotPoseHistory), "ms")


    x = []
    y = []
    for pose in robotPoseHistory:
        x.append(pose.x)
        y.append(pose.y)

    print("simulated time: ", len(robotPoseHistory) * LOOPTIME, "s")

    plt.plot(x, y, 'ro')
    plt.plot([point[0] for point in ipoints],
             [point[1] for point in ipoints], 'bx-')
    plt.plot([point[0] for point in points],
             [point[1] for point in points], 'cx-')
    plt.show()
