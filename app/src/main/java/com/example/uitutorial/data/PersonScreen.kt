package com.example.uitutorial.data


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun PersonScreen(viewModel: PersonViewModel) {
    var name by remember {
        mutableStateOf("")
    }
    val persons by viewModel.allPersons.collectAsState(initial = emptyList())
    val coroutine = rememberCoroutineScope()
    var personName : String? = ""
    var personPassword : String? = ""
    val person = Person(name = "", age = 0, plan = 0, complete = 0, password = "", days = emptyList(), gender = 'M')

    Column {
        TextField(value = name, onValueChange = {
            name = it
        })
        Button(onClick = {
            coroutine.launch {
                personName = viewModel.getPersonByName(name)
                    .map { it?.name }
                    .firstOrNull()
                personPassword = viewModel.getPersonByName(name)
                    .map{ it?.password }
                    .firstOrNull()
            }
            if(personName == null){
                val newPerson = Person(name, "12345678",25, 'F', 1, 0, emptyList())
                viewModel.insert(newPerson)
            }
            else{
                val newPerson = Person(name, "87654321", person.age, 'p', person.plan, person.complete, person.days)
                viewModel.update(newPerson)
            }
        }) {
            Text("Insert Person")
        }
        LazyColumn {
            items(persons) { person ->
                Text(text = "Name: ${person.name}, Age: ${person.age}, Gender: ${person.gender}")
            }
        }
    }
}

