package com.madaarsoft.app.presentation.userlist

import com.madaarsoft.domain.model.User

sealed interface UserListIntent {
    data object LoadUsers : UserListIntent
    data object RetryClicked : UserListIntent
    data class OnDeleteUserClicked(val user: User) : UserListIntent
    data object OnConfirmDelete : UserListIntent
    data object OnDismissDeleteDialog : UserListIntent
}
