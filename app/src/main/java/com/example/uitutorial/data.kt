package com.example.uitutorial

data class Data(
    val name: String,
    val publisher: String,
    val imageFileName: String = ""
)

data class Diet(
    val dietName: String,
    val dietaryType: String,
    val imageFileName: String = ""
)

val dietList: List<Diet> = listOf(
    Diet(dietName = "Eggs", dietaryType = "Keto", imageFileName = "images/eggs.png"),
    Diet(dietName = "Fish and Fries", dietaryType = "Not Keto", imageFileName = "images/fish.png"),
    Diet(dietName = "Fruit Salad", dietaryType = "Keto", imageFileName = "images/fruitSalad.png"),
    Diet(dietName = "Salad", dietaryType = "Not Keto", imageFileName = "images/salad.png"),
)

val myList: List<Data> = listOf(
    Data(name = "Hand Stand Push-Up", publisher = "Hard", imageFileName = "images/handStandPushUp.png"),
    Data(name = "Push-Up", publisher = "Easy", imageFileName = "images/push_up.png"),
    Data(name = "Squads", publisher = "Easy", imageFileName = "images/squads.png"),
    Data(name = "Stretching", publisher = "Easy", imageFileName = "images/stretching.png"),
)
