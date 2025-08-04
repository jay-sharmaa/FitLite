package com.example.fitlite.data

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
    val height: Int,
    val weight: Int,
    val days: List<Date>
)
