package com.madaarsoft.data.di

import android.content.Context
import androidx.room.Room
import com.madaarsoft.data.local.AppDatabase
import com.madaarsoft.data.local.UserDao
import com.madaarsoft.data.repository.UserRepositoryImpl
import com.madaarsoft.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "madaar_db").build()

        @Provides
        @Singleton
        fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    }
}
