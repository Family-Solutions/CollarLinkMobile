package familitysolutions.edu.myapplication.model

data class Pet(
    val id: Long,
    val username: String,
    val collarId: Long?,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    val age: Int
)

data class PetRequest(
    val username: String,
    val collarId: Long,
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