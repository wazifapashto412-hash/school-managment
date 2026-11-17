package com.schoolmanagement.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.schoolmanagement.app.data.database.dao.SettingsDao
import com.schoolmanagement.app.data.database.dao.UserDao
import com.schoolmanagement.app.data.database.entity.*

@Database(
    entities = [
        UserEntity::class, SettingEntity::class, AcademicSessionEntity::class, StaffEntity::class,
        SchoolClassEntity::class, SectionEntity::class, StudentEntity::class, PromotionHistoryEntity::class,
        AttendanceEntity::class, StaffAttendanceEntity::class, SalaryPaymentEntity::class,
        FeeChargeEntity::class, FeePaymentEntity::class, FeeAllocationEntity::class,
        SubjectEntity::class, ExamEntity::class, ExamSubjectEntity::class, MarkEntity::class,
        GradeScaleEntity::class, AuditLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "school.db")
                // Never use fallbackToDestructiveMigration: school records must not be lost.
                // Add explicit Migration objects here when version increases.
                .build().also { instance = it }
        }
    }
}
