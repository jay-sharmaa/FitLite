package com.example.uitutorial.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Update
    suspend fun updatePerson(person: Person)

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("SELECT * FROM person")
    fun getAllPersons(): Flow<List<Person>>

    @Query("SELECT * FROM person WHERE name = :name")
    fun getPersonByName(name: String): Flow<Person>
}
