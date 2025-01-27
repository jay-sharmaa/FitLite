package com.example.uitutorial

data class Data(
    val name: String,
    val publisher: String,
    val imageVector: Int
)

val myList: List<Data> = listOf(
    Data(name = "1", publisher = "1", imageVector = R.drawable.ic_launcher_background),
    Data("2", "2", imageVector = R.drawable.ic_launcher_foreground),
    Data(name = "1", publisher = "1", imageVector = R.drawable.ic_launcher_background),
    Data("2", "2", imageVector = R.drawable.ic_launcher_foreground),
)