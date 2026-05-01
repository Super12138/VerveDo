package cn.super12138.todo.ui.navigation

import androidx.navigation3.runtime.NavKey

class VerveDoNavigator(startKey: NavKey) {
    val backStack = TopLevelBackStack<NavKey>(startKey)

    fun addTopLevel(key: NavKey) = backStack.addTopLevel(key)

    fun add(key: NavKey) = backStack.add(key)

    fun onBack() = backStack.removeLast()
}