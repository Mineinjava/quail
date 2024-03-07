# Copyright (C) Marcus Kauffman 2023-Present

# This work would not have been possible without the work of many
# contributors, most notably Colin Montigel. See ACKNOWLEDGEMENT.md for
# more details.

# This file is part of Quail.

# Quail is free software: you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free
# Software Foundation, version 3.

# Quail is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
# for more details.

# You should have received a copy of the GNU General Public License
# along with Quail. If not, see <https://www.gnu.org/licenses/>.

import point
from node import node
import numpy as np
import bresenhams
import traceback
from perlin_noise import PerlinNoise
HERUISTIC_WEIGHT = 1

def reconstruct_path(goal:node):
    path = []
    current = goal
    print("goal", goal)
    print("goal parent", goal.parent)
    print("goal parent parent", goal.parent.parent)
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
            neighbor.set_shortestDist(s.parent.shortestDist + s.parent.distance(neighbor))
            neighbor.set_heuristic_distance(goal)
    else:
        if neighbor.line_of_sight(s, grid):
            if s.shortestDist + s.distance(neighbor) < neighbor.shortestDist:
                neighbor.parent = s
                neighbor.set_shortestDist(s.shortestDist + s.distance(neighbor))
                neighbor.set_heuristic_distance(goal)




def theta_star(start:node, goal:point, grid):
    start.shortestDist = 0
    start.parent = start
    start.set_heuristic_distance(goal)
    openSet = [start]
    closedSet = []
    nodes_evaluated = 1
    while len(openSet) > 0:
        openSet.sort(key=lambda x: x.shortestDist + (HERUISTIC_WEIGHT * x.set_heuristic_distance(goal)))
        s = openSet.pop(0)
        if s == goal:
            print("nodes evaluated", nodes_evaluated)
            return reconstruct_path(s)
        closedSet.append(s)
        for neighbor in s.neighbors:
            if neighbor in closedSet:
                continue
            #if neighbor.x >= len(grid) or neighbor.y >= len(grid[0]):
            #    continue
            if True:# grid[neighbor.x-1][neighbor.y-1] == 0:
                update_vertex(s, neighbor, goal, grid)
                nodes_evaluated += 1
                if neighbor not in openSet and neighbor.parent != None:
                    openSet.append(neighbor)
    return None



if __name__ == '__main__':
    """test case"""
    import matplotlib.pyplot as plt
    from cubicSpline import interpolate_xy
    WIDTH = 50
    HEIGHT = 50
    FILL_PCT = 0.15
    start = node(0, 0)
    goal = node(WIDTH-1, HEIGHT-1)
    grid = []
    SWAP_XY = True
    '''
    for x in range(WIDTH+1):
        arr = ([1] * round(HEIGHT*FILL_PCT) + ([0] * round(1+ HEIGHT - (HEIGHT*FILL_PCT))))
        np.random.shuffle(arr)
        grid.append(arr)

    '''
    noise = PerlinNoise(octaves=10)
    xpix, ypix = WIDTH, HEIGHT
    pic = [[noise([i/xpix, j/ypix]) for j in range(xpix)] for i in range(ypix)]

    grid = np.array(pic)
    grid = np.where(grid < FILL_PCT, 0, 1)


    grid = np.array(grid)
    plt.imshow(grid, cmap='gray', interpolation='none', origin='lower')
    plt.show()
    grid[0][0] = 0
    grid[HEIGHT-1][WIDTH-1] = 0

    path = theta_star(start, goal, grid)
    print(path)
    splinex = []
    spliney = []
    for i in range(len(path)):
        splinex.append(path[i].x)
        spliney.append(path[i].y)

    splinedx, splinedy = interpolate_xy(splinex, spliney, len(splinex) * 30)


    if SWAP_XY:
        splinedx, splinedy = splinedy, splinedx
        splinex, spliney = spliney, splinex

    plt.imshow(grid, cmap='gray', interpolation='none', origin='lower')
    plt.plot(splinex, spliney, marker='o')
    plt.plot(splinedx, splinedy, marker='')
    plt.show()
