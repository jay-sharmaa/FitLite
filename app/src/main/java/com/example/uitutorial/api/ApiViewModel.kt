package com.example.uitutorial.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel : ViewModel(){
    private val _posts = MutableStateFlow<List<Post>>(emptyList())

    val posts: StateFlow<List<Post>> = _posts

    init{
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try{
                val response = RetrofitInstance.api.getPosts()
                _posts.value = response
            } catch(e: Exception){

            }
        }
    }
}