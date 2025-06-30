package top.suhasdissa.robotcontroller.data

data class AngleData(val angle: Float) {
    fun isValid(): Boolean {
        return angle in 0f..360f
    }
}