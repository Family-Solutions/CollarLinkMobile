package familitysolutions.edu.myapplication.model

import com.google.gson.annotations.SerializedName

data class Collar(
    val id: Long,
    val username: String,
    val serialNumber: Long,
    val model: String,
    val lastLatitude: Double?,
    val lastLongitude: Double?,
    val pet: PetInCollar? = null
)

data class PetInCollar(
    val id: Long,
    val name: String
)

data class CreateCollarRequest(
    @SerializedName("serialNumber") val serialNumber: Long,
    @SerializedName("model") val model: String,
    @SerializedName("username") val username: String
)

data class AssignPetToCollarRequest(
    @SerializedName("petId") val petId: Long
) 