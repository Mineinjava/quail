import numpy as np
from numba import njit
from numba import int32, float32, uint32
from numba.experimental import jitclass
from typing import Optional
import bresenhams


class Node:
    def __init__(self, x, y, parent=None):
        self.x: int = x
        self.y: int = y
        self.parent: Optional[Node] = parent
        self.gScore: float = 0
        self.heuristic: float = 0
        self.f: float = 0

    def neighbors(self):
        _neighbors = []
        for i in range(-1, 2):
            for j in range(-1, 2):
                if i == 0 and j == 0:
                    continue
                _neighbors.append(Node(self.x + i, self.y + j, self))
        return _neighbors
    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    @property
    def pos(self):
        return (self.x, self.y)

    def __le__(self, other):
        return self.f <= other.f

    def __lt__(self, other):
        return self.f < other.f

    def __hash__(self):
        return hash(self.pos)


def line_of_sight(grid: np.ndarray, a, b) -> bool:
    """Returns true if there is a line of sight between self and other.
        """
    if a == b:
        return True
    if a is None or b is None:
        return False

    if a.x < 0 or a.y < 0 or a.x >= grid.shape[0] or a.y >= grid.shape[1]:
        return False
    if b.x < 0 or b.y < 0 or b.x >= grid.shape[0] or b.y >= grid.shape[1]:
        return False
    if grid[a.x][a.y] == 1 or grid[b.x][b.y] == 1:
        return False

    intersected_points = [(x, y) for x, y in
                          bresenhams.get_line(a.as_tuple(),
                                              b.as_tuple())]
    intersected_points.append(b)
    intersected_points.append(a)

    for point in intersected_points:
        if point[0] < 0 or point[0] < 0 or point[1] >= grid.shape[
            0] or point[1] >= grid.shape[1]:
            return False
        elif grid[point[0]][point[1]] == 1:
            return False

    return True

def euclidian_node_distance(pose: Node, goal: Node):
    return np.sqrt((pose.x - goal.x) ** 2 + (pose.y - goal.y) ** 2)


def euclidian_tuple_distance(pose: tuple, goal: tuple):
    return np.sqrt((pose[0] - goal[0]) ** 2 + (pose[1] - goal[1]) ** 2)


def update_vertex(s: Node, neighbor: Node, grid: np.ndarray):
    if line_of_sight(grid, s.parent, neighbor):
        if s.gScore + euclidian_node_distance(s.parent, neighbor) < neighbor.gScore:
            neighbor.gScore = s.parent.gScore + euclidian_node_distance(s, neighbor)
            neighbor.f = neighbor.gScore + neighbor.heuristic
            neighbor.parent = s.parent

    elif (s.gScore + euclidian_node_distance(s, neighbor) < neighbor.gScore) \
            and line_of_sight(grid, s, neighbor):
        neighbor.gScore = s.gScore + euclidian_node_distance(s, neighbor)
        neighbor.f = neighbor.gScore + neighbor.heuristic
        neighbor.parent = s


def theta_star(grid: np.ndarray, start: tuple, goal: tuple) -> Optional[
    list]:
    start_node = Node(start[0], start[1])
    goal_node = Node(goal[0], goal[1])
    start_node.heuristic = euclidian_node_distance(start_node, goal_node)
    start_node.f = start_node.heuristic
    open_set = [start_node]
    closed_set = []
    while open_set:
        open_set.sort()
        s = open_set.pop(0)
        if s == goal_node:
            path = []
            while s.parent:
                path.append(s.pos)
                s = s.parent
            path.append(s.pos)
            return path[::-1]
        closed_set.append(s)
        for neighbor in s.neighbors():
            if neighbor in closed_set or not grid[neighbor.x][
                neighbor.y]:
                continue
            if neighbor not in open_set:
                neighbor.heuristic = euclidian_node_distance(neighbor, goal_node)
                neighbor.gScore = float('inf')
                neighbor.f = float('inf')
                neighbor.parent = None
            update_vertex(s, neighbor, grid)
            if neighbor not in open_set:
                open_set.append(neighbor)
    return None


if __name__ == '__main__':
    from perlin_noise import PerlinNoise
    import matplotlib.pyplot as plt

    noise = PerlinNoise(octaves=10, seed=1)
    w, h = 10, 10
    grid = np.zeros((w, h))
    for i in range(w):
        for j in range(h):
            grid[i][j] = noise([i / w, j / h])
    # threshold the grid
    grid = np.where(grid > 1.5, 1, 0)
    grid = np.zeros((w,h))

    start = (0, 0)
    goal = (w - 1, h - 1)
    path = theta_star(grid, start, goal)
    print(path)
    plt.imshow(grid)
    plt.show()
    xs, ys = zip(*path)
    plt.plot(ys, xs, 'r')
    plt.show()

