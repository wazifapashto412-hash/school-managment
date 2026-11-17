package com.schoolmanagement.app.data.database.dao

import androidx.room.*
import com.schoolmanagement.app.data.database.entity.SettingEntity

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun put(setting: SettingEntity)
    @Query("SELECT value FROM app_settings WHERE `key` = :key") suspend fun get(key: String): String?
}
