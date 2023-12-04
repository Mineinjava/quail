import random

import matplotlib.pyplot as plt

# Simulation Constants
LOOPTIME = 0.2  # seconds
LOOPTIME_DEVIATION = 0  # seconds

MAX_VELOCITY = 2.25  # m/s
MAX_ACCELERATION = 0.5  # m/s^2
MAX_ANGULAR_VELOCITY = 1.0  # rad/s
MAX_ANGULAR_ACCELERATION = 1.0  # rad/s^2
CRUISE_VELOCITY = 0.3 # m/s

PRECISION_RADIUS = 0.05
SLOW_DOWN_RADIUS = 0.5

USE_SPLINE = True
SPLINE_RESOLUTION = 30

SHOW_GRID = True


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
    ipoints = [(0,0), (1,1), (2,0), (3,1), (4,0), (5,1)]
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
    if USE_SPLINE:
        splinex, spliney = cubicSpline.interpolate_xy(splinex, spliney, SPLINE_RESOLUTION)
    points = list(zip(splinex, spliney))
    print(points)

    WAYPOINTS = [Waypoint(point[0], point[1], 0) for point in points]
    robotPose = INITIAL_POSE
    robotPoseHistory = [robotPose]

    velocities = []
    accelerations = []

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
            desiredvel *= CRUISE_VELOCITY

        desiredvel *= LOOPTIME
        if desiredvel.length() > MAX_VELOCITY:
            desiredvel /= desiredvel.length()
            desiredvel *= MAX_VELOCITY

        desiredvel /= LOOPTIME

        accel = (desiredvel - vel) / LOOPTIME
        if accel.length() > MAX_ACCELERATION:
            accel /= accel.length()
            accel *= MAX_ACCELERATION

        accelerations.append(accel.length())

        robotPose.velocity = vel + (accel * LOOPTIME)
        velocities.append(robotPose.velocity.length())
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


    times = [i * LOOPTIME for i in range(len(velocities))]

    plt.subplot(2, 1, 1)
    plt.plot(times, velocities, 'g-', label="Velocity")
    plt.plot(times, accelerations, 'r-', label="Acceleration")
    plt.title("Acceleration and Velocity vs Time")
    plt.xlabel("Time (s)")
    plt.legend()
    plt.grid(SHOW_GRID)

    plt.subplot(2, 1, 2)
    plt.plot(x, y, 'ro')
    plt.plot([point[0] for point in ipoints],
             [point[1] for point in ipoints], 'bx-')
    plt.plot([point[0] for point in points],
             [point[1] for point in points], 'cx-')
    plt.title("Path")
    plt.ylabel("Y (m)")
    plt.xlabel("X (m)")
    plt.grid(SHOW_GRID)

    plt.show()