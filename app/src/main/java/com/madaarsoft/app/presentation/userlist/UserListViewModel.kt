package com.madaarsoft.app.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}
