package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.model.DiscoveredAnimalRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseDiscoveredAnimalRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) : DiscoveredAnimalRepository {

    override suspend fun getDiscoveredAnimals(
        userId: String
    ): Result<Set<String>> =
        runCatching {

            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }


            client
                .from("discovered_animals")
                .select {
                    filter {

                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<DiscoveredAnimalRow>()
                .map { row ->

                    row.animalType
                }
                .toSet()
        }


    override suspend fun addDiscoveredAnimal(
        userId: String,
        animalType: String
    ): Result<Unit> =
        runCatching {

            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            require(animalType.isNotBlank()) {
                "animalType cannot be blank."
            }


            val row =
                DiscoveredAnimalRow(

                    userId =
                        userId,

                    animalType =
                        animalType
                )


            client
                .from("discovered_animals")
                .upsert(row) {

                    onConflict =
                        "user_id,animal_type"
                }


            Unit
        }
}