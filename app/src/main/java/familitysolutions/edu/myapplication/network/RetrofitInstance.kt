package familitysolutions.edu.myapplication.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor

object RetrofitInstance {
    private const val BASE_URL = "https://collar-link-production.up.railway.app/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Esta función permite inyectar el token dinámicamente
    fun getRetrofit(token: String? = null): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                if (!token.isNullOrEmpty()) {
                    builder.header("Authorization", "Bearer $token")
                }
                chain.proceed(builder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://collar-link-production.up.railway.app/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Por compatibilidad, si no se requiere token
    val retrofit: Retrofit by lazy {
        getRetrofit()
    }

    // Aquí agregaremos las interfaces de la API
    // val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }
} 