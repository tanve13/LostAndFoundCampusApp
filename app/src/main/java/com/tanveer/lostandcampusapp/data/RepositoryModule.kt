package com.tanveer.lostandcampusapp.data

import com.tanveer.lostandcampusapp.Admin.Repository.ClaimRepository
import com.tanveer.lostandcampusapp.Admin.Repository.ClaimRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideClaimRepository(): ClaimRepository = ClaimRepositoryImpl()
}