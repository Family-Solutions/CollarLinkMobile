package familitysolutions.edu.myapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.RetrofitInstance
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitInstance.retrofit.create(ApiService::class.java)
    }
} 