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
        if (current.name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                addUser(
                    name = current.name,
                    age = current.age.toIntOrNull() ?: 0,
                    jobTitle = current.jobTitle,
                    gender = current.gender,
                )
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
