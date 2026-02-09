package cn.super12138.todo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import cn.super12138.todo.R

enum class VerveDoDestinations(
    val route: NavKey,
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int
) {
    Overview(
        route = VerveDoScreen.Overview,
        label = R.string.page_overview,
        icon = R.drawable.ic_dashboard,
        selectedIcon = R.drawable.ic_dashboard_filled
    ),
    Tasks(
        route = VerveDoScreen.Tasks,
        label = R.string.page_tasks,
        icon = R.drawable.ic_ballot,
        selectedIcon = R.drawable.ic_ballot_filled
    ),
    Settings(
        route = VerveDoScreen.Settings.Main,
        label = R.string.page_settings,
        icon = R.drawable.ic_settings,
        selectedIcon = R.drawable.ic_settings_filled
    )
}

