import numpy as np
import matplotlib.pyplot as plt
import math
import cubicSpline

waypoints =[[0, 1], [1, 3], [2, 5], [3, 4], [4, 6], [5, 8], [6, 7], [7, 9], [8, 10], [9, 12],
            [10, 14], [11, 15], [12, 14], [13, 13], [14, 12], [15, 11], [16, 12], [17, 14], [18, 16], [19, 18]]
splinex = []
spliney = []
other = [[7, 2], [7, 0], [10, 2], [10,0]]

for i in range(len(waypoints)):
    splinex.append(waypoints[i][0])
    spliney.append(waypoints[i][1])

splinex, spliney = cubicSpline.interpolate_xy(splinex, spliney,
                                              len(splinex) * 3)


# graph waypoints, spline, and other individually, and then all of them together
def graph():
    x_waypoints = []
    y_waypoints = []
    x_other = []
    y_other = []

    for i in range(len(waypoints)):
        x_waypoints.append(waypoints[i][0])
        y_waypoints.append(waypoints[i][1])

    for i in range(len(other)):
        x_other.append(other[i][0])
        y_other.append(other[i][1])

    plt.plot(x_waypoints, y_waypoints, 'ro')
    plt.plot(splinex, spliney, 'b-')
    plt.plot(x_other, y_other, 'g-')
    plt.show()


graph()
