package com.cyberkaidev.notes.model

import androidx.navigation.NavHostController

data class HomePageModel (
    val onNavigate: NavHostController,
    val notes: ArrayList<NoteModel>
)
