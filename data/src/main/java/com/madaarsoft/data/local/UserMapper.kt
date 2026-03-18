package com.madaarsoft.data.local

import com.madaarsoft.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)
