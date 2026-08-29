package com.example.wildguard.data.repository

interface DiscoveredAnimalRepository {

    suspend fun getDiscoveredAnimals(
        userId: String
    ): Result<Set<String>>

    suspend fun addDiscoveredAnimal(
        userId: String,
        animalType: String
    ): Result<Unit>
}
