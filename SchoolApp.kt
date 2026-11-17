package com.schoolmanagement.app

import android.app.Application
import com.schoolmanagement.app.data.database.AppDatabase
import com.schoolmanagement.app.data.repository.AuthRepository
import com.schoolmanagement.app.domain.security.SessionManager

/** Simple manual dependency container (no DI framework needed at this size). */
class SchoolApp : Application() {
    val session by lazy { SessionManager() }
    val database by lazy { AppDatabase.get(this) }
    val authRepository by lazy { AuthRepository(database.userDao(), session) }
}
