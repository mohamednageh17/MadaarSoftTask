package com.madaarsoft.app.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madaarsoft.domain.usecase.DeleteUserUseCase
import com.madaarsoft.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsers: GetUsersUseCase,
    private val deleteUser: DeleteUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private var usersJob: Job? = null

    init {
        loadUsers()
    }

    fun onIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.LoadUsers,
            UserListIntent.RetryClicked -> loadUsers()
            is UserListIntent.OnDeleteUserClicked -> _state.update {
                it.copy(userToDelete = intent.user, showDeleteDialog = true)
            }

            UserListIntent.OnConfirmDelete -> confirmDelete()
            UserListIntent.OnDismissDeleteDialog -> _state.update {
                it.copy(userToDelete = null, showDeleteDialog = false)
            }
        }
    }

    private fun loadUsers() {
        usersJob?.cancel()
        usersJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getUsers()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
                .collect { users -> _state.update { it.copy(users = users, isLoading = false) } }
        }
    }

    private fun confirmDelete() {
        val user = _state.value.userToDelete ?: return
        _state.update { it.copy(showDeleteDialog = false, userToDelete = null) }
        viewModelScope.launch {
            try {
                deleteUser(user)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Failed to delete user. Please try again.") }
            }
        }
    }
}
