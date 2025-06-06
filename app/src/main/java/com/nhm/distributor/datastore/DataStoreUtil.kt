package com.nhm.distributor.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.nhm.distributor.datastore.DataStoreKeys.dataStore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DataStoreUtil {

    private var dataStore: DataStore<Preferences>? = null
    private val coRoutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        CoroutineScope(Dispatchers.Main).launch {
            t.printStackTrace()
        }
    }

    fun initDataStore(context: Context) {
        dataStore = context.dataStore
    }

    /** dataStore*/
    fun <T> saveData(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <T> readData(key: Preferences.Key<T>, valueIs: (T?) -> Unit) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(it[key])
                }
            }
        }
    }

    fun <T> readDataSynchronously(key: Preferences.Key<T>, valueIs: (T?) -> Unit) {
        CoroutineScope(Dispatchers.Main + coRoutineExceptionHandler).launch {
            dataStore?.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(it[key])
                }
            }
        }
    }

    fun <T> saveObject(key: Preferences.Key<String>, value: T) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit { preferences ->
                preferences[key] = Gson().toJson(value)
            }
        }
    }

    fun <T> readObject(key: Preferences.Key<String>, clazz: Class<T>, valueIs: (T?) -> Unit) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(Gson().fromJson(it[key], clazz))
                }
            }
        }
    }

    fun clearDataStore(valueIs: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit { preferences ->
                preferences.clear()
            }
            CoroutineScope(Dispatchers.Main).launch {
//                Log.d("clearDataStore", "clearDataStore")
                valueIs(true)
            }
        }
    }

    fun <T> removeKey(key: Preferences.Key<T>, onRemove: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore?.edit { preferences ->
                preferences.remove(key)
            }
            CoroutineScope(Dispatchers.Main).launch {
                onRemove(true)
            }
        }
    }

}

