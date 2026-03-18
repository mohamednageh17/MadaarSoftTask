package com.madaarsoft.app.presentation.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.madaarsoft.designsystem.components.MadaarButton
import com.madaarsoft.designsystem.components.MadaarInput
import com.madaarsoft.designsystem.components.MadaarText

private val genderOptions = listOf("Male", "Female")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onUserAdded: () -> Unit,
    viewModel: InputViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var genderMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) onUserAdded()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add User") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                MadaarInput(
                    value = state.name,
                    onValueChange = { viewModel.onIntent(InputIntent.NameChanged(it)) },
                    label = "Name",
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                MadaarInput(
                    value = state.age,
                    onValueChange = { viewModel.onIntent(InputIntent.AgeChanged(it)) },
                    label = "Age",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.ageError != null,
                    supportingText = state.ageError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                MadaarInput(
                    value = state.jobTitle,
                    onValueChange = { viewModel.onIntent(InputIntent.JobTitleChanged(it)) },
                    label = "Job Title",
                    isError = state.jobTitleError != null,
                    supportingText = state.jobTitleError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                Column {
                    ExposedDropdownMenuBox(
                        expanded = genderMenuExpanded,
                        onExpandedChange = { genderMenuExpanded = it },
                    ) {
                        MadaarInput(
                            value = state.gender,
                            onValueChange = {},
                            label = "Gender",
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderMenuExpanded) },
                            isError = state.genderError != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        )
                        ExposedDropdownMenu(
                            expanded = genderMenuExpanded,
                            onDismissRequest = { genderMenuExpanded = false },
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onIntent(InputIntent.GenderChanged(option))
                                        genderMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    state.genderError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                        )
                    }
                }

                state.errorMessage?.let { error ->
                    MadaarText(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                MadaarButton(
                    text = if (state.isLoading) "Saving…" else "Save",
                    onClick = { viewModel.onIntent(InputIntent.SubmitClicked) },
                    enabled = !state.isLoading,
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
