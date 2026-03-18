package com.madaarsoft.domain.usecase

import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.repository.UserRepository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(name: String, age: Int, jobTitle: String, gender: String): User =
        repository.addUser(name, age, jobTitle, gender)
}
