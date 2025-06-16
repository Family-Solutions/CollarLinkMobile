package familitysolutions.edu.myapplication.model

data class Geofence(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val username: String
)

data class GeofenceRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val username: String
)

data class UpdateGeofenceRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double
) 