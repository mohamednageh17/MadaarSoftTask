package com.madaarsoft.app.presentation.input

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.usecase.AddUserUseCase
import com.madaarsoft.domain.usecase.GetUserByIdUseCase
import com.madaarsoft.domain.usecase.UpdateUserUseCase
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
    private val updateUser: UpdateUserUseCase,
    private val getUserById: GetUserByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(InputState())
    val state: StateFlow<InputState> = _state.asStateFlow()

    init {
        val userId = savedStateHandle.get<Int>("userId") ?: -1
        if (userId != -1) {
            loadUserForEdit(userId)
        }
    }

    fun onIntent(intent: InputIntent) {
        when (intent) {
            is InputIntent.NameChanged -> _state.update {
                it.copy(
                    name = intent.value,
                    nameError = null
                )
            }

            is InputIntent.AgeChanged -> _state.update {
                it.copy(
                    age = intent.value,
                    ageError = null
                )
            }

            is InputIntent.JobTitleChanged -> _state.update {
                it.copy(
                    jobTitle = intent.value,
                    jobTitleError = null
                )
            }

            is InputIntent.GenderChanged -> _state.update {
                it.copy(
                    gender = intent.value,
                    genderError = null
                )
            }
            is InputIntent.SubmitClicked -> submitUser()
        }
    }

    private fun loadUserForEdit(userId: Int) {
        viewModelScope.launch {
            val user = getUserById(userId) ?: return@launch
            _state.update {
                it.copy(
                    isEditMode = true,
                    userId = userId,
                    name = user.name,
                    age = user.age.toString(),
                    jobTitle = user.jobTitle,
                    gender = user.gender,
                )
            }
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
                if (current.isEditMode && current.userId != null) {
                    updateUser(
                        User(
                            id = current.userId,
                            name = current.name,
                            age = current.age.toInt(),
                            jobTitle = current.jobTitle,
                            gender = current.gender,
                        )
                    )
                } else {
                    addUser(
                        name = current.name,
                        age = current.age.toInt(),
                        jobTitle = current.jobTitle,
                        gender = current.gender,
                    )
                }
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
