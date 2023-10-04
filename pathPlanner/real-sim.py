import matplotlib.pyplot as plt
import cubicSpline

# Change out this list of list with a list of your waypoints in a list of lists
waypoints = [(0, 0), (10, 3), (20, 20)]
velocity = (1,1)
velocitySensitivity = 2

waypoints.insert(0, (waypoints[0][0] - velocity[0] * velocitySensitivity, waypoints[0][1] - velocity[1] * velocitySensitivity))

splinex = []
spliney = []

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

    # for i in range(len(other)):
    #    x_other.append(other[i][0])
    #    y_other.append(other[i][1])

    plt.plot(x_waypoints, y_waypoints, 'ro')
    plt.plot(splinex, spliney, 'b-')
    plt.plot(x_other, y_other, 'g-')
    plt.show()


graph()
