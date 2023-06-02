import point
from node import node
import numpy as np
import bresenhams
import traceback
from perlin_noise import PerlinNoise
HERUISTIC_WEIGHT = 0.9

def reconstruct_path(goal:node):
    path = []
    current = goal
    while current.parent != current:
        path.append(current)
        current = current.parent
    path.append(current)
    path.reverse()
    return path

def update_vertex(s:node, neighbor:node, goal:node, grid):
    if neighbor.line_of_sight(s.parent, grid):
        if s.parent.shortestDist + s.parent.distance(neighbor) < neighbor.shortestDist:
            neighbor.parent = s.parent
            neighbor.shortestDist = s.parent.shortestDist + s.parent.distance(neighbor)
            neighbor.set_heuristic_distance(goal)
    else:
        if neighbor.line_of_sight(s, grid):
            if s.shortestDist + s.distance(neighbor) < neighbor.shortestDist:
                neighbor.parent = s
                neighbor.shortestDist = s.shortestDist + s.distance(neighbor)
                neighbor.set_heuristic_distance(goal)



def theta_star(start:node, goal:point, grid):
    start.shortestDist = 0
    start.parent = start
    start.set_heuristic_distance(goal)
    openSet = [start]
    closedSet = []
    while len(openSet) > 0:
        openSet.sort(key=lambda x: x.shortestDist + (HERUISTIC_WEIGHT * x.set_heuristic_distance(goal)))
        s = openSet[0]
        openSet.pop(0)
        if s == goal:
            return reconstruct_path(s)
        closedSet.append(s)
        for neighbor in s.neighbors:
            if neighbor not in closedSet:
                update_vertex(s, neighbor, goal, grid)
                if neighbor not in openSet:
                    openSet.append(neighbor)
                if neighbor.parent != None:
                    assert neighbor.line_of_sight(neighbor.parent, grid)

    return None



if __name__ == '__main__':
    """test case"""
    import matplotlib.pyplot as plt
    from cubicSpline import interpolate_xy
    WIDTH = 25
    HEIGHT = 50
    FILL_PCT = 0.15
    start = node(0, 0)
    goal = node(WIDTH, HEIGHT)
    grid = []
    '''
    for x in range(WIDTH+1):
        arr = ([1] * round(HEIGHT*FILL_PCT) + ([0] * round(1+ HEIGHT - (HEIGHT*FILL_PCT))))
        np.random.shuffle(arr)
        grid.append(arr)
    '''
    noise = PerlinNoise(octaves=8)
    xpix, ypix = WIDTH, HEIGHT
    pic = [[noise([i/xpix, j/ypix]) for j in range(xpix)] for i in range(ypix)]

    grid = np.array(pic)
    grid = np.where(grid > FILL_PCT, 1, 0)
    path = theta_star(start, goal, grid)
    print(path)
    splinex = []
    spliney = []
    for i in range(len(path)):
        splinex.append(path[i].x)
        spliney.append(path[i].y)

    splinedx, splinedy = interpolate_xy(splinex, spliney, len(splinex) * 3)

    plt.imshow(grid, cmap='gray', interpolation='none', origin='lower')
    plt.plot([x.x for x in path], [x.y for x in path], marker='o')
    plt.plot(splinedx, splinedy, marker='.')
    plt.show()
