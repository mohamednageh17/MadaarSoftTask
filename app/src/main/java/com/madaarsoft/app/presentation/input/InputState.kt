package com.madaarsoft.app.presentation.input

data class InputState(
    val name: String = "",
    val age: String = "",
    val jobTitle: String = "",
    val gender: String = "",
    val nameError: String? = null,
    val ageError: String? = null,
    val jobTitleError: String? = null,
    val genderError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSubmitted: Boolean = false,
    val isEditMode: Boolean = false,
    val userId: Int? = null,
)
