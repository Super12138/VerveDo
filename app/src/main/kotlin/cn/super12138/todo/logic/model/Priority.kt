package cn.super12138.todo.logic.model

import androidx.annotation.StringRes
import cn.super12138.todo.R

enum class Priority(@param:StringRes val nameRes: Int) {
    NotUrgent(nameRes = R.string.priority_not_urgent),
    NotImportant(nameRes = R.string.priority_not_important),
    Default(nameRes = R.string.priority_default),
    Important(nameRes = R.string.priority_important),
    Urgent(nameRes = R.string.priority_urgent);
}