package com.schoolmanagement.app.domain.security

enum class Role { SUPER_ADMIN, SCHOOL_ADMIN, ACCOUNTANT, TEACHER, DATA_ENTRY }

enum class Permission {
    VIEW_STUDENTS, EDIT_STUDENTS, DELETE_STUDENTS,
    MANAGE_CLASSES,
    MARK_ATTENDANCE, EDIT_ATTENDANCE,
    VIEW_FEES, MANAGE_FEES,
    MANAGE_EXAMS, ENTER_MARKS, VIEW_RESULTS,
    MANAGE_STAFF, MANAGE_SALARIES,
    VIEW_REPORTS, FINANCIAL_REPORTS,
    BACKUP_RESTORE, MANAGE_SETTINGS, MANAGE_USERS
}

object Permissions {
    private val all = Permission.values().toSet()

    private val map: Map<Role, Set<Permission>> = mapOf(
        Role.SUPER_ADMIN to all,
        Role.SCHOOL_ADMIN to (all - Permission.MANAGE_USERS - Permission.BACKUP_RESTORE),
        Role.ACCOUNTANT to setOf(
            Permission.VIEW_STUDENTS, Permission.VIEW_FEES, Permission.MANAGE_FEES,
            Permission.MANAGE_SALARIES, Permission.VIEW_REPORTS, Permission.FINANCIAL_REPORTS
        ),
        Role.TEACHER to setOf(
            Permission.VIEW_STUDENTS, Permission.MARK_ATTENDANCE, Permission.ENTER_MARKS,
            Permission.VIEW_RESULTS, Permission.VIEW_REPORTS
        ),
        Role.DATA_ENTRY to setOf(Permission.VIEW_STUDENTS, Permission.EDIT_STUDENTS, Permission.MARK_ATTENDANCE)
    )

    fun of(role: Role): Set<Permission> = map.getValue(role)
    fun has(role: Role, permission: Permission) = permission in of(role)
}
