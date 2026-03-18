package com.madaarsoft.app.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madaarsoft.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsers: GetUsersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    init {
        onIntent(UserListIntent.LoadUsers)
    }

    fun onIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.LoadUsers,
            UserListIntent.RetryClicked -> loadUsers()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            // TODO: implement load logic
        }
    }
}
