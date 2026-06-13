package com.example.backend.api

import com.example.BuildConfig
import com.example.backend.database.Contact
import com.example.backend.database.IncidentReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class SupabaseContact(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

data class SupabaseIncident(
    val id: Int = 0,
    val timestamp: Long,
    val type: String,
    val description: String,
    val liveLocation: String,
    val voiceClipName: String?,
    val aiConfidenceScore: Int,
    val alertStatus: String,
    val user_email: String?,
    val user_name: String?,
    val user_mobile: String?,
    val alert_trigger_source: String?,
    val alerted_contact_names: String?,
    val alerted_contact_phones: String?
)

data class SupabaseIncidentInsert(
    val timestamp: Long,
    val type: String,
    val description: String,
    val liveLocation: String,
    val voiceClipName: String?,
    val aiConfidenceScore: Int,
    val alertStatus: String,
    val user_email: String?,
    val user_name: String?,
    val user_mobile: String?,
    val alert_trigger_source: String?,
    val alerted_contact_names: String?,
    val alerted_contact_phones: String?
)

data class UserLogin(
    val email: String,
    val name: String?,
    val login_time: Long
)

interface SupabaseApiService {
    @POST("rest/v1/user_logins")
    suspend fun insertUserLogin(@Body userLogin: UserLogin): Response<Unit>

    @GET("rest/v1/contacts")
    suspend fun getContacts(): List<SupabaseContact>

    @POST("rest/v1/contacts")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertContact(
        @Body contact: SupabaseContact,
        @Query("on_conflict") onConflict: String = "id"
    ): Response<Unit>

    @DELETE("rest/v1/contacts")
    suspend fun deleteContact(@Query("id") filter: String): Response<Unit>

    @GET("rest/v1/incidents")
    suspend fun getIncidents(@Query("user_email") filter: String): List<SupabaseIncident>

    @POST("rest/v1/incidents")
    suspend fun insertIncident(
        @Body incident: SupabaseIncidentInsert
    ): Response<Unit>


    @DELETE("rest/v1/incidents")
    suspend fun deleteIncident(@Query("id") filter: String): Response<Unit>

    @DELETE("rest/v1/incidents")
    suspend fun deleteIncidents(@Query("user_email") filter: String): Response<Unit>

    @GET("rest/v1/users")
    suspend fun getUsers(@Query("email") filter: String): List<UserProfile>

    @POST("rest/v1/users")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertUser(
        @Body user: UserProfile,
        @Query("on_conflict") onConflict: String = "email"
    ): Response<Unit>

    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @POST("auth/v1/token")
    suspend fun signIn(
        @Query("grant_type") grantType: String = "password",
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body request: RecoverRequest,
        @Query("redirect_to") redirectTo: String = "omniguard://reset"
    ): Response<Unit>

    @PUT("auth/v1/user")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): Response<Unit>

    @PUT("auth/v1/user")
    suspend fun updateUserMetadata(@Body request: UpdateUserRequest): Response<Unit>
}

data class UserProfile(
    val email: String,
    val name: String,
    val mobile_number: String,
    val role: String = "User"
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val options: SignUpOptions? = null
)

data class SignUpOptions(
    val data: UserMetadata
)

data class UserMetadata(
    val name: String? = null,
    val mobile_number: String? = null,
    val blood_type: String? = null,
    val allergies: String? = null,
    val major_conditions: String? = null,
    val languages: String? = null
)

data class SignUpResponse(
    val id: String?,
    val email: String?,
    val confirmed_at: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val user: SupabaseUser
)

data class SupabaseUser(
    val id: String,
    val email: String,
    val user_metadata: UserMetadata?,
    val confirmed_at: String?
)

data class RecoverRequest(
    val email: String
)

data class UpdatePasswordRequest(
    val password: String
)

data class UpdateUserRequest(
    val data: UserMetadata
)

object SupabaseClient {
    var accessToken: String? = null

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("apikey", BuildConfig.SUPABASE_KEY)
            .header("Content-Type", "application/json")
        
        val path = original.url.encodedPath
        val token = if ((path.contains("/rest/v1/") || path.contains("/auth/v1/user")) && accessToken != null) {
            accessToken
        } else {
            BuildConfig.SUPABASE_KEY
        }
        builder.header("Authorization", "Bearer $token")
        chain.proceed(builder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .build()

    val service: SupabaseApiService? by lazy {
        var baseUrl = BuildConfig.SUPABASE_URL.trim()
        if (baseUrl.isBlank() || baseUrl.startsWith("YOUR_SUPABASE") || !baseUrl.startsWith("http")) {
            null
        } else {
            try {
                baseUrl = baseUrl.replace(Regex("/rest/v1/?$"), "")
                val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
                Retrofit.Builder()
                    .baseUrl(formattedUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseApiService::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
