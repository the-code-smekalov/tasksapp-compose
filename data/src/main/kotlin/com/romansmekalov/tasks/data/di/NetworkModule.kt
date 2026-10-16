package com.romansmekalov.tasks.data.di

import com.romansmekalov.tasks.data.BuildConfig
import com.romansmekalov.tasks.data.remote.CatApi
import com.romansmekalov.tasks.data.remote.InternalApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @CatNetwork
    fun provideCatOkHttp(): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder().apply {
            if (BuildConfig.CAT_API_KEY.isNotBlank()) header(
                "x-api-key", BuildConfig.CAT_API_KEY
            )
        }.build()
        chain.proceed(request)
    }.build()

    @Provides
    @Singleton
    @CatNetwork
    fun provideCatRetrofit(@CatNetwork client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://api.thecatapi.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun provideCatApi(@CatNetwork retrofit: Retrofit): CatApi = retrofit.create(CatApi::class.java)

    @Provides
    @Singleton
    @InternalNetwork
    fun provideInternalOkHttp() = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()

    @Provides
    @Singleton
    @InternalNetwork
    fun provideInternalRetrofit(@InternalNetwork client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://localhost/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun provideInternalApi(@InternalNetwork retrofit: Retrofit): InternalApi =
        retrofit.create(InternalApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CatNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalNetwork
