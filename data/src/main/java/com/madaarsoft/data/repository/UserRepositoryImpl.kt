package com.madaarsoft.data.repository

import com.madaarsoft.data.local.UserDao
import com.madaarsoft.data.local.UserEntity
import com.madaarsoft.data.local.toDomain
import com.madaarsoft.domain.model.User
import com.madaarsoft.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {

    override fun getUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addUser(name: String, age: Int, jobTitle: String, gender: String): User {
        val entity = UserEntity(name = name, age = age, jobTitle = jobTitle, gender = gender)
        userDao.insertUser(entity)
        return entity.toDomain()
    }
}
