
# quail
<img src="https://github.com/Mineinjava/quail/blob/main/images/quail_above_border.png" width="40%" align="right">

[![](https://jitpack.io/v/mineinjava/quail.svg) ![](https://jitpack.io/v/Mineinjava/Quail/month.svg)](https://jitpack.io/#mineinjava/quail)

Bird-brained swerve drive utility.

To use quail, you need a few things. The first is swerve modules. More than one. I don't care where your swerve modules are. They could be 52 miles apart and in the shape of a pentagon and quail would still work.

The second thing you need is a little knowledge of Java. You should be able to:
 - use a PID controller
 - use subclasses
 - make your motors spin

But, most importantly, you need a desire for freedom.

In case you can't read code, there are docs [here](https://astr0clad.github.io/quail_docs/)

---
I wish I knew Kotlin so I could make it purple. 💜

Currently quail is in early development so please report issues as you encounter them.

Quail is designed for use and tested in FRC and FTC, but it should work in any java program.

---
### Features:

**Working:**
- Swerve reverse kinematics (robot can drive)
- Swerve odometry (robot can know where it is)
    - Includes heading odometry (can run field oriented without gyroscope and/or provide corrections to gyroscope via Kálmán filter
- 2-deadwheel odometry (robot can know where it is (requires additional hardware & gyro))
- Autonomous navigation and pathing

**Not quite working**
- Path sequences



