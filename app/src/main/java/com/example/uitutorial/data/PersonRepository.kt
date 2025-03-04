package com.example.uitutorial.data

import kotlinx.coroutines.flow.Flow

class PersonRepository(private val personDao: PersonDao) {
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()

    suspend fun insert(person: Person) {
        personDao.insertPerson(person)
    }

    suspend fun update(person: Person) {
        personDao.updatePerson(person)
    }

    suspend fun delete(person: Person) {
        personDao.deletePerson(person)
    }

    fun getPersonByName(name: String): Flow<Person?> {
        return personDao.getPersonByName(name)
    }
}
