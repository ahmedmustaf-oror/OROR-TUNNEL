package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY priority ASC, name ASC")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Query("UPDATE servers SET isFavorite = :fav WHERE id = :id")
    suspend fun updateFavorite(id: String, fav: Boolean)

    @Query("DELETE FROM servers")
    suspend fun clearServers()
}

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs ORDER BY name ASC")
    fun getAllConfigs(): Flow<List<ConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<ConfigEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("DELETE FROM configs WHERE id = :id")
    suspend fun deleteConfig(id: String)

    @Query("UPDATE configs SET isFavorite = :fav WHERE id = :id")
    suspend fun updateFavorite(id: String, fav: Boolean)
}
