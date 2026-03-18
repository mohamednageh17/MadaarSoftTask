package com.madaarsoft.app.presentation.userlist

import com.madaarsoft.domain.model.User

data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
