from point import point2d
from typing import Optional, Union
from numba import njit

@njit(fastmath=True, cache=True)
def jump(pos, dx, dy, grid):
    """go in a direction until we are near a wall
    inspired by JPS but more simple"""
    if dx == 0 and dy == 0:
        raise ValueError("dx and dy cannot be 0")
    x, y = pos
    while True:
        if x < 0 or y < 0 or x >= grid.shape[0] or y >= grid.shape[1]:
            return (x - dx, y - dy)
        # calculate sum of all point around the current point
        sum_ = grid[x-1:x+2, y-1:y+2].sum()
        if sum_ != 0:
            return (x, y)
        x = x + dx
        y = y + dy

class node(point2d):
    def __init__(self, x:float, y:float, shortestDist:Optional[float]=float('inf'), parent:Optional['node']=None):
        super().__init__(x, y)
        self.heuristic_distance = None
        self.parent: Optional['node'] = parent
        self.shortestDist = shortestDist
        self._neighbors = None

    def set_parent(self, parent:'node'):
        self.parent = parent

    def set_shortestDist(self, shortestDist:float):
        self.shortestDist = shortestDist

    def set_heuristic_distance(self, goal:point2d):
        if self.heuristic_distance is None:
            self.heuristic_distance = self.distance(goal)
        return self.heuristic_distance

    def get_neighbors(self, diagonal:bool=True, grid=None)->list:
        neighbors:list = []
        backupneighbors = []
        for i in range(-1, 1+1):
            for j in range(-1, 1+1):
                if not (i == 0 and j == 0):
                    backupneighbors.append(node(self.x + i, self.y + j))
                    jumppoint = jump((self.x, self.y), i, j, grid)
                    if jumppoint != (self.x, self.y):
                        neighbors.append(jumppoint)

        #neighbors = [neighbor for neighbor in neighbors if neighbor.x >= 0 and neighbor.y >= 0]
        neighbors = [node(neighbor[0], neighbor[1], float('inf'), self) for neighbor in neighbors]
        if len(neighbors) == 0:
            neighbors = backupneighbors
        # make sure neighbors are within the grid
        # neighbors = [neighbor for neighbor in neighbors if neighbor.x >= 0 and neighbor.y >= 0 and neighbor.x < grid.shape[0] and neighbor.y < grid.shape[1]]
        return neighbors

    @property
    def neighbors(self):
        if self._neighbors is None:
            self._neighbors = self.get_neighbors()
        return self._neighbors

if __name__ == "__main__":
    import numpy as np
    import matplotlib.pyplot as plt
    grid = np.zeros((10, 10))
    grid[3:6, 3:6] = 1
    start = node(0, 0)
    goal = node(9, 9)
    points = start.get_neighbors(diagonal=True, grid=grid)
    print(points)
    xs, ys = zip(*[(point.x, point.y) for point in points])
    plt.scatter(xs, ys)
    plt.imshow(grid, cmap='gray')
    plt.show()