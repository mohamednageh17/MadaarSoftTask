package com.madaarsoft.data.repository

import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    override suspend fun getUsers(): List<User> {
        // TODO: replace with Room data source
        return emptyList()
    }

    override suspend fun addUser(name: String, age: Int, jobTitle: String, gender: String): User {
        // TODO: replace with Room data source
        return User(id = 0, name = name, age = age, jobTitle = jobTitle, gender = gender)
    }
}
