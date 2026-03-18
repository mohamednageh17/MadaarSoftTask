package com.madaarsoft.app.presentation.input

data class InputState(
    val name: String = "",
    val age: String = "",
    val jobTitle: String = "",
    val gender: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
