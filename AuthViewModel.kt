package com.schoolmanagement.app.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schoolmanagement.app.R
import com.schoolmanagement.app.SchoolApp
import com.schoolmanagement.app.data.repository.AuthResult
import com.schoolmanagement.app.domain.security.SessionUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = true,
    val needsSetup: Boolean = false,
    val user: SessionUser? = null,
    val errorRes: Int? = null,
    val busy: Boolean = false
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as SchoolApp).authRepository
    private val session = (app as SchoolApp).session
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = AuthState(loading = false, needsSetup = repo.needsSetup(), user = session.current())
    }

    fun setup(username: String, fullName: String, password: String) = run { repo.createInitialAdmin(username, fullName, password) }
    fun login(username: String, password: String) = run { repo.login(username, password) }

    fun logout() { repo.logout(); _state.value = _state.value.copy(user = null, errorRes = null) }
    fun clearError() { _state.value = _state.value.copy(errorRes = null) }

    private fun run(block: suspend () -> AuthResult) = viewModelScope.launch {
        _state.value = _state.value.copy(busy = true, errorRes = null)
        val r = block()
        _state.value = when (r) {
            is AuthResult.Success -> {
                // Setup creates the user but does not start a session; log in explicitly.
                val u = session.current() ?: r.user.also { session.start(it) }
                _state.value.copy(busy = false, needsSetup = false, user = u)
            }
            AuthResult.Invalid -> _state.value.copy(busy = false, errorRes = R.string.err_invalid)
            AuthResult.Locked -> _state.value.copy(busy = false, errorRes = R.string.err_locked)
            AuthResult.Weak -> _state.value.copy(busy = false, errorRes = R.string.err_weak)
            AuthResult.Taken -> _state.value.copy(busy = false, errorRes = R.string.err_taken)
            AuthResult.Required -> _state.value.copy(busy = false, errorRes = R.string.err_required)
        }
    }
}
