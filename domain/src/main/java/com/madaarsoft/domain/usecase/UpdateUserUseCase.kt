package com.madaarsoft.domain.usecase

import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(user: User) = repository.updateUser(user)
}
