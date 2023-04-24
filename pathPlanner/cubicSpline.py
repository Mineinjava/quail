# the below text is from the original C# source code.

#   Parabolic Cubic Spline Interpolation Module for C#
#   Original Author: Ryan Seghers
#   Original Copyright (C) 2013-2014 Ryan Seghers
#   Original License: MIT https://opensource.org/licenses/MIT
#   Original Source Code: https://github.com/SCToolsfactory/SCJMapper-V2/tree/master/OGL
#   Related Article: https://www.codeproject.com/Articles/560163/Csharp-Cubic-Spline-Interpolation

#   Modified by: Scott W Harden in 2022 (released under MIT license)
#   Related Article: https://swharden.com/blog/2022-01-22-spline-interpolation/
#   Related Source Code: https://github.com/swharden/Csharp-Data-Visualization

# The below text is not from the original C# source code.

# Modified by: Marcus Kauffman in 2023 (released under GNU GPLv3 license)
# This is a direct port of the C# code to Python by Marcus Kauffman in 2023

# Copyright (C) 2023  Marcus Kauffman,
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

import math
import numpy as np


def interpolate_xy(xs, ys, count):
    if xs is None or ys is None or len(xs) != len(ys):
        raise ValueError("xs and ys must have same length")

    input_point_count = len(xs)
    input_distances = np.zeros(input_point_count)
    for i in range(1, input_point_count):
        dx = xs[i] - xs[i - 1]
        dy = ys[i] - ys[i - 1]
        distance = math.sqrt(dx * dx + dy * dy)
        input_distances[i] = input_distances[i - 1] + distance

    mean_distance = input_distances[-1] / (count - 1)
    even_distances = np.arange(0, count) * mean_distance
    xs_out = interpolate(input_distances, xs, even_distances)
    ys_out = interpolate(input_distances, ys, even_distances)
    return xs_out, ys_out


def interpolate(x_orig, y_orig, x_interp):
    a, b = fit_matrix(x_orig, y_orig)

    y_interp = np.zeros(x_interp.shape[0])
    for i in range(y_interp.shape[0]):
        for j in range(x_orig.shape[0] - 1):
            if x_interp[i] <= x_orig[j + 1]:
                break
        dx = x_orig[j + 1] - x_orig[j]
        t = (x_interp[i] - x_orig[j]) / dx
        y = (1 - t) * y_orig[j] + t * y_orig[j + 1] + t * (1 - t) * (
                a[j] * (1 - t) + b[j] * t)
        y_interp[i] = y

    return y_interp


def fit_matrix(x, y):
    n = len(x)
    a = np.zeros(n - 1)
    b = np.zeros(n - 1)
    r = np.zeros(n)
    A = np.zeros(n)
    B = np.zeros(n)
    C = np.zeros(n)

    dx1, dx2, dy1, dy2 = 0.0, 0.0, 0.0, 0.0

    dx1 = x[1] - x[0]
    C[0] = 1.0 / dx1
    B[0] = 2.0 * C[0]
    r[0] = 3 * (y[1] - y[0]) / (dx1 * dx1)

    for i in range(1, n - 1):
        dx1 = x[i] - x[i - 1]
        dx2 = x[i + 1] - x[i]
        A[i] = 1.0 / dx1
        C[i] = 1.0 / dx2
        B[i] = 2.0 * (A[i] + C[i])
        dy1 = y[i] - y[i - 1]
        dy2 = y[i + 1] - y[i]
        r[i] = 3 * (dy1 / (dx1 * dx1) + dy2 / (dx2 * dx2))

    dx1 = x[n - 1] - x[n - 2]
    dy1 = y[n - 1] - y[n - 2]
    A[n - 1] = 1.0 / dx1
    B[n - 1] = 2.0 * A[n - 1]
    r[n - 1] = 3 * (dy1 / (dx1 * dx1))

    cPrime = np.zeros(n)
    cPrime[0] = C[0] / B[0]
    for i in range(1, n):
        cPrime[i] = C[i] / (B[i] - cPrime[i - 1] * A[i])

    dPrime = np.zeros(n)
    dPrime[0] = r[0] / B[0]
    for i in range(1, n):
        dPrime[i] = (r[i] - dPrime[i - 1] * A[i]) / (B[i] - cPrime[i - 1] * A[i])

    k = np.zeros(n)
    k[n - 1] = dPrime[n - 1]
    for i in range(n - 2, -1, -1):
        k[i] = dPrime[i] - cPrime[i] * k[i + 1]

    for i in range(1, n):
        dx1 = x[i] - x[i - 1]
        dy1 = y[i] - y[i - 1]
        a[i - 1] = k[i - 1] * dx1 - dy1
        b[i - 1] = -k[i] * dx1 + dy1

    return a, b