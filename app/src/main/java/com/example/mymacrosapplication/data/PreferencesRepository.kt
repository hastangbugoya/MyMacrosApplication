package com.example.mymacrosapplication.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        companion object {
            private val NUTRIENTS_KEY = stringSetPreferencesKey("selected_nutrients")
        }

        val nutrientListFlow: Flow<Set<String>> =
            dataStore.data.map { prefs ->
                prefs[NUTRIENTS_KEY] ?: emptySet()
            }

        suspend fun addNutrient(name: String) {
            dataStore.edit { prefs ->
                val updated = prefs[NUTRIENTS_KEY]?.toMutableSet() ?: mutableSetOf()
                updated.add(name)
                prefs[NUTRIENTS_KEY] = updated
            }
        }

        suspend fun removeNutrient(name: String) {
            dataStore.edit { prefs ->
                val updated = prefs[NUTRIENTS_KEY]?.toMutableSet() ?: mutableSetOf()
                updated.remove(name)
                prefs[NUTRIENTS_KEY] = updated
            }
        }

        suspend fun preloadDefaults() {
            dataStore.edit { prefs ->
                if (prefs[NUTRIENTS_KEY].isNullOrEmpty()) {
                    prefs[NUTRIENTS_KEY] = setOf("Protein", "Calories", "Carbs", "Fat")
                }
            }
        }
    }
