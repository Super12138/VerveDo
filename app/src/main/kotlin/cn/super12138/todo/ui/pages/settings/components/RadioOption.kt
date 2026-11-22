package cn.super12138.todo.ui.pages.settings.components

import androidx.compose.runtime.Immutable

@Immutable
data class RadioOption(
    val text: String,
    val selected: Boolean,
    val onClick: () -> Unit
)