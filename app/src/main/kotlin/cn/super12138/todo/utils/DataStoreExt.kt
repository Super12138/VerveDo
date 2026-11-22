package cn.super12138.todo.utils

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import cn.super12138.todo.constants.Constants

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
