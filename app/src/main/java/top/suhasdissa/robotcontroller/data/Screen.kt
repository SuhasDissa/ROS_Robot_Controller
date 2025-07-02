package top.suhasdissa.robotcontroller.data

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Controller : Screen("controller")
    object CommunicationTester : Screen("communication_tester")
    object Settings : Screen("settings")
}