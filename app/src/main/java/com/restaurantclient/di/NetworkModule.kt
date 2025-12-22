package com.restaurantclient.di

import com.restaurantclient.BuildConfig
import com.restaurantclient.data.network.ApiService
import com.restaurantclient.data.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val rawUrl = BuildConfig.BASE_URL.trim()
        
        // Clean the URL: Remove trailing api/v1 if the user included it in the secret
        // because ApiService already defines endpoints starting with api/v1/
        val cleanedUrl = when {
            rawUrl.endsWith("/api/v1/") -> rawUrl.removeSuffix("api/v1/")
            rawUrl.endsWith("/api/v1") -> rawUrl.removeSuffix("api/v1")
            else -> rawUrl
        }
        
        val baseUrl = if (cleanedUrl.endsWith("/")) cleanedUrl else "$cleanedUrl/"
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
