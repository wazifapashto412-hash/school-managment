package com.schoolmanagement.app.data.repository

import com.schoolmanagement.app.data.database.dao.UserDao
import com.schoolmanagement.app.data.database.entity.UserEntity
import com.schoolmanagement.app.domain.security.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface AuthResult {
    data class Success(val user: SessionUser) : AuthResult
    data object Invalid : AuthResult
    data object Locked : AuthResult
    data object Weak : AuthResult
    data object Taken : AuthResult
    data object Required : AuthResult
}

class AuthRepository(private val dao: UserDao, private val session: SessionManager) {
    private var failures = 0
    private var lockedUntil = 0L
    private val maxFailures = 5
    private val lockMs = 60_000L

    suspend fun needsSetup(): Boolean = dao.count() == 0

    /** First-run only: creates the Super Admin. Refuses if any user already exists. */
    suspend fun createInitialAdmin(username: String, fullName: String, password: String): AuthResult =
        withContext(Dispatchers.Default) {
            if (username.isBlank() || fullName.isBlank()) return@withContext AuthResult.Required
            if (!isStrong(password)) return@withContext AuthResult.Weak
            if (dao.count() != 0) return@withContext AuthResult.Taken
            createUser(username.trim(), fullName.trim(), Role.SUPER_ADMIN, password)
        }

    suspend fun createUser(username: String, fullName: String, role: Role, password: String): AuthResult =
        withContext(Dispatchers.Default) {
            if (!isStrong(password)) return@withContext AuthResult.Weak
            if (dao.findByUsername(username) != null) return@withContext AuthResult.Taken
            val h = PasswordHasher.hash(password.toCharArray())
            val id = dao.insert(UserEntity(username = username, fullName = fullName, role = role.name,
                passwordHash = h.hash, salt = h.salt, iterations = h.iterations))
            val u = SessionUser(id, username, fullName, role)
            AuthResult.Success(u)
        }

    suspend fun login(username: String, password: String): AuthResult = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (now < lockedUntil) return@withContext AuthResult.Locked
        val e = dao.findByUsername(username.trim())
        val ok = e != null && e.isActive &&
            PasswordHasher.verify(password.toCharArray(), e.passwordHash, e.salt, e.iterations)
        if (!ok || e == null) {
            if (++failures >= maxFailures) { lockedUntil = now + lockMs; failures = 0 }
            return@withContext AuthResult.Invalid
        }
        failures = 0
        val u = SessionUser(e.id, e.username, e.fullName, Role.valueOf(e.role))
        session.start(u)
        AuthResult.Success(u)
    }

    suspend fun changePassword(userId: Long, old: String, new: String): AuthResult = withContext(Dispatchers.Default) {
        if (!isStrong(new)) return@withContext AuthResult.Weak
        val e = dao.findById(userId) ?: return@withContext AuthResult.Invalid
        if (!PasswordHasher.verify(old.toCharArray(), e.passwordHash, e.salt, e.iterations)) return@withContext AuthResult.Invalid
        val h = PasswordHasher.hash(new.toCharArray())
        dao.update(e.copy(passwordHash = h.hash, salt = h.salt, iterations = h.iterations, mustChangePassword = false))
        AuthResult.Success(SessionUser(e.id, e.username, e.fullName, Role.valueOf(e.role)))
    }

    fun logout() = session.end()

    companion object {
        fun isStrong(p: String) = p.length >= 8 && p.any { it.isLetter() } && p.any { it.isDigit() }
    }
}
