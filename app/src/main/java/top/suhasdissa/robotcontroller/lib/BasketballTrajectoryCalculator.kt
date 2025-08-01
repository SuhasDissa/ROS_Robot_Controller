package top.suhasdissa.robotcontroller.lib

// Usage example
class BasketballTrajectoryCalculator {
    private val ml = BasketballML()

    fun getOptimalShot(distanceToHoop: Double, robotHeight: Double): TrajectoryResult? {
        val result = ml.calculateTrajectory(
            distance = distanceToHoop,
            robotHeight = robotHeight,
            targetHeight = BasketballML.STANDARD_HOOP_HEIGHT
        )

        return if (result.success) result else null
    }

    fun getHighArcShot(distanceToHoop: Double, robotHeight: Double): TrajectoryResult? {
        val result = ml.calculateTrajectoryWithArc(
            distance = distanceToHoop,
            robotHeight = robotHeight,
            targetHeight = BasketballML.STANDARD_HOOP_HEIGHT,
            preferredArc = BasketballML.HIGH_ARC
        )

        return if (result.success) result else null
    }

    fun testTrajectory(velocity: Double, angle: Double, robotHeight: Double, distance: Double): Double {
        return ml.simulateShot(velocity, angle, robotHeight, distance)
    }
}