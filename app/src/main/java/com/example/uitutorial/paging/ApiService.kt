package com.example.uitutorial.paging
import androidx.room.Entity
import androidx.room.PrimaryKey
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("X-api-key: ")
    @GET("nutrition")
    suspend fun getPosts(
        @Query("query") query: String,
    ): List<PostEntity>
}

fun provideRetrofit(): ApiService{
    return retrofit2.Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val fat_total_g: Float,
    val fat_saturated_g: Float,
    val cholesterol_mg: Float,
    val fiber_g: Float,
    val sugar_g: Float
)