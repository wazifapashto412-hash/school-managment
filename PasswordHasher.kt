package com.schoolmanagement.app.domain.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    const val DEFAULT_ITERATIONS = 120_000
    private const val KEY_BITS = 256

    data class Hashed(val hash: String, val salt: String, val iterations: Int)

    fun hash(password: CharArray, iterations: Int = DEFAULT_ITERATIONS): Hashed {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return Hashed(derive(password, salt, iterations), Base64.getEncoder().encodeToString(salt), iterations)
    }

    fun verify(password: CharArray, hash: String, salt: String, iterations: Int): Boolean {
        val actual = derive(password, Base64.getDecoder().decode(salt), iterations)
        return MessageDigest.isEqual(actual.toByteArray(), hash.toByteArray()) // constant-time
    }

    private fun derive(password: CharArray, salt: ByteArray, iterations: Int): String {
        val spec = PBEKeySpec(password, salt, iterations, KEY_BITS)
        try {
            val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
            return Base64.getEncoder().encodeToString(key)
        } finally { spec.clearPassword() }
    }
}
