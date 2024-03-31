from point import point2d
from typing import Optional, Union
from numba import njit

@njit(fastmath=True, cache=True)
def jump(pos, dx, dy, grid):
    """go in a direction until we are near a wall
    inspired by JPS but more simple"""
    x, y = pos
    while True:
        if x < 0 or y < 0 or x >= grid.shape[0] or y >= grid.shape[1]:
            return (x - dx, y - dy)
        # calculate sum of all point around the current point
        sum_ = grid[x-1:x+2, y-1:y+2].sum()
        if sum_ > 0:
            return (x, y)
        x += dx
        y += dy

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

    def get_neighbors(self, diagonal:bool=True)->list:
        neighbors:list = []
        for i in range(-1, 1+1):
            for j in range(-1, 1+1):
                if i == 0 and j == 0:
                    continue
                if (i==j) and not diagonal:
                    continue

                else:
                    neighbors.append(node(self.x + i, self.y + j))

        #neighbors = [neighbor for neighbor in neighbors if neighbor.x >= 0 and neighbor.y >= 0]

        return neighbors

    @property
    def neighbors(self):
        if self._neighbors is None:
            self._neighbors = self.get_neighbors()
        return self._neighbors

