package com.nhm.distributor.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.nhm.distributor.networking.ApiInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhm.distributor.networking.ApiTranslateInterface
import com.nhm.distributor.networking.TRANSLATE_URL
import com.nhm.distributor.networking.URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun cache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, (5 * 1024 * 1024).toLong())
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .addInterceptor(NetworkInterceptor.interceptor)
            .retryOnConnectionFailure(true)
            .cache(cache)
            .build()
    }


    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    @Qualifiers.Normal
    fun providesRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    @Qualifiers.Translate
    fun providesRetrofitTranslate(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(TRANSLATE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(@Qualifiers.Normal retrofit: Retrofit): ApiInterface =
        retrofit.create(ApiInterface::class.java)



    @Provides
    @Singleton
    fun provideTranslateApiService(@Qualifiers.Translate retrofit: Retrofit): ApiTranslateInterface =
        retrofit.create(ApiTranslateInterface::class.java)
}