package com.cyberkaidev.notes.viewmodel

import androidx.lifecycle.ViewModel
import com.cyberkaidev.notes.model.NoteModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<ArrayList<NoteModel>>(arrayListOf())
    val notes = _notes.asStateFlow()

    fun add(note: NoteModel) {
        notes.value.add(0, note)
    }
}