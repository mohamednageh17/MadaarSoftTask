package com.madaarsoft.app.presentation.userlist

sealed interface UserListIntent {
    data object LoadUsers : UserListIntent
    data object RetryClicked : UserListIntent
}
