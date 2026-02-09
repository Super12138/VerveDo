package cn.super12138.todo.logic.model

import androidx.annotation.StringRes
import cn.super12138.todo.R

enum class SortingMethod(
    val id: Int,
    @param:StringRes val nameRes: Int
) {
    // 按添加先后顺序
    Sequential(id = 1, nameRes = R.string.sorting_sequential),

    // 按学科
    Category(id = 2, nameRes = R.string.sorting_category),

    // 按优先级
    Priority(id = 3, nameRes = R.string.sorting_priority),

    // 按完成情况
    Completion(id = 4, nameRes = R.string.sorting_completion),

    // 按字母升序
    AlphabeticalAscending(id = 5, nameRes = R.string.sorting_alphabetical_ascending),

    // 按字母降序
    AlphabeticalDescending(id = 6, nameRes = R.string.sorting_alphabetical_descending),

    DueDate(id = 7, nameRes = R.string.sorting_due_date);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Sequential
    }
}