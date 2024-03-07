// Copyright (C) Marcus Kauffman 2023-Present

// This work would not have been possible without the work of many
// contributors, most notably Colin Montigel. See ACKNOWLEDGEMENT.md for
// more details.

// This file is part of Quail.

// Quail is free software: you can redistribute it and/or modify it
// underthe terms of the GNU General Public License as published by the
// Free Software Foundation, version 3.

// Quail is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details.

// You should have received a copy of the GNU General Public License
// along with Quail. If not, see <https://www.gnu.org/licenses/>

package com.mineinjava.quail.pathing;

/** DEPRECATED - DO NOT USE */
public class SequenceSegment {
  private PathSequenceFollower pathSequenceFollower;
  private Runnable action;
  private SegmentType type;
  private boolean isRunning = false;

  public SequenceSegment(
      PathSequenceFollower pathSequenceFollower, SegmentType type, Runnable action) {
    this.pathSequenceFollower = pathSequenceFollower;
    this.action = action;
  }

  public SegmentType getType() {
    return type;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void run() {
    isRunning = true;
    action.run();

    if (type == SegmentType.PATH) {
      if (pathSequenceFollower.pathFollower.isFinished()) {
        pathSequenceFollower.nextSegment();
        isRunning = false;
      } else {
        isRunning = true;
        return;
      }
    }

    pathSequenceFollower.nextSegment();
    isRunning = false;
  }
}
