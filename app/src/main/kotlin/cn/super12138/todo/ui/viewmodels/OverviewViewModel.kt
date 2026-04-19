package cn.super12138.todo.ui.viewmodels

import androidx.lifecycle.ViewModel
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.database.TaskDatabase
import cn.super12138.todo.logic.datastore.DataStoreManager

class OverviewViewModel(
    private val repository: IRepository
) : ViewModel()  {
}