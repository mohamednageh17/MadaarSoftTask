package com.madaarsoft.domain.repository

import com.madaarsoft.domain.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun addUser(name: String, age: Int, jobTitle: String, gender: String): User
}
