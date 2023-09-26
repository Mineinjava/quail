def get_line(start, end):

    """Bresenham's Line Algorithm
    Produces a list of tuples from start and end
    >>> points1 = get_line((0, 0), (3, 4))
    >>> points2 = get_line((3, 4), (0, 0))
    >>> assert(set(points1) == set(points2))
    >>> print points1
    [(0, 0), (1, 1), (1, 2), (2, 3), (3, 4)]
    >>> print points2
    [(3, 4), (2, 3), (1, 2), (1, 1), (0, 0)]
    """
    # Setup initial conditions
    x1, y1 = start
    x2, y2 = end
    dx = x2 - x1
    dy = y2 - y1

    # Determine how steep the line is
    is_steep = abs(dy) > abs(dx)

    # Rotate line
    if is_steep:
        x1, y1 = y1, x1
        x2, y2 = y2, x2

    # Swap start and end points if necessary and store swap state
    swapped = False
    if x1 > x2:
        x1, x2 = x2, x1
        y1, y2 = y2, y1
        swapped = True

    # Recalculate differentials
    dx = x2 - x1
    dy = y2 - y1

    # Calculate error
    error = int(dx / 2.0)
    ystep = 1 if y1 < y2 else -1

    # Iterate over bounding box generating points between start and end
    y = y1
    points = []
    for x in range(x1, x2 + 1):
        coord = (y, x) if is_steep else (x, y)
        points.append(coord)
        error -= abs(dy)
        if error < 0:
            y += ystep
            error += dx

    # Reverse the list if the coordinates were swapped
    if swapped:
        points.reverse()
    return points

def lerp(pos1, pos2):
    """Linear interpolation between two points.
    """
    x1, y1 = pos1
    x2, y2 = pos2
    points = []
    length = max(abs(x2-x1), abs(y2-y1))
    for i in range(length):
        t = i / length
        x = round(x1 * (1-t) + x2 * t)
        y = round(y1 * (1-t) + y2 * t)
        points.append((x, y))

def cover_even_more(points):
    extrapts = []
    for i in range(len(points)):
        if i == 0:
            continue

        if points[i][0] != points[i-1][0] and points[i][1] != points[i-1][1]:
            extrapts.append((points[i][0], points[i-1][1]))
            extrapts.append((points[i-1][0], points[i][1]))

    points.extend(extrapts)
    #points.sort()
    return points

def supercover(a, b):
    (x1, y1), (x2, y2) = a, b

    x1 = x1 - 1 * (x2 - x1 < 0)
    y1 = y1 - 1 * (y2 - y1 < 0)
    x2 = x2 - 1 * (x2 - x1 > 0)
    y2 = y2 - 1 * (y2 - y1 > 0)

    dx, dy = x2 - x1, y2 - y1
    x, y = x1, y1
    pts = []
    pts.append([x1, y1])

    if dy < 0:
        ystep = -1
        dy = -dy
    else:
        ystep = 1

    if dx < 0:
        dx = -dx
        xstep = -1
    else:
        xstep = 1

    ddy = 2 * dy
    ddx = 2 * dx

    if ddx >= ddy:
        error = dx
        errorprev = dx

        for _ in range(dx):
            x += xstep
            error += ddy
            if error > ddx:
                y += ystep
                error -= ddx
                if error + errorprev < ddx:
                    pts.append([x, y-ystep])
                elif error + errorprev > ddx:
                    pts.append([x-xstep, y])
                else:
                    pts.append([x, y-ystep])
                    pts.append([x-xstep, y])
            pts.append([x, y])
            errorprev = error
    else:
        errorprev = dy
        error = dy
        for _ in range(dy):
            y += ystep
            error += ddx
            if error > ddy:
                x += xstep
                error -= ddy
                if error + errorprev < ddy:
                    pts.append([x-xstep, y])
                elif error + errorprev > ddy:
                    pts.append([x, y-ystep])
                else:
                    pts.append([x-xstep, y])
                    pts.append([x, y-ystep])
            pts.append([x, y])
            errorprev = error
    #return cover_even_more(pts)
    return pts

if __name__ == "__main__":
    import matplotlib.pyplot as plt
    import numpy as np
    start = (0, 0)
    end = (10, 1)
    x1, y1 = start
    x2, y2 = end
    points = get_line(start, end)
    points2 = lerp(start, end)
    points3 = supercover(start, end)
    print(points, '\n', points2)
    plt.plot([x[0] for x in points], [x[1] for x in points], marker='.', color='b', linestyle='')
    plt.plot([x[0] for x in points2], [x[1] for x in points2], marker='x', color='r', linestyle='')
    plt.plot([x[0] for x in points3], [x[1] for x in points3], marker='o', color='y', linestyle='')
    plt.plot([x1, x2], [y1, y2], marker='o', color='g')
    plt.show()