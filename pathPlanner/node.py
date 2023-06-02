from point import point2d
from typing import Optional, Union

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
        neighbors = []
        for i in range(-1, 1+1):
            for j in range(-1, 1+1):
                if i == 0 and j == 0:
                    continue
                if (i==j) and not diagonal:
                    continue

                else:
                    neighbors.append(node(self.x + i, self.y + j))

        return neighbors

    @property
    def neighbors(self):
        if self._neighbors is None:
            self._neighbors = self.get_neighbors()
        return self._neighbors

