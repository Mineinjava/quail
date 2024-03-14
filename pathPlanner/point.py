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

from typing import Optional, Union
import bresenhams


class point2d:
    def __init__(self, x: float, y: float):
        self.x: float = x
        self.y: float = y

    def distance(self, other: Union['point2d', tuple, list]) -> float:
        return ((self[0] - other[0]) ** 2 + (
                    self[1] - other[1]) ** 2) ** 0.5

    def as_tuple(self) -> tuple:
        return (self.x, self.y)

    def line_of_sight(self, other: 'point2d', grid) -> bool:
        """Returns true if there is a line of sight between self and other.
        """
        if self == other:
            return True
        if self is None or other is None:
            return False
        intersected_points = [point2d(x, y) for x, y in
                              bresenhams.supercover(self.as_tuple(),
                                                    other.as_tuple())]
        intersected_points.append(other)
        intersected_points.append(self)

        for point in intersected_points:
            if point.x < 0 or point.y < 0 or point.x >= grid.shape[
                0] or point.y >= grid.shape[1]:
                return False
            elif grid[point.x][point.y] == 1:
                return False

        return True

    def __str__(self):
        return "(" + str(self.x) + ", " + str(self.y) + ")"

    def __repr__(self):
        return "(" + str(self.x) + ", " + str(self.y) + ")"

    def __eq__(self, other):
        if not isinstance(other, point2d):
            return False
        return self.x == other.x and self.y == other.y

    def __ne__(self, other):
        return not self.__eq__(other)

    def __hash__(self):
        return hash((self.x, self.y))

    def __getitem__(self, index):
        if index == 0:
            return self.x
        elif index == 1:
            return self.y
        else:
            raise IndexError("Index out of bounds")

    def __setitem__(self, index, value):
        if index == 0:
            self.x = value
        elif index == 1:
            self.y = value
        else:
            raise IndexError("Index out of bounds")
