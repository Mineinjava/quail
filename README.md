
# quail
<img src="https://github.com/Mineinjava/quail/blob/main/images/quail_above_border.png" width="40%" align="right">

Looking to contribute to or use Quail? You may find value in joining the Discord server: https://discord.gg/ABznnnBB

[![](https://jitpack.io/v/mineinjava/quail.svg) ![](https://jitpack.io/v/Mineinjava/Quail/month.svg)](https://jitpack.io/#mineinjava/quail)

Bird-brained swerve drive utility.

To use quail, you need a few things. The first is swerve modules. More than one. I don't care where your swerve modules are. They could be 52 miles apart (i.e. [this](https://www.youtube.com/watch?v=Ui-ehJlGM1Q)) and in the shape of a pentagon and quail would still work.

The second thing you need is a little knowledge of Java. You should be able to:
 - use a PID controller
 - use subclasses
 - read an encoder
 - make your motors spin (to an angle if using [coaxial swerve](https://www.chiefdelphi.com/t/coaxial-vs-non-coaxial-swerve/158629/4))

But, most importantly, you need a desire for freedom (and simplicity).

In case you can't read code, there are docs [here](https://astr0clad.github.io/quail_docs/)
A javadoc can be found [here](https://mineinjava.github.io/quail/javadoc/)

---
I wish I knew Kotlin so I could make it purple. 💜

Currently, quail is stable. Not quite sure about the documentation--it seems to be a bit tempermental.

Quail is designed for use and tested in FRC and FTC, but it should work in any java program.

---
### Features:

**Working:**
- Swerve reverse kinematics (robot can drive)
- Swerve odometry (robot can know where it is)
    - Includes heading odometry (can run field oriented without gyroscope and/or provide corrections to gyroscope via Kálmán filter)
- 2-deadwheel odometry (robot can know where it is (requires additional hardware & gyro))
- Autonomous navigation and pathing
- Kálmán filter for use with vision

**Not quite working**
- Path sequences



