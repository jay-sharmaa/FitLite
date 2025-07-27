package com.example.uitutorial.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "person")
data class Person(
    @PrimaryKey
    val name: String,
    val password: String,
    val age: Int,
    val gender: Char,
    val plan: Int,
    val complete: Int,
    val days: List<Date>
)
