package com.madaarsoft.app.presentation.userlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.madaarsoft.designsystem.components.MadaarButton
import com.madaarsoft.designsystem.components.MadaarText
import com.madaarsoft.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onAddUserClicked: () -> Unit,
    onEditUserClicked: (User) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.showDeleteDialog && state.userToDelete != null) {
        DeleteConfirmationDialog(
            userName = state.userToDelete!!.name,
            onConfirm = { viewModel.onIntent(UserListIntent.OnConfirmDelete) },
            onDismiss = { viewModel.onIntent(UserListIntent.OnDismissDeleteDialog) },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Users") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUserClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add user")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null -> {
                    ErrorContent(
                        message = state.errorMessage!!,
                        onRetry = { viewModel.onIntent(UserListIntent.RetryClicked) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.users.isEmpty() -> {
                    MadaarText(
                        text = "No users yet. Tap + to add one.",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    UserList(
                        users = state.users,
                        onEditClicked = onEditUserClicked,
                        onDeleteClicked = { viewModel.onIntent(UserListIntent.OnDeleteUserClicked(it)) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserList(
    users: List<User>,
    onEditClicked: (User) -> Unit,
    onDeleteClicked: (User) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(users, key = { it.id }) { user ->
            rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteClicked(user)
                    }
                    false
                },
            )
            UserCard(
                user = user,
                onEditClicked = { onEditClicked(user) },
                onDeleteClicked = { onDeleteClicked(user) },
            )

        }
    }
}

@Composable
private fun UserCard(
    user: User,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                MadaarText(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                MadaarText(
                    text = "${user.jobTitle} · ${user.gender} · ${user.age} y/o",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = "Edit ${user.name}")
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete ${user.name}",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    userName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete User") },
        text = { Text("Are you sure you want to delete $userName?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MadaarText(text = message)
        MadaarButton(text = "Retry", onClick = onRetry)
    }
}
