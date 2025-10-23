package com.example.mymacrosapplication.network.nutrition

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Room Entity for cached responses
@Entity(tableName = "cached_food")
data class CachedFood(
    @PrimaryKey val query: String,
    val json: String,
    val timestamp: Long,
)

// 2. DAO (Data Access Object)
@Dao
interface CachedFoodDao {
    @Query("SELECT * FROM cached_food WHERE query = :query")
    suspend fun getByQuery(query: String): CachedFood?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cached: CachedFood)

    @Query("DELETE FROM cached_food WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}

// 3. Room Database
@Database(entities = [CachedFood::class], version = 1)
abstract class AppCacheDatabase : RoomDatabase() {
    abstract fun cachedFoodDao(): CachedFoodDao

    companion object {
        @Volatile private var INSTANCE: AppCacheDatabase? = null

        fun getDatabase(context: Context): AppCacheDatabase =
            INSTANCE ?: synchronized(this) {
                val inst =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppCacheDatabase::class.java,
                            "usda_fdc_cache_db",
                        ).build()
                INSTANCE = inst
                inst
            }
    }
}
