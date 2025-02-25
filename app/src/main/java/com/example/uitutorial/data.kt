package com.example.uitutorial

data class Data(
    val name: String,
    val publisher: String,
    val imageVector: Int
)

data class Diet(
    val dietName: String,
    val dietaryType: String,
    val imageVector: Int
)

val dietList: List<Diet> = listOf(
    Diet(dietName = "Salad", dietaryType = "Keto", imageVector = R.drawable.ic_launcher_background),
    Diet(dietName = "Not Salad", dietaryType = "Not Keto", imageVector = R.drawable.ic_launcher_foreground),
    Diet(dietName = "Salad", dietaryType = "Keto", imageVector = R.drawable.ic_launcher_background),
    Diet(dietName = "Not Salad", dietaryType = "Not Keto", imageVector = R.drawable.ic_launcher_foreground),
)

val myList: List<Data> = listOf(
    Data(name = "1", publisher = "1", imageVector = R.drawable.ic_launcher_background),
    Data("2", "2", imageVector = R.drawable.ic_launcher_foreground),
    Data(name = "1", publisher = "1", imageVector = R.drawable.ic_launcher_background),
    Data("2", "2", imageVector = R.drawable.ic_launcher_foreground),
)

