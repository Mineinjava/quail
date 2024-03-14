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
