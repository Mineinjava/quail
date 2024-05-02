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

"""
self-contained theta* implementation, optimized for speed
Goal: get it fast enough to be run on every loop cycle.
"""
import copy
# Grid is a 1d tuple of integers. The array represents the x-axis and
# each integer represents the y-axis.

# Nodes are represented as a list
# [heuristic_distance, (x, y), parent, shortestDist]
# [float, tuple, tuple, float]

from typing import Tuple, List
from numba import njit
import heapq
from heapq import heappush, heappop

USE_JUMP = False
GRID_SHAPE = (50, 50)


def reconstruct_path(goal: List) -> \
        list[list]:
    """Reconstructs the path from the goal node to the start node.
    returns a list of nodes on the path"""
    path = []
    current = goal
    while current[2][1] != current[1]:  # the parent is not the current
        path.append(current)
        current = current[2]
    path.append(current)
    path.reverse()
    return path


def get_grid_value(grid: Tuple[int], x: int, y: int) -> int:
    """Returns the value of the grid at the given coordinates, or 1 if
    out of bounds"""
    if x < 0 or y < 0 or x >= GRID_SHAPE[0] or y >= GRID_SHAPE[1]:
        return 1
    return (grid[x] >> y) & 1


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
    # if swapped:
    # we are iterating over all the points so the order
    # doesn't matter.
    # points.reverse()
    return points


def line_of_sight(s: List,
                  neighbor: List,
                  grid: Tuple[int]) -> bool:
    """Returns true if there is a line of sight between self and other.
    """
    if s[1] == neighbor[1]:
        return True
    if s is None or neighbor is None:
        return False
    intersected_points: List[Tuple[int, int]] = get_line(s[1],
                                                         neighbor[1])
    intersected_points.append(neighbor[1])
    intersected_points.append(s[1])

    for point in intersected_points:
        if point[0] < 0 or point[1] < 0 or point[0] >= GRID_SHAPE[0] \
                or point[1] >= GRID_SHAPE[1]:
            print("F")
            return False
        elif get_grid_value(grid, point[0], point[1]) == 1:
            print("F")
            return False

    return True


def point_distance(s: Tuple[int, int], neighbor: Tuple[int, int]) \
        -> float:
    return ((s[0] - neighbor[0]) ** 2 + (
            s[1] - neighbor[1]) ** 2) ** 0.5


def node_distance(s: List,
                  neighbor: List) -> float:
    return ((s[1][0] - neighbor[1][0]) ** 2 + (
            s[1][1] - neighbor[1][1]) ** 2) ** 0.5


def heuristic_distance(s: List,
                       goal: List,
                       shortest_distance: float) -> float:
    """Returns the heuristic distance to the goal.
    distance is the distance from the start node to s."""
    if shortest_distance is None:
        return node_distance(s, goal)
    return node_distance(s, goal) + shortest_distance


def update_vertex(s: List,
                  neighbor: List,
                  goal: List,
                  grid: Tuple[int]):
    if line_of_sight(s[2], neighbor, grid):  # if line of sight
        # between parent of s and neighbor
        if (s[3] + node_distance(s[2], neighbor)) < neighbor[3]:
            # if the shortest known distance to s + distance to neighbor
            # is less than the shortest known distance to neighbor
            neighbor[0] = heuristic_distance(neighbor, goal, s[3])
            neighbor[2] = s[2],  # parent of neighbor is parent of s
            neighbor[3] = s[2][3] + node_distance(s[2], neighbor)
            # shortest distance to neighbor is shortest
            # distance to s + distance to neighbor

    else:
        if line_of_sight(s, neighbor, grid):  # if line of sight between
            # s and neighbor
            if s[3] + node_distance(s, neighbor) < neighbor[3]:
                # if the shortest known distance to s + distance
                # to neighbor is less than the shortest known distance to neighbor
                neighbor[0] = heuristic_distance(neighbor, goal, s[3])
                neighbor[2] = s,  # parent of neighbor is s
                neighbor[3] = s[3] + node_distance(s, neighbor)


def jump(pos: Tuple[int, int], dx: int, dy: int, grid: Tuple[int]) \
        -> Tuple[int, int]:
    """go in a direction until we are near a wall
    inspired by JPS but simple"""
    if dx == 0 and dy == 0:
        raise ValueError("dx and dy cannot be 0")
    x, y = pos
    while True:
        if x < 0 or y < 0 or x >= GRID_SHAPE[0] or y >= GRID_SHAPE[1]:
            return (x - dx, y - dy)
        # calculate sum of all point around the current point
        sum_ = 0
        for i in range(-1, 1 + 1):
            for j in range(-1, 1 + 1):
                sum_ += get_grid_value(grid, x + i, y + j)
        if sum_ != 0:
            print("sum")
            return (x-dx, y-dy)
        x = x + dx
        y = y + dy


def get_neighbors(s: List,
                  grid: Tuple[int],
                  goal: List) \
        -> List[List]:
    neighbors: List[List] = []
    backup_neighbors: List[List] = []

    for i in range(-1, 2):
        for j in range(-1, 2):
            if not (i == 0 and j == 0):
                backup_neighbors.append([None,
                                         (s[1][0] + i, s[1][1] + j),
                                         s,
                                         None])
                jumppoint = jump(s[1], i, j, grid)
                if jumppoint != s[1]:
                    neighbors.append([None, jumppoint, s, None])

    if len(neighbors) == 0:
        neighbors = backup_neighbors

    new_neighbors = []
    for neighbor in neighbors:
        if neighbor[1][0] < 0 or neighbor[1][1] < 0 or neighbor[1][0] >= \
                GRID_SHAPE[0] or neighbor[1][1] >= GRID_SHAPE[1]:
            continue
        elif get_grid_value(grid, neighbor[1][0], neighbor[1][1]) == 1:
            continue
        new_neighbors.append([heuristic_distance(neighbor, goal, s[3]),
                              neighbor[1],  # keep pos the same
                              neighbor[2],  # keep parent the same
                              s[3] + node_distance(s, neighbor)])

    return new_neighbors


def theta_star(start_pose: Tuple[int, int], goal_pose: Tuple[int, int],
               grid: Tuple[int]) -> list[tuple]:
    start = [0, start_pose, None, 0]
    goal_node = [0, goal_pose, None, 0]
    start[2] = copy.deepcopy(start)
    # the start node is the first node in the open set, so it will be
    # added to the closed set first, so [0] and [1] don't really matter

    open_set: List[List] = [start]

    closed_set = []

    while len(open_set) > 0:
        s = heappop(open_set)
        if s[1] == goal_pose:
            return reconstruct_path(s)
        closed_set.append(s)
        for neighbor in get_neighbors(s, grid=grid, goal=goal_node):
            if neighbor in closed_set:
                continue
            update_vertex(s, neighbor, goal_node, grid)
            if neighbor not in open_set and neighbor[2] is not None:
                heappush(open_set, neighbor)
    return None


if __name__ == "__main__":
    import matplotlib.pyplot
    import numpy as np
    from perlin_noise import PerlinNoise

    FILL_PCT = 0.1
    SWAP_XY = True
    start = (0, 0)
    goal = (GRID_SHAPE[0] - 1, GRID_SHAPE[1] - 1)
    grid = []

    noise = PerlinNoise(octaves=3, seed=999)
    xpix, ypix = GRID_SHAPE
    pic = [[noise([i / xpix, j / ypix]) for j in range(xpix)] for i in
           range(ypix)]
    grid = np.array(pic)
    grid = np.where(grid < FILL_PCT, 0, 1)

    pathing_grid = np.array([0 for x in range(GRID_SHAPE[0])])

    # convert the grid to a 1d tuple
    for x in range(GRID_SHAPE[0]):
        for y in range(GRID_SHAPE[1]):
            pathing_grid[x] = pathing_grid[x] + (grid[x][y] << -y)

    import time

    theta_star(start, goal, tuple(pathing_grid))  # precompile for njit
    startT = time.perf_counter_ns()
    path = theta_star(start, goal, tuple(pathing_grid))
    endT = time.perf_counter_ns()

    print("Time taken: ", (endT - startT) / 1e6)

    xs, ys = [], []

    import matplotlib.pyplot as plt

    for point in path:
        xs.append(point[1][0])
        ys.append(point[1][1])

    plt.plot(xs, ys, 'ro-')

    plt.imshow(grid, cmap='gray', interpolation='none', origin='lower')
    plt.show()

    print(path)
