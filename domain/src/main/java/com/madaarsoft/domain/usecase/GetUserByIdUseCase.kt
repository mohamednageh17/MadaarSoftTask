package com.madaarsoft.domain.usecase

import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: Int): User? = repository.getUserById(id)
}
