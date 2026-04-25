package cn.super12138.todo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation3.runtime.NavKey
import androidx.room.Room
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.Repository
import cn.super12138.todo.logic.database.TaskDao
import cn.super12138.todo.logic.database.TaskDatabase
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.ui.navigation.TopLevelBackStack
import cn.super12138.todo.ui.navigation.VerveDoScreen
import cn.super12138.todo.ui.pages.editor.EditorViewModel
import cn.super12138.todo.ui.pages.overview.OverviewViewModel
import cn.super12138.todo.ui.pages.settings.SettingsViewModel
import cn.super12138.todo.ui.pages.tasks.TaskViewModel
import cn.super12138.todo.ui.viewmodels.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object VerveDoDI {
    val Context.dataStore by preferencesDataStore(
        name = Constants.SP_NAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = Constants.SP_NAME,
                )
            )
        }
    )

    val databaseModule = module {
        single<TaskDatabase> {
            Room.databaseBuilder(
                androidApplication(),
                TaskDatabase::class.java,
                Constants.DB_NAME
            )
                .addMigrations(
                    TaskDatabase.MIGRATION_2_3,
                    TaskDatabase.MIGRATION_3_4,
                    TaskDatabase.MIGRATION_4_5
                )
                .fallbackToDestructiveMigration(false)
                .build()
        }
        single<TaskDao> { get<TaskDatabase>().taskDao() }
        single<IRepository> { Repository(get()) }
    }

    val datastoreModule = module {
        single<DataStore<Preferences>> { androidApplication().dataStore }
        singleOf(::DataStoreManager)
    }

    val viewModelModule = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::OverviewViewModel)
        viewModelOf(::TaskViewModel)

        // https://insert-koin.io/docs/reference/koin-compose/compose-viewmodel#classic-dsl-with-parameters
        viewModel<EditorViewModel> {
            EditorViewModel(
                initialTask = it.getOrNull(),
                context = androidApplication(),
                repository = get(),
                dataStoreManager = get()
            )
        }

        viewModelOf(::SettingsViewModel)
    }

    val navigationModule = module {
        activityRetainedScope {
            scoped { TopLevelBackStack<NavKey>(VerveDoScreen.Overview) }
        }
    }

    val allModules = listOf(databaseModule, datastoreModule, viewModelModule, navigationModule)
}
