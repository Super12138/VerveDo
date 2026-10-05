package cn.super12138.todo.logic.model

import androidx.annotation.StringRes
import cn.super12138.todo.R

enum class SortingOption(
    val id: Int,
    @param:StringRes val labelRes: Int
) {
    // 按添加先后顺序
    Sequential(id = 1, labelRes = R.string.sorting_sequential),

    Category(id = 2, labelRes = R.string.sorting_category),

    Priority(id = 3, labelRes = R.string.sorting_priority),

    DueDate(id = 4, labelRes = R.string.sorting_due_date),
    Alphabetical(id = 5, labelRes = R.string.sorting_alphabetical);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Sequential
    }
}