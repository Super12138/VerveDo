package cn.super12138.todo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskDao
import cn.super12138.todo.logic.database.TaskDatabase
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.ui.activities.MainViewModel
import cn.super12138.todo.ui.navigation.TopLevelBackStack
import cn.super12138.todo.ui.navigation.VerveDoScreen
import cn.super12138.todo.ui.pages.editor.EditorViewModel
import cn.super12138.todo.ui.pages.overview.OverviewViewModel
import cn.super12138.todo.ui.pages.settings.SettingsAppearanceViewModel
import cn.super12138.todo.ui.pages.settings.SettingsDataCategoryViewModel
import cn.super12138.todo.ui.pages.settings.SettingsDataViewModel
import cn.super12138.todo.ui.pages.settings.SettingsInterfaceInteractionViewModel
import cn.super12138.todo.ui.pages.tasks.TaskViewModel
import cn.super12138.todo.utils.ConfettiController
import com.jsoizo.kotlincsv.csvWriter
import com.jsoizo.kotlincsv.writer.CsvWriter
import com.jsoizo.kotlincsv.writer.WriteQuoteMode
import kotlinx.serialization.json.Json
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

    val singleInstanceModule = module {
        singleOf(::ConfettiController)
        single<CsvWriter> { csvWriter { quoteMode = WriteQuoteMode.ALL } }
        single<Json> { Json { ignoreUnknownKeys = true } }
    }

    val databaseModule = module {
        single<TaskDatabase> {
            Room.databaseBuilder(
                context = androidApplication(),
                klass = TaskDatabase::class.java,
                name = Constants.DB_NAME
            )
                .addMigrations(
                    TaskDatabase.MIGRATION_2_3,
                    TaskDatabase.MIGRATION_3_4,
                    TaskDatabase.MIGRATION_4_5,
                    TaskDatabase.MIGRATION_5_6
                )
                .fallbackToDestructiveMigration(false)
                .build()
        }
        single<TaskDao> { get<TaskDatabase>().taskDao() }
        singleOf(::TaskRepository)
        singleOf(::SettingsRepository)
    }

    val datastoreModule = module {
        single<DataStore<Preferences>> { androidApplication().dataStore }
        singleOf(::DataStoreManager)
    }

    val viewModelModule = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::OverviewViewModel)
        viewModelOf(::TaskViewModel)
        viewModel<EditorViewModel> { params ->
            EditorViewModel(
                initialTask = params.getOrNull(),
                taskRepository = get(),
                settingsRepository = get(),
                confettiController = get()
            )
        }
        viewModelOf(::SettingsAppearanceViewModel)
        viewModelOf(::SettingsDataViewModel)
        viewModelOf(::SettingsDataCategoryViewModel)
        viewModelOf(::SettingsInterfaceInteractionViewModel)
    }

    val navigationModule = module {
        activityRetainedScope {
            scoped { TopLevelBackStack<NavKey>(VerveDoScreen.Overview) }
        }
    }

    val allModules = listOf(
        singleInstanceModule,
        databaseModule,
        datastoreModule,
        viewModelModule,
        navigationModule
    )
}
