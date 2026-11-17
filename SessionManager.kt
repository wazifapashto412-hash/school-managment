package com.schoolmanagement.app.domain.security

data class SessionUser(val id: Long, val username: String, val fullName: String, val role: Role)

/** In-memory session with inactivity timeout. Nothing sensitive is persisted. */
class SessionManager(private val timeoutMs: Long = 15 * 60 * 1000L, private val clock: () -> Long = System::currentTimeMillis) {
    @Volatile private var user: SessionUser? = null
    @Volatile private var lastActive = 0L

    fun start(u: SessionUser) { user = u; lastActive = clock() }
    fun end() { user = null }
    fun touch() { if (user != null) lastActive = clock() }

    /** Returns the current user, or null if logged out / timed out. */
    fun current(): SessionUser? {
        val u = user ?: return null
        if (clock() - lastActive > timeoutMs) { user = null; return null }
        return u
    }

    fun can(permission: Permission): Boolean = current()?.let { Permissions.has(it.role, permission) } ?: false
}
