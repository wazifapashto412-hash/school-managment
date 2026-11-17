package com.schoolmanagement.app

import com.schoolmanagement.app.data.repository.AuthRepository
import com.schoolmanagement.app.domain.security.*
import org.junit.Assert.*
import org.junit.Test

class SecurityTest {
    @Test fun hashVerifiesCorrectPasswordOnly() {
        val h = PasswordHasher.hash("Secret123".toCharArray(), iterations = 1000)
        assertTrue(PasswordHasher.verify("Secret123".toCharArray(), h.hash, h.salt, h.iterations))
        assertFalse(PasswordHasher.verify("secret123".toCharArray(), h.hash, h.salt, h.iterations))
    }

    @Test fun saltsDiffer() {
        val a = PasswordHasher.hash("Secret123".toCharArray(), 1000)
        val b = PasswordHasher.hash("Secret123".toCharArray(), 1000)
        assertNotEquals(a.salt, b.salt); assertNotEquals(a.hash, b.hash)
    }

    @Test fun passwordStrength() {
        assertFalse(AuthRepository.isStrong("short1"))
        assertFalse(AuthRepository.isStrong("allletters"))
        assertFalse(AuthRepository.isStrong("12345678"))
        assertTrue(AuthRepository.isStrong("abcd1234"))
    }

    @Test fun rolePermissions() {
        assertTrue(Permissions.has(Role.SUPER_ADMIN, Permission.MANAGE_USERS))
        assertFalse(Permissions.has(Role.SCHOOL_ADMIN, Permission.MANAGE_USERS))
        assertTrue(Permissions.has(Role.ACCOUNTANT, Permission.MANAGE_FEES))
        assertFalse(Permissions.has(Role.ACCOUNTANT, Permission.DELETE_STUDENTS))
        assertFalse(Permissions.has(Role.TEACHER, Permission.MANAGE_FEES))
        assertTrue(Permissions.has(Role.TEACHER, Permission.MARK_ATTENDANCE))
        assertFalse(Permissions.has(Role.DATA_ENTRY, Permission.DELETE_STUDENTS))
        assertFalse(Permissions.has(Role.DATA_ENTRY, Permission.EDIT_ATTENDANCE))
    }

    @Test fun sessionTimesOut() {
        var now = 0L
        val s = SessionManager(timeoutMs = 1000, clock = { now })
        s.start(SessionUser(1, "a", "A", Role.TEACHER))
        now = 500; assertNotNull(s.current())
        now = 2000; assertNull(s.current())
        assertFalse(s.can(Permission.VIEW_STUDENTS))
    }
}
