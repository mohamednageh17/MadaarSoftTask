
# Madaar Soft — Android Task

https://github.com/user-attachments/assets/bceeb272-fb87-4fef-a52d-f5a80cdaf561



A multi-module Android application built with Jetpack Compose, MVI architecture, Room database, and
Hilt dependency injection. The app allows users to add, edit, delete, and view a list of people
with their personal details.

---

## Table of Contents

- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Architecture Overview](#architecture-overview)
- [Data Flow](#data-flow)
- [Screens](#screens)
- [Database](#database)
- [Dependency Injection](#dependency-injection)
- [Validation](#validation)
- [Setup & Build](#setup--build)

---

## Project Structure

The project follows a **clean multi-module architecture**, where each module has a single
responsibility and strict dependency rules.

```
madaar_soft/
├── app/                        # Presentation layer — UI, ViewModels, Navigation
│   └── presentation/
│       ├── navigation/         # NavHost and route definitions
│       ├── userlist/           # User list screen (MVI)
│       └── input/              # Add / Edit user screen (MVI)
│
├── domain/                     # Business logic — models, repository interfaces, use cases
│   ├── model/
│   │   └── User.kt
│   ├── repository/
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── GetUsersUseCase.kt
│       ├── GetUserByIdUseCase.kt
│       ├── AddUserUseCase.kt
│       ├── UpdateUserUseCase.kt
│       └── DeleteUserUseCase.kt
│
├── data/                       # Data layer — Room database, DAOs, repository implementations
│   ├── local/
│   │   ├── UserEntity.kt
│   │   ├── UserDao.kt
│   │   ├── AppDatabase.kt
│   │   └── UserMapper.kt
│   ├── repository/
│   │   └── UserRepositoryImpl.kt
│   └── di/
│       └── DataModule.kt
│
├── design-system/              # Shared UI components and theming
│   ├── Color.kt
│   ├── Typography.kt
│   ├── Theme.kt
│   └── components/
│       ├── MadaarButton.kt
│       ├── MadaarInput.kt
│       └── MadaarText.kt
│
└── gradle/
    └── libs.versions.toml      # Centralized version catalog
```

### Module Dependency Rules

```
app  →  domain  ←  data
app  →  design-system
```

- `domain` has **zero Android dependencies** — pure Kotlin/JVM module.
- `data` depends on `domain` but never on `app`.
- `app` depends on `domain` and `design-system`, but never directly on `data`.

---

## Technologies Used

| Category             | Library / Tool                 | Version     |
|----------------------|--------------------------------|-------------|
| Language             | Kotlin                         | 2.0.21      |
| UI                   | Jetpack Compose + Material 3   | BOM 2024.09 |
| Architecture         | MVI + Clean Architecture       | —           |
| Dependency Injection | Hilt (Dagger)                  | 2.51.1      |
| Database             | Room                           | 2.6.1       |
| Async / Streams      | Kotlin Coroutines + Flow       | 1.8.1       |
| Navigation           | Navigation Compose             | 2.8.9       |
| ViewModel            | Lifecycle ViewModel Compose    | 2.10.0      |
| Build System         | Gradle (KTS) + Version Catalog | AGP 8.13.2  |
| Code Generation      | KSP (Kotlin Symbol Processing) | 2.0.21      |
| Min SDK              | 26 (Android 8.0)               | —           |

---

## Architecture Overview

### Clean Architecture Layers

```
Presentation (app)
      ↕  ViewModels / State / Intent
   Use Cases (domain)
      ↕  Repository Interface
 Repository Impl (data)
      ↕  DAO
   Room Database (data)
```

### MVI Pattern (per screen)

Each screen follows a strict **Model–View–Intent** cycle:

```
User Interaction → Intent → ViewModel → State Update → UI Re-composition
```

- **Intent** — a sealed class representing all possible user actions (e.g. `SubmitClicked`,
  `OnDeleteUserClicked`).
- **State** — a single immutable data class representing the full UI state at any point in time.
- **ViewModel** — receives intents, applies business logic, and emits a new state via `StateFlow`.

Example — delete flow:

```
UserListIntent.OnDeleteUserClicked(user)
    → set userToDelete + showDeleteDialog = true
    → user confirms dialog → OnConfirmDelete
    → DeleteUserUseCase(user) called
    → Room emits updated list → UI re-renders automatically
```

Example — edit/add flow:

```
InputIntent.SubmitClicked
    → validate fields
    → if invalid: emit field-level errors in InputState
    → if valid + isEditMode: call UpdateUserUseCase
    → if valid + add mode: call AddUserUseCase
    → emit isSubmitted = true → navigate back
```

---

## Data Flow

```
Room DB (Flow<List<UserEntity>>)
    → UserDao.getAllUsers()
    → UserRepositoryImpl.getUsers()          [maps to domain User]
    → GetUsersUseCase.invoke()
    → UserListViewModel.loadUsers()          [collects Flow]
    → UserListState.users
    → UserListScreen                         [renders list]
```

Flow is used **end-to-end** from the DAO up to the ViewModel. Any insert, update, or delete
automatically triggers a fresh emission — no manual refresh needed.

---

## Screens

### User List Screen

- Displays all saved users from the local database.
- Shows a loading spinner while the first emission is pending.
- Shows an error message with a retry button if collection fails.
- Each user card has two icon buttons:
    - **Edit** → navigates to the Input screen pre-filled with the user's data.
    - **Delete** → shows a confirmation dialog before deleting.
- Delete confirmation dialog:
    - Title: "Delete User"
    - Message: "Are you sure you want to delete {name}?"
    - Buttons: Cancel / Delete
- Navigates to the Add User screen via the FAB (+).

### Input Screen (Add / Edit User)

- **Add mode** (default): empty form, top bar shows "Add User", button shows "Save".
- **Edit mode** (when opened via the edit icon): form pre-filled with selected user's data, top bar
  shows "Edit User", button shows "Save Changes".
- Form fields: Name, Age, Job Title, Gender (dropdown).
- Validates all fields on submit — shows inline error messages under each field.
- Field errors clear automatically as the user starts typing.
- Shows a `CircularProgressIndicator` overlay while saving.
- On success, navigates back to the User List.
- On failure, shows a general error message below the form.

---

## Database

Room is configured in the `data` module with `exportSchema = false`.

```
AppDatabase  (RoomDatabase)
    └── UserDao
            ├── insertUser(UserEntity)              [suspend]
            ├── updateUser(UserEntity)              [suspend — @Update]
            ├── deleteUser(UserEntity)              [suspend — @Delete]
            ├── getAllUsers(): Flow<List<UserEntity>>
            └── getUserById(id: Int): UserEntity?   [suspend]
```

**UserEntity** mirrors the domain `User` model with an auto-generated `@PrimaryKey`.

**Mapping** is done through two simple extension functions in `UserMapper.kt`:

```kotlin
UserEntity.toDomain(): User
User.toEntity(): UserEntity
```

No mapper classes, no abstraction layers — just functions.

---

## Dependency Injection

Hilt is used with a single `DataModule` in the `data` module, installed in `SingletonComponent`.

```kotlin
DataModule
├── @Provides  AppDatabase       (via Room.databaseBuilder)
├── @Provides  UserDao           (from AppDatabase)
└── @Binds     UserRepository    (bound to UserRepositoryImpl)
```

Use cases are injected directly into `@HiltViewModel` classes via constructor injection. The
`InputViewModel` also receives a `SavedStateHandle` to read the optional `userId` navigation
argument for edit mode.

---

## Validation

Validation lives entirely inside `InputViewModel` — no separate validator classes.

| Field     | Rule                                         | Error Message                                   |
|-----------|----------------------------------------------|-------------------------------------------------|
| Name      | Must not be blank                            | "Name is required"                              |
| Age       | Must not be blank and must be a positive int | "Age is required" / "Must be a positive number" |
| Job Title | Must not be blank                            | "Job title is required"                         |
| Gender    | Must be selected from dropdown               | "Gender is required"                            |

- Validation runs on `SubmitClicked` before any repository call.
- Each field error is stored in `InputState` and displayed via `isError` + `supportingText` on
  `OutlinedTextField`.
- Field errors are cleared individually as the user edits each field.
- All field errors are cleared before a valid save attempt begins.

---

## Setup & Build

### Requirements

- Android Studio Hedgehog or newer
- JDK 11+
- Android SDK 36

### Steps

```bash
# Clone the repository
git clone https://github.com/mohamednageh17/MadaarSoftTask
cd madaar_soft

# Open in Android Studio and let Gradle sync

# Or build from the command line
./gradlew assembleDebug
```

### Run on a device/emulator

Use the standard **Run** button in Android Studio, or:

```bash
./gradlew installDebug
```
