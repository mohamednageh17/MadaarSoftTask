package com.madaarsoft.domain.repository

import com.madaarsoft.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun getUserById(id: Int): User?
    suspend fun addUser(name: String, age: Int, jobTitle: String, gender: String): User
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
}
