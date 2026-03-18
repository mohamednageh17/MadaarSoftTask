package com.madaarsoft.app.presentation.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madaarsoft.domain.usecase.AddUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val addUser: AddUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(InputState())
    val state: StateFlow<InputState> = _state.asStateFlow()

    fun onIntent(intent: InputIntent) {
        when (intent) {
            is InputIntent.NameChanged -> _state.update { it.copy(name = intent.value) }
            is InputIntent.AgeChanged -> _state.update { it.copy(age = intent.value) }
            is InputIntent.JobTitleChanged -> _state.update { it.copy(jobTitle = intent.value) }
            is InputIntent.GenderChanged -> _state.update { it.copy(gender = intent.value) }
            is InputIntent.SubmitClicked -> submitUser()
        }
    }

    private fun submitUser() {
        val current = _state.value
        if (!validate(current)) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    nameError = null,
                    ageError = null,
                    jobTitleError = null,
                    genderError = null,
                )
            }
            try {
                addUser(
                    name = current.name,
                    age = current.age.toInt(),
                    jobTitle = current.jobTitle,
                    gender = current.gender,
                )
                _state.update { it.copy(isLoading = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save user. Please try again."
                    )
                }
            }
        }
    }

    private fun validate(current: InputState): Boolean {
        val nameError = if (current.name.isBlank()) "Name is required" else null
        val ageError = when {
            current.age.isBlank() -> "Age is required"
            current.age.toIntOrNull() == null || current.age.toInt() <= 0 -> "Must be a positive number"
            else -> null
        }
        val jobTitleError = if (current.jobTitle.isBlank()) "Job title is required" else null
        val genderError = if (current.gender.isBlank()) "Gender is required" else null

        val hasErrors = listOf(nameError, ageError, jobTitleError, genderError).any { it != null }
        if (hasErrors) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    ageError = ageError,
                    jobTitleError = jobTitleError,
                    genderError = genderError,
                )
            }
        }
        return !hasErrors
    }
}
