package com.example.mymacrosapplication.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
                    prefs[NUTRIENTS_KEY] =
                        setOf(
                            "1003",
                            "1004",
                            "1005",
                            "1008",
                            "1089",
                            "1093",
                            "1253",
                            "1257",
                            "1058",
                            "1090",
                            "1091",
                            "1253",
                            "1095",
                            "1098",
                            "1101",
                            "1293",
                            "1087",
                            "2047",
                            "1018",
                            "1057",
                            "2000",
                            "1079",
                            "1103",
                            "1105",
                            "1106",
                            "1107",
                            "1108",
                            "1109",
                            "1114",
                            "1120",
                            "1122",
                            "1123",
                            "1162",
                            "1165",
                            "1166",
                            "1167",
                            "1175",
                            "1177",
                            "1178",
                            "1186",
                            "1187",
                            "1190",
                            "1242",
                            "1246",
                            "1258",
                            "1058",
                            "1212",
                            "1213",
                            "1214",
                            "1216",
                            "1220",
                            "1221",
                            "1223",
                            "1227",
                            "1242",
                            "1186",
                            "1092",
                            "1292",
                            "1293",
                            "1112",
                            "1210",
                        )
                }
            }
        }
    }
