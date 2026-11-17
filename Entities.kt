package com.schoolmanagement.app.data.database.entity

import androidx.room.*

// Money is stored as Long paisa-free whole PKR "minor units" (rupees * 100) to avoid floating-point errors.
// Dates are stored as ISO text yyyy-MM-dd (display format DD-MM-YYYY is applied in UI).

@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val fullName: String,
    val role: String,
    val passwordHash: String,
    val salt: String,
    val iterations: Int,
    val isActive: Boolean = true,
    val mustChangePassword: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class SettingEntity(@PrimaryKey val key: String, val value: String)

@Entity(tableName = "academic_sessions", indices = [Index(value = ["name"], unique = true)])
data class AcademicSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val startDate: String, val endDate: String, val isCurrent: Boolean = false
)

@Entity(tableName = "staff", indices = [Index(value = ["employeeId"], unique = true)])
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: String, val name: String, val fatherName: String = "",
    val contact: String = "", val address: String = "", val qualification: String = "",
    val designation: String = "", val joiningDate: String = "", val isTeacher: Boolean = true,
    val monthlySalary: Long = 0, val status: String = "ACTIVE", val photoPath: String? = null,
    val userId: Long? = null
)

@Entity(tableName = "classes", indices = [Index(value = ["name"], unique = true)])
data class SchoolClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val sortOrder: Int = 0
)

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(SchoolClassEntity::class, ["id"], ["classId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(StaffEntity::class, ["id"], ["classTeacherId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("classId"), Index("classTeacherId"), Index(value = ["classId", "name"], unique = true)]
)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long, val name: String, val classTeacherId: Long? = null
)

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(SectionEntity::class, ["id"], ["sectionId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(AcademicSessionEntity::class, ["id"], ["sessionId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index(value = ["admissionNo"], unique = true), Index("sectionId"), Index("sessionId"), Index("fullName"), Index("fatherName")]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val admissionNo: String, val fullName: String, val fatherName: String, val motherName: String = "",
    val dateOfBirth: String = "", val gender: String = "", val photoPath: String? = null,
    val bForm: String = "", val religion: String = "", val bloodGroup: String = "",
    val nationality: String = "Pakistani", val address: String = "", val contact: String = "",
    val admissionDate: String = "", val sectionId: Long, val sessionId: Long, val rollNo: String = "",
    val previousSchool: String = "", val previousClass: String = "", val previousRecord: String = "",
    val status: String = "ACTIVE", // ACTIVE, ARCHIVED, LEFT, PASSED_OUT
    val fatherOccupation: String = "", val fatherContact: String = "", val motherContact: String = "",
    val guardianName: String = "", val guardianRelation: String = "", val emergencyContact: String = "",
    val parentAddress: String = ""
)

@Entity(
    tableName = "promotion_history",
    foreignKeys = [ForeignKey(StudentEntity::class, ["id"], ["studentId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("studentId")]
)
data class PromotionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long, val fromSectionId: Long, val toSectionId: Long?, val sessionId: Long, val date: String, val note: String = ""
)

@Entity(
    tableName = "attendance",
    foreignKeys = [ForeignKey(StudentEntity::class, ["id"], ["studentId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["studentId", "date"], unique = true), Index("date")]  // unique => no duplicate entries
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long, val date: String, val status: String, // PRESENT, ABSENT, LEAVE, LATE
    val markedBy: Long, val note: String = ""
)

@Entity(
    tableName = "staff_attendance",
    foreignKeys = [ForeignKey(StaffEntity::class, ["id"], ["staffId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["staffId", "date"], unique = true)]
)
data class StaffAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val staffId: Long, val date: String, val status: String
)

@Entity(
    tableName = "salary_payments",
    foreignKeys = [ForeignKey(StaffEntity::class, ["id"], ["staffId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("staffId")]
)
data class SalaryPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffId: Long, val month: String, val amount: Long, val paidOn: String, val note: String = ""
)

@Entity(
    tableName = "fee_charges",
    foreignKeys = [ForeignKey(StudentEntity::class, ["id"], ["studentId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("studentId"), Index("period")]
)
data class FeeChargeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long, val type: String, // TUITION, ADMISSION, ANNUAL, EXAM, TRANSPORT, OTHER
    val period: String, val amount: Long, val discount: Long = 0, val note: String = ""
)

@Entity(
    tableName = "fee_payments",
    foreignKeys = [ForeignKey(StudentEntity::class, ["id"], ["studentId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("studentId"), Index("paidOn"), Index(value = ["receiptNo"], unique = true)]
)
data class FeePaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNo: String, val studentId: Long, val amount: Long, val paidOn: String,
    val receivedBy: Long, val note: String = ""
)

@Entity(
    tableName = "fee_payment_allocations",
    foreignKeys = [
        ForeignKey(FeePaymentEntity::class, ["id"], ["paymentId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(FeeChargeEntity::class, ["id"], ["chargeId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("paymentId"), Index("chargeId")]
)
data class FeeAllocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val paymentId: Long, val chargeId: Long, val amount: Long
)

@Entity(tableName = "subjects", indices = [Index(value = ["name"], unique = true)])
data class SubjectEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String)

@Entity(
    tableName = "exams",
    foreignKeys = [ForeignKey(AcademicSessionEntity::class, ["id"], ["sessionId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("sessionId")]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val type: String, // MONTHLY, MIDTERM, FINAL
    val sessionId: Long, val startDate: String, val endDate: String
)

@Entity(
    tableName = "exam_subjects",
    foreignKeys = [
        ForeignKey(ExamEntity::class, ["id"], ["examId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(SubjectEntity::class, ["id"], ["subjectId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(SchoolClassEntity::class, ["id"], ["classId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index(value = ["examId", "classId", "subjectId"], unique = true), Index("subjectId"), Index("classId")]
)
data class ExamSubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long, val classId: Long, val subjectId: Long, val maxMarks: Int, val teacherId: Long? = null
)

@Entity(
    tableName = "marks",
    foreignKeys = [
        ForeignKey(ExamSubjectEntity::class, ["id"], ["examSubjectId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(StudentEntity::class, ["id"], ["studentId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["examSubjectId", "studentId"], unique = true), Index("studentId")]
)
data class MarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examSubjectId: Long, val studentId: Long, val obtained: Double, val absent: Boolean = false
)

@Entity(tableName = "grade_scale")
data class GradeScaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val minPercent: Double, val grade: String, val remarks: String = ""
)

@Entity(
    tableName = "audit_log",
    indices = [Index("timestamp")]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(), val userId: Long, val action: String, val details: String = ""
)
