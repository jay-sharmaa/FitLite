package com.example.uitutorial

import androidx.compose.runtime.mutableStateListOf

data class Data(
    val name: String,
    val publisher: String,
    val imageFileName: String = "",
    val steps: MutableList<String> = mutableListOf(),
    val id: Int,
    val obj: String
)

data class Diet(
    val dietName: String,
    val dietaryType: String,
    val imageFileName: String = ""
)

val dietList: List<Diet> = listOf(
    Diet(dietName = "Eggs", dietaryType = "Keto", imageFileName = "eggs.png"),
    Diet(dietName = "Fish", dietaryType = "Not Keto", imageFileName = "fish.png"),
    Diet(dietName = "Fruit", dietaryType = "Keto", imageFileName = "fruitSalad.png"),
    Diet(dietName = "Salad", dietaryType = "Not Keto", imageFileName = "salad.png"),
)

val myList = mutableStateListOf(
    Data(
        name = "Hand Stand Push-Up",
        publisher = "Hard",
        imageFileName = "images/handStandPushUp.png",
        steps = mutableListOf("Start in handstand", "Lower yourself", "Push back up"),
        id = 0,
        obj = "model.glb"
    ),
    Data(
        name = "Push-Up",
        publisher = "Easy",
        imageFileName = "images/push_up.png",
        steps = mutableListOf("Get into plank position", "Lower body", "Push back up"),
        id = 1,
        obj = "model.glb"
    ),
    Data(
        name = "Squads",
        publisher = "Easy",
        imageFileName = "images/squads.png",
        steps = mutableListOf("Stand straight", "Lower into squat", "Stand up"),
        id = 2,
        obj = "model.glb"
    ),
    Data(
        name = "Stretching",
        publisher = "Easy",
        imageFileName = "images/stretching.png",
        steps = mutableListOf("Reach arms up", "Bend forward", "Hold for 10 seconds"),
        id = 3,
        obj = "model.glb"
    )
)

fun addExercise(
    name: String,
    publisher: String,
    imageFileName: String = "",
    steps: MutableList<String> = mutableListOf(),
    obj: String = "model.glb"
) {
    val newId = myList.maxOf { it.id } + 1

    val newExercise = Data(
        name = name,
        publisher = publisher,
        imageFileName = imageFileName,
        steps = steps,
        id = newId,
        obj = obj
    )

    myList.add(newExercise)
}

