package top.suhasdissa.robotcontroller.lib

class BasketballML {

    // Basic trajectory calculation
    external fun calculateTrajectory(
        distance: Double,
        robotHeight: Double,
        targetHeight: Double
    ): TrajectoryResult

    // Trajectory with preferred arc type
    external fun calculateTrajectoryWithArc(
        distance: Double,
        robotHeight: Double,
        targetHeight: Double,
        preferredArc: Int // 0 = low arc, 1 = high arc
    ): TrajectoryResult

    // Simulate a shot to test trajectories
    external fun simulateShot(
        initialVelocity: Double,
        angleDegrees: Double,
        robotHeight: Double,
        targetDistance: Double
    ): Double // Returns height at target distance

    companion object {
        init {
            System.loadLibrary("basketball_ml")
        }

        const val LOW_ARC = 0
        const val HIGH_ARC = 1
        const val STANDARD_HOOP_HEIGHT = 3.05 // meters
    }
}