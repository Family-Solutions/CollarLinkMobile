package familitysolutions.edu.myapplication.network

import familitysolutions.edu.myapplication.model.*
import retrofit2.Response
import retrofit2.http.*

// Modelos de datos para autenticación

data class SignUpRequest(
    val username: String,
    val password: String,
    val roles: List<String>
)

data class SignUpResponse(
    val id: Long,
    val username: String,
    val roles: List<String>
)

data class SignInRequest(
    val username: String,
    val password: String
)

data class SignInResponse(
    val id: Long,
    val username: String,
    val token: String
)

// Modelos de datos para mascotas
data class PetRequest(
    val username: String,
    val collarId: Long?,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    val age: Int
)

data class UpdatePetRequest(
    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    val age: Int
)

data class UpdatePetCollarRequest(
    val collarId: Long
)

// Modelos de datos para geocercas
data class GeofenceRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val username: String
)

data class UpdateGeofenceRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int
)

interface ApiService {
    // Autenticación
    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    // Mascotas - Endpoints completos según Swagger
    @GET("pet/{petId}")
    suspend fun getPet(@Path("petId") petId: Long): Response<Pet>

    @PUT("pet/{petId}")
    suspend fun updatePet(
        @Path("petId") petId: Long,
        @Body request: UpdatePetRequest
    ): Response<Pet>

    @DELETE("pet/{petId}")
    suspend fun deletePet(@Path("petId") petId: Long): Response<Pet>

    @PUT("pet/updatePetCollar/{petId}")
    suspend fun updatePetCollar(
        @Path("petId") petId: Long,
        @Body request: UpdatePetCollarRequest
    ): Response<Pet>

    @POST("pet")
    suspend fun createPet(@Body request: PetRequest): Response<Pet>

    @GET("pet/username/{username}")
    suspend fun getPetsByUsername(@Path("username") username: String): Response<List<Pet>>

    @GET("pet/collarId/{collarId}")
    suspend fun getPetByCollarId(@Path("collarId") collarId: Long): Response<Pet>

    // Geocercas
    @GET("geofence/{geofenceId}")
    suspend fun getGeofence(@Path("geofenceId") geofenceId: Long): Response<Geofence>

    @PUT("geofence/{geofenceId}")
    suspend fun updateGeofence(
        @Path("geofenceId") geofenceId: Long,
        @Body request: UpdateGeofenceRequest
    ): Response<Geofence>

    @DELETE("geofence/{geofenceId}")
    suspend fun deleteGeofence(@Path("geofenceId") geofenceId: Long): Response<Unit>

    @POST("geofence")
    suspend fun createGeofence(@Body request: GeofenceRequest): Response<Geofence>

    @GET("geofence/username/{username}")
    suspend fun getGeofencesByUsername(@Path("username") username: String): Response<List<Geofence>>

    // Collares
    @GET("collar/username/{username}")
    suspend fun getCollarsByUsername(@Path("username") username: String): Response<List<Collar>>

    @POST("collar")
    suspend fun createCollar(@Body request: CreateCollarRequest): Response<Collar>

    @DELETE("collar/{collarId}")
    suspend fun deleteCollar(@Path("collarId") collarId: String): Response<Unit>

    @PUT("collar/{collarId}/pet")
    suspend fun assignPetToCollar(@Path("collarId") collarId: String, @Body request: AssignPetToCollarRequest): Response<Collar>
} 