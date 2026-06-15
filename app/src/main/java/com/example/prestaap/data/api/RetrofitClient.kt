package com.example.prestaap.data.api

import android.util.Log
import com.example.prestaap.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking {
            FirebaseAuth.getInstance().currentUser
                ?.getIdToken(false)
                ?.await()
                ?.token
        }
        Log.d("RetrofitAuth", "Firebase user: ${FirebaseAuth.getInstance().currentUser?.email}, token=${if (token != null) "present" else "null"}")

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val apiService: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}