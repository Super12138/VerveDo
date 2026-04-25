package cn.super12138.todo.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {
    val showConfetti = mutableStateOf(false)

    fun playConfetti() {
        showConfetti.value = true
    }
}