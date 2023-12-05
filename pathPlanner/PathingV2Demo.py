from math import sqrt
import random

import matplotlib.pyplot as plt

PATH = [(-36, 48), (-24,36), (-12, 0), (-12,-64)]
START_POSE = (-36, 48, 0)

# Simulation Constants
LOOPTIME = 0.02  # seconds
LOOPTIME_DEVIATION = 0.0  # seconds

MAX_VELOCITY = 62  # your unit per second
MAX_ACCELERATION = 25  # your unit per second squared
MAX_ANGULAR_VELOCITY = 1.0  # rad/s
MAX_ANGULAR_ACCELERATION = 1.0  # rad/s^2
CRUISE_VELOCITY = 30 # your unit per second

PRECISION_RADIUS = 2 # your unit
SLOW_DOWN_RADIUS = 10 # your unit

USE_SPLINE = True
# USE_SPLINE = False
SPLINE_RESOLUTION = 20

ANIMATE = True

SHOW_ACCEL_VELO_GRAPH = False
SHOW_GRID = True
GRID_INCREMENT = 24 # your unit

FIELD_IMAGE = "./centerstage.png"
FIELD_WIDTH = 144 # your unit
FIELD_LENGTH = 144 # your unit

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
    INITIAL_POSE = RobotPose(START_POSE[0], START_POSE[1], START_POSE[2])
    ipoints = PATH
    splinex = [point[0] for point in ipoints]
    spliney = [point[1] for point in ipoints]
    import numpy as np

    half_width = FIELD_WIDTH/2
    half_length = FIELD_LENGTH/2
    
    plt.style.use('dark_background')
    if ANIMATE:
        h1 = plt.plot([], [], 'rx-')
        plt.xlim(-half_width, half_width)
        plt.ylim(-half_length, half_length)
        if SHOW_GRID:
            plt.grid(True)
        def update_line(hl, new_data):
            hl.set_xdata(np.append(hl.get_xdata(), new_data[0]))
            hl.set_ydata(np.append(hl.get_ydata(), new_data[1]))
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
        if ANIMATE:
            update_line(h1[0], (robotPose.x, robotPose.y))
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

    total_dist = 0
    for i in range(1, len(robotPoseHistory)):
        distance = sqrt((robotPoseHistory[i].x - robotPoseHistory[i-1].x)**2 +
                        (robotPoseHistory[i].y - robotPoseHistory[i-1].y)**2)
        total_dist += distance

    print("Total distance: ", total_dist)

    if SHOW_ACCEL_VELO_GRAPH:
        times = [i * LOOPTIME for i in range(len(velocities))]

        plt.subplot(2, 1, 1)
        plt.plot(times, velocities, 'g-', label="Velocity", color="lime")
        plt.plot(times, accelerations, 'r-', label="Acceleration")
        plt.title("Acceleration and Velocity vs Time")
        plt.xlabel("Time (s)")
        plt.legend()
        plt.grid(SHOW_GRID)
        plt.subplot(2, 1, 2)

    plt.ylim(-half_length, half_length)
    plt.xlim(-half_width, half_width)
    plt.imshow(plt.imread("./centerstage.png"), extent=[-half_length, half_length, -half_width, half_width])
    plt.plot([point[0] for point in ipoints],
             [point[1] for point in ipoints], 'bx-', 
             color="fuchsia", label="Point to Point/Direct")
    plt.plot([point[0] for point in points],
             [point[1] for point in points], 'rx-', 
             label="Spline")
    plt.plot(x, y, 'ro', color="lime", label="Loop Point/Predicted Localized Point")
    plt.title("Path")
    plt.legend()
    plt.ylabel("Y")
    plt.xlabel("X")
    plt.grid(SHOW_GRID, which="both", axis="both", linestyle="-", linewidth=0.5)
    plt.xticks(np.arange(-half_length, half_length + 1, GRID_INCREMENT))
    plt.yticks(np.arange(-half_width, half_width + 1, GRID_INCREMENT))

    plt.text(-half_width, half_length * 1.1, 
             "Simulated time: " + str(round(len(robotPoseHistory) * LOOPTIME, 2)) + "s\nTotal distance: " + str(round(total_dist, 2)) + " units", 
             horizontalalignment='center', 
             verticalalignment='center', 
             bbox=dict(facecolor='white', alpha=0.5)
             )

    plt.pause(0.01)
    plt.show()
