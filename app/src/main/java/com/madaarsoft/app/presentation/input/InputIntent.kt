package com.madaarsoft.app.presentation.input

sealed interface InputIntent {
    data class NameChanged(val value: String) : InputIntent
    data class AgeChanged(val value: String) : InputIntent
    data class JobTitleChanged(val value: String) : InputIntent
    data class GenderChanged(val value: String) : InputIntent
    data object SubmitClicked : InputIntent
}
