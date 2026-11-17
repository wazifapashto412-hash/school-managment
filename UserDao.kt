package com.schoolmanagement.app.data.database.dao

import androidx.room.*
import com.schoolmanagement.app.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insert(user: UserEntity): Long
    @Update suspend fun update(user: UserEntity)
    @Query("SELECT COUNT(*) FROM users") suspend fun count(): Int
    @Query("SELECT * FROM users WHERE username = :username COLLATE NOCASE LIMIT 1") suspend fun findByUsername(username: String): UserEntity?
    @Query("SELECT * FROM users WHERE id = :id") suspend fun findById(id: Long): UserEntity?
    @Query("SELECT * FROM users ORDER BY username") fun observeAll(): Flow<List<UserEntity>>
    @Query("SELECT COUNT(*) FROM users WHERE role = :role AND isActive = 1") suspend fun countActiveWithRole(role: String): Int
}
