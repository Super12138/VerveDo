package cn.super12138.todo.logic.model

import androidx.annotation.StringRes
import cn.super12138.todo.R

enum class SortingDirection(
    val id: Int,
    @param:StringRes val labelRes: Int
) {
    Ascending(id = 1, labelRes = R.string.sorting_ascending),
    Descending(id = 2, labelRes = R.string.sorting_descending);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Ascending
    }
}